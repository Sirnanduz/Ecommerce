package pe.com.kusaytech.ecommerce.core.tracking.order_item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.repository.ProductRepository;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.repository.OrderRepository;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto.RequestOrderItemDTO;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.model.OrderitemModel;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.repository.OrderItemRepository;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderdetailsRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderitemModel addItemToOrder(Long orderId, RequestOrderItemDTO orderItemRequest) throws ValidationException {
        OrderModel order = orderdetailsRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found"));

        Optional<OrderitemModel> existingItemOpt = order.getOrderItems().stream()
                .filter(oi -> oi.getProduct().getIdProduct().equals(orderItemRequest.getProductId()))
                .findFirst();

        OrderitemModel orderItem;
        if (existingItemOpt.isPresent()) {
            // El producto ya está en el carrito, actualizar la cantidad
            orderItem = existingItemOpt.get();
            orderItem.setQuantity(orderItem.getQuantity() + orderItemRequest.getQuantity());
            orderItem.setUpdatedAt(LocalDateTime.now());
        } else {
            // El producto no está en el carrito, añadirlo como un nuevo ítem
            orderItem = new OrderitemModel();
            orderItem.setOrder(order);
            ProductModel product = productRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new ValidationException("Product not found"));
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setUnitPrice(orderItemRequest.getPrice());
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setStatus("ACTIVE");
            order.getOrderItems().add(orderItem);
        }

        double total = order.getOrderItems().stream()
                .mapToDouble(oi -> oi.getQuantity() * oi.getUnitPrice())
                .sum();
        order.setTotal(total);

        orderItemRepository.save(orderItem);
        orderdetailsRepository.save(order);
        return orderItem;
    }

    public List<OrderitemModel> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void deleteItemFromOrder(Long itemId) throws ValidationException {
        OrderitemModel orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException("Item not found"));
        orderItemRepository.delete(orderItem);
    }
}