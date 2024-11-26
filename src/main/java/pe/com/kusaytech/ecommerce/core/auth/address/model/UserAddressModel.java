package pe.com.kusaytech.ecommerce.core.auth.address.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_user_address")
public class UserAddressModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_address")
    private Long idAddress;

    @ManyToOne
    @JoinColumn(name = "n_id_user", nullable = false)
    private UserModel user;

    @Column(name = "c_address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "c_address_line2")
    private String addressLine2;

    @Column(name = "c_city", nullable = false)
    private String city;

    @Column(name = "c_postal_code", nullable = false)
    private String postalCode;

    @Column(name = "c_state", nullable = false)
    private String state;

    @Column(name = "c_country", nullable = false)
    private String country;

    @Column(name = "c_phone", nullable = false)
    private String phone;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "d_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "c_status", nullable = false)
    private String status;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setIdUser(UserModel user) {
        this.user = user;
    }
}