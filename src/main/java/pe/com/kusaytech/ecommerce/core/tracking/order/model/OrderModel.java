package pe.com.kusaytech.ecommerce.core.tracking.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.tracking.order_item.model.OrderitemModel;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_order")
    private Long id;

    @Column(name = "n_id_user", nullable = false)
    private Long userId;

    @Column(name = "n_total", nullable = false)
    private double total;
    

    @Column(name = "c_order_status", nullable = false)
    private String orderStatus;

    @Column(name = "d_order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "d_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "c_status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderitemModel> orderItems;


    
}