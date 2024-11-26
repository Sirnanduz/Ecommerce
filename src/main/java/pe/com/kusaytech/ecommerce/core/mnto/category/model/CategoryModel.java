package pe.com.kusaytech.ecommerce.core.mnto.category.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_category")
public class CategoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_category")
    private Long idCategory;

    @NotBlank(message = "El nombre de la categoría es obligatorio.")
    @Column(name = "c_category_name", nullable = false)
    private String categoryName;

    @NotBlank(message = "La descripción de la categoría es obligatorio.")
    @Column(name = "c_description", nullable = false)
    private String description;

    @NotBlank(message = "La imagen es obligatorio.")
    @Column(name = "c_image_url", nullable = false)
    private String imageUrl;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime created;

    @Column(name = "d_updated_at", nullable = false)
    private LocalDateTime updated;

    @Column(name = "c_status", nullable = false)
    private String status;

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
