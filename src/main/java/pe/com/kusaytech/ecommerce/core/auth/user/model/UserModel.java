package pe.com.kusaytech.ecommerce.core.auth.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_user_account")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_user")
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Column(name = "c_username", nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Column(name = "c_password", nullable = false)
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Debe ser un correo electrónico válido.")
    @Column(name = "c_email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El nombre es obligatorio.")
    @Column(name = "c_first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio.")
    @Column(name = "c_last_name", nullable = false)
    private String lastName;

    @Column(name = "c_phone")
    private String phone;

    @Column(name = "c_document_type")
    private String documentType;

    @Column(name = "c_document_number")
    private String documentNumber;

    @Column(name = "c_status", nullable = false)
    private String status;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "d_updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "n_id_role", nullable = false)
    private RolModel role;
}