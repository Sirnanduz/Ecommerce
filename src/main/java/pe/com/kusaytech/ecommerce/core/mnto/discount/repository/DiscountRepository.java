package pe.com.kusaytech.ecommerce.core.mnto.discount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;

public interface DiscountRepository extends JpaRepository<DiscountModel, Long> {
}
