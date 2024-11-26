package pe.com.kusaytech.ecommerce.core.tracking.order.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.service.OrderService;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto.RequestOrderItemDTO;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

@RestController
@RequestMapping(Apis.TRACKING_API + "/order")
@Validated
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderdetailsService;

    @PostMapping("/add-cart")
    public ResponseEntity<ResponseDTO<OrderModel>> addToCart(
            @RequestBody List<RequestOrderItemDTO> items) {

        try {
            UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = user.getId();
            logger.info("User ID obtained from security context: {}", userId);

            OrderModel createdOrder = orderdetailsService.addToCart(userId, items);
            logger.info("Order created successfully for user ID: {}", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Items added to cart", createdOrder));
        } catch (ValidationException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }
    @GetMapping("/cart")
    public ResponseEntity<ResponseDTO<List<OrderModel>>> getActiveCart() {
        try {
            UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = user.getId();
            logger.info("User ID obtained from security context: {}", userId);

            List<OrderModel> cart = orderdetailsService.getActiveCartByUserId(userId);
            logger.info("Cart retrieved successfully for user ID: {}", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Cart retrieved successfully", cart));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ResponseDTO<List<OrderModel>>> getOrders() {
        try {
            UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = user.getId();
            logger.info("User ID obtained from security context: {}", userId);

            List<OrderModel> orders = orderdetailsService.getOrdersByUserId(userId);
            logger.info("Orders retrieved successfully for user ID: {}", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }

    @PatchMapping("/update-status/{orderId}")
    public ResponseEntity<ResponseDTO<Void>> updateOrderStatus(@PathVariable Long orderId) {
        try {
        	
            orderdetailsService.updateOrderStatus(orderId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "La orden se actualizó correctamente", null));
        } catch (RuntimeException ex) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, ex.getMessage(), null));
        }
    }
    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<ResponseDTO<Void>> deleteItemFromOrder(@PathVariable Long itemId) {
        try {
            orderdetailsService.deleteItemFromOrder(itemId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Item deleted successfully", null));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }

}