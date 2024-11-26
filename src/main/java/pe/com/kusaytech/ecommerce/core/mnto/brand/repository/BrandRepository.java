package pe.com.kusaytech.ecommerce.core.mnto.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;

@Repository
public interface BrandRepository extends JpaRepository<BrandModel, Long>{

}
