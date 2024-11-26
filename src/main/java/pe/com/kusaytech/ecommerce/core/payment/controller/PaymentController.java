package pe.com.kusaytech.ecommerce.core.payment.controller;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.annotation.PostConstruct;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.auth.user.controller.UserController;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;
import pe.com.kusaytech.ecommerce.core.payment.model.PaymentModel;
import pe.com.kusaytech.ecommerce.core.payment.service.PaymentService;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.service.OrderService;

@Controller
@RequestMapping(Apis.PAYMENT_API)
@Validated
public class PaymentController {

	private static final Logger LOGGER = LogManager.getLogger(UserController.class);
	
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;
    
    private UserModel user;
    
    @PostConstruct
    public void init() {
    	user = new UserModel();
    } 

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createPayment() {
        try {
            user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<OrderModel> activeCarts = orderService.getActiveCartByUserId(user.getId());
            if (activeCarts.isEmpty()) {
                throw new RuntimeException("No active cart found for the user");
            }
            OrderModel order= activeCarts.get(0);
            double total = order.getTotal();

            PaymentModel paymentModel = new PaymentModel();
            paymentModel.setOrderDetail(order);
            paymentModel.setN_amount(total);
            paymentModel.setC_provider("paypal");
            paymentModel.setC_payment_status("P");
            paymentModel.setC_status("A");
                               
            Payment payment = paymentService.createPayment(paymentModel);
            
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(links.getHref());
                }
            }
            
        } catch (PayPalRESTException e) {
            LOGGER.log(Level.INFO, "ERROR : {0}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el pago");
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,@RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
        if (paymentId == null || payerId == null || token == null) {
            return "paymentError"; 
        }
                
        List<OrderModel> activeCarts = orderService.getActiveCartByUserId(user.getId());
        if (activeCarts.isEmpty()) {
            throw new RuntimeException("No active cart found for the user");
        }
        OrderModel order= activeCarts.get(0);
        double total = order.getTotal();
        
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setOrderDetail(order);
        paymentModel.setN_amount(total);
        paymentModel.setC_provider("paypal");
        paymentModel.setC_payment_status("P");
        paymentModel.setC_status("A");

        paymentService.savePayment(paymentModel);

        order.setOrderStatus("P");
        OrderModel orderModel = orderService.saveOrder(order);
        
        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                orderModel.setOrderStatus("T");
                orderService.saveOrder(orderModel);
                return "paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "paymentError"; 
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }

    @GetMapping("/error")
    public String paymentError() {
        return "paymentError";
    }
}
