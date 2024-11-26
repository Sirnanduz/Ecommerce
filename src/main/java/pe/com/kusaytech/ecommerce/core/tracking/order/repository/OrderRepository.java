package pe.com.kusaytech.ecommerce.core.tracking.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    @Query("SELECT o FROM OrderModel o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.userId = :userId AND o.orderStatus = 'W'")
    List<OrderModel> findActiveCartByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM OrderModel o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.userId = :userId ")
    List<OrderModel> findOrdersByUserId(@Param("userId") Long userId);
    
    boolean existsByIdAndOrderStatus(Long orderId, String status);
}