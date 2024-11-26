package pe.com.kusaytech.ecommerce.core.mnto.products.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;
import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_product")
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_product")
    private Long idProduct;

    @Column(name = "c_name", nullable = false)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @Column(name = "c_description", nullable = false)
    @NotBlank(message = "El campo descripción es obligatorio")
    private String description;

    @Column(name = "n_price", nullable = false)
    private Double price;

    @Column(name = "c_sku", nullable = false)
    private String sku;

    @Column(name = "n_stock", nullable = false)
    private int stock;

    @ManyToOne
    @JoinColumn(name = "n_id_brand", nullable = false)
    private BrandModel brand;

    @Column(name = "c_color", nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "n_id_category", nullable = false)
    private CategoryModel category;

    @ManyToOne
    @JoinColumn(name = "n_id_discount", nullable = false)
    private DiscountModel discount;

    @Column(name = "d_created_at", nullable = false)
    private LocalDateTime created;

    @Column(name = "d_updated_at", nullable = false)
    private LocalDateTime updated;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ImagesModel> imagenes;

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