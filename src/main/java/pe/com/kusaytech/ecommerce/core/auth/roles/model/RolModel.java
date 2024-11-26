package pe.com.kusaytech.ecommerce.core.auth.roles.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;

import java.time.LocalDateTime;
import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_roles")
public class RolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_role")
    private Long idRole;

    @NotBlank(message = "El nombre del rol es obligatorio.")
    @Column(name = "c_role_name", nullable = false, unique = true)
    private String roleName;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    private Set<UserModel> users;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "d_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "c_status", nullable = false)
    private String status;

    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }
}