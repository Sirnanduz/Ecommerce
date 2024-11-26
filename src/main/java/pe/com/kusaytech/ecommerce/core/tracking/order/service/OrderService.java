package pe.com.kusaytech.ecommerce.core.tracking.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.repository.ProductRepository;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.repository.OrderRepository;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto.RequestOrderItemDTO;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.model.OrderitemModel;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.repository.OrderItemRepository;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderdetailsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderModel addToCart(Long userId, List<RequestOrderItemDTO> items) throws ValidationException {
        logger.info("Adding items to cart for user ID: {}", userId);

        List<OrderModel> activeCarts = orderdetailsRepository.findActiveCartByUserId(userId);
        OrderModel order;
        if (activeCarts.isEmpty()) {
            order = new OrderModel();
            order.setUserId(userId);
            order.setOrderStatus("W");
            order.setOrderDate(LocalDateTime.now());
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("A");
            order.setOrderItems(new ArrayList<>());
            order.setTotal(0);
        } else {
            order = activeCarts.get(0);
        }

        double total = order.getTotal();
        for (RequestOrderItemDTO item : items) {
            Optional<OrderitemModel> existingItemOpt = order.getOrderItems().stream()
                    .filter(oi -> oi.getProduct().getIdProduct().equals(item.getProductId()))
                    .findFirst();
            if (existingItemOpt.isPresent()) {
                OrderitemModel existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                total += item.getQuantity() * item.getPrice();
            } else {
                OrderitemModel orderItem = new OrderitemModel();
                orderItem.setOrder(order);
                ProductModel product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ValidationException("Product not found"));
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setUnitPrice(item.getPrice());
                orderItem.setCreatedAt(LocalDateTime.now());
                orderItem.setStatus("A");
                total += item.getQuantity() * item.getPrice();
                order.getOrderItems().add(orderItem);
            }
        }
        order.setTotal(total);

        orderdetailsRepository.save(order);
        logger.info("Order saved successfully for user ID: {}", userId);
        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getActiveCartByUserId(Long userId) {
        logger.info("Fetching active cart for user ID: {}", userId);
        List<OrderModel> activeCarts = orderdetailsRepository.findActiveCartByUserId(userId);
        for (OrderModel order : activeCarts) {
            order.getOrderItems().size();
        }
        return activeCarts;
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user ID: {}", userId);
        List<OrderModel> orders = orderdetailsRepository.findOrdersByUserId(userId);
        for (OrderModel order : orders) {
            order.getOrderItems().size();
        }
        return orders;
    }

    public Optional<OrderModel> getOrderDetailById(Long orderId) {
        return orderdetailsRepository.findById(orderId);
    }

    public OrderModel updateOrderStatus(Long orderId, String newStatus) throws ValidationException {
        OrderModel order = orderdetailsRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found"));

        switch (newStatus.toLowerCase()) {
            case "w":
                order.setOrderStatus("W"); // W = Waiting
                break;
            case "p":
                order.setOrderStatus("P"); // P = Payment
                break;
            case "f":
                order.setOrderStatus("T"); // T = Terminado
                break;
            default:
                throw new ValidationException("Invalid status code");
        }

        order.setUpdatedAt(LocalDateTime.now());
        orderdetailsRepository.save(order);
        logger.info("Order status updated successfully for order ID: {}", orderId);
        return order;
    }

    public OrderModel saveOrder(OrderModel orderDetail) {
        return orderdetailsRepository.save(orderDetail);
    }

    public void updateOrderStatus(Long orderId) {
        OrderModel orderDetails = orderdetailsRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        String currentStatus = orderDetails.getOrderStatus();

        if ("F".equals(currentStatus)) {
            throw new RuntimeException("No se puede cambiar el estado porque ya está finalizado");
        }

        // Finalizado
        if ("E".equals(currentStatus)) {
            orderDetails.setOrderStatus("F");
        } else {
            // Enviado
            orderDetails.setOrderStatus("E");
        }

        orderdetailsRepository.save(orderDetails);
    }

    @Transactional
    public void deleteItemFromOrder(Long itemId) throws ValidationException {
        OrderitemModel orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException("Item not found"));
        
        if(!existsOrderByIdAndOrderStatus(orderItem.getOrder().getId(), "W")) {
        	throw new ValidationException("La compra ya esta en proceso o esta finalizada");
        }
        
        OrderModel order = orderItem.getOrder();
        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);

        double total = order.getOrderItems().stream()
                .mapToDouble(oi -> oi.getQuantity() * oi.getUnitPrice())
                .sum();
        order.setTotal(total);

        orderdetailsRepository.save(order);
        
        logger.info("Item deleted and order total updated successfully for order ID: {}", order.getId());
    }
    
    public boolean existsOrderByIdAndOrderStatus(Long orderId,String status) {
    	return orderdetailsRepository.existsByIdAndOrderStatus(orderId, status);
    }
}