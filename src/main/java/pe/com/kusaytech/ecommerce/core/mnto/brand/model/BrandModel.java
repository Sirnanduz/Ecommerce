package pe.com.kusaytech.ecommerce.core.mnto.brand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_brand")
public class BrandModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "n_id_brand")
	private Long brandId;

	@NotBlank(message = "El campo descripcion es obligatorio")
	@Column(name = "c_description")
	private String description;

	@Column(name = "c_state")
	private String state;
}
