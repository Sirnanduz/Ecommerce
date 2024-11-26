package pe.com.kusaytech.ecommerce.core.mnto.discount.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_discount")
public class DiscountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_discount")
    private Long idDiscount;

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "n_discount_percentage", nullable = false)
    private Long discountPercentage;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime created;

    @Column(name = "d_updated_at", nullable = false)
    private LocalDateTime updated;

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = LocalDateTime.now();
    }

}
