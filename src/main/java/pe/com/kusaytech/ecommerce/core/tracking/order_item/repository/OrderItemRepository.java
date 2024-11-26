package pe.com.kusaytech.ecommerce.core.tracking.order_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.model.OrderitemModel;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderitemModel, Long> {
    List<OrderitemModel> findByOrderId(Long orderId);
}