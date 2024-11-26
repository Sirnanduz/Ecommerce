package pe.com.kusaytech.ecommerce.core.mnto.products.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
	public boolean existsByNameOrSku(String name,String sku);
	List<ProductModel> findByCategory(CategoryModel category);
}
