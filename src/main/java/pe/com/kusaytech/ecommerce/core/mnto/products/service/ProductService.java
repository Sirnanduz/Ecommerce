package pe.com.kusaytech.ecommerce.core.mnto.products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public List<ProductModel> getAllProduct() {
		return productRepository.findAll();
	}

	public Optional<ProductModel> getProductById(Long id) {		
		return productRepository.findById(id);
	}
	public boolean existsByNameOrSku(String name,String sku) {		
		return productRepository.existsByNameOrSku(name,sku);
	}

	public ProductModel saveProduct(ProductModel productModel) {
		return productRepository.save(productModel);
	}

	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}

	 public List<ProductModel> getProductsByCategory(CategoryModel category) {
        return productRepository.findByCategory(category);
    }


}