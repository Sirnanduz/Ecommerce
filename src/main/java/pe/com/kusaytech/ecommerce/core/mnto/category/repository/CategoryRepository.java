package pe.com.kusaytech.ecommerce.core.mnto.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {
}
