package pe.com.kusaytech.ecommerce.core.auth.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.kusaytech.ecommerce.core.auth.address.model.UserAddressModel;

public interface UserAddressRepository extends JpaRepository<UserAddressModel, Long> {
}
