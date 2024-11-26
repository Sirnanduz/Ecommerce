package pe.com.kusaytech.ecommerce.core.mnto.products.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_images")
public class ImagesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "n_orden")
    private Integer order;

    @Column(name = "c_filename")
    private String filename;

    @ManyToOne
    @JoinColumn(name = "n_id_product", nullable = false)
    @JsonBackReference
    private ProductModel product;
}
