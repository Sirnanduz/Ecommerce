package pe.com.kusaytech.ecommerce.core.tracking.order_item.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order_item")
public class OrderitemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_order_item")
    private Long idOrderItem;

    @ManyToOne
    @JoinColumn(name = "n_id_order", nullable = false)

    @JsonBackReference
    private OrderModel order;

    @ManyToOne
    @JoinColumn(name = "n_id_product", nullable = false)
    private ProductModel product;

    @Column(name = "n_quantity", nullable = false)
    private int quantity;

    @Column(name = "n_unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "d_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "c_status", nullable = false)
    private String status;
}

