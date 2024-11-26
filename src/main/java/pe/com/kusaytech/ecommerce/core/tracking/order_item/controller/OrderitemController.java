package pe.com.kusaytech.ecommerce.core.tracking.order_item.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.tracking.order.service.OrderService;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto.RequestOrderItemDTO;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.model.OrderitemModel;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.service.OrderItemService;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

@RestController
@RequestMapping(Apis.TRACKING_API + "/orderItem")
@Validated
public class OrderitemController {
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private OrderService orderService;

    @PostMapping("/add-item")
    public ResponseEntity<ResponseDTO<OrderitemModel>> addItemToOrder(
            @RequestParam Long orderId,
            @RequestBody RequestOrderItemDTO orderItemRequest) {
        try {
            if(!orderService.existsOrderByIdAndOrderStatus(orderId, "W")) {
            	throw new ValidationException("La compra ya esta en proceso o esta finalizada");
            }
            OrderitemModel addedItem = orderItemService.addItemToOrder(orderId, orderItemRequest);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Item added to order successfully", addedItem));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseDTO<List<OrderitemModel>>> getItemsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderitemModel> items = orderItemService.getItemsByOrderId(orderId);
            return ResponseEntity.ok(new ResponseDTO<>(200, "Items retrieved successfully", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(500, "Server error", null));
        }
    }
}

