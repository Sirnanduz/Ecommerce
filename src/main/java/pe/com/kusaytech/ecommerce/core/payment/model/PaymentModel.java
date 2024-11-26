package pe.com.kusaytech.ecommerce.core.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.tracking.order.model.OrderModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_payment")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_payment")
    private Long idPayment;

    @ManyToOne
    @JoinColumn(name = "n_id_order", nullable = false)
    private OrderModel order;

    @Column(name = "n_amount", nullable = false)
    private Double n_amount;

    @Column(name = "c_provider", nullable = false)
    private String c_provider;

    @Column(name = "c_payment_status", nullable = false)
    private String c_payment_status;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime created;

    @Column(name = "d_updated_at", nullable = false)
    private LocalDateTime updated;

    @Column(name = "c_status", nullable = false)
    private String c_status;

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = LocalDateTime.now();
    }

    public void setOrderDetail(OrderModel order) {
        this.order = order;
    }
}