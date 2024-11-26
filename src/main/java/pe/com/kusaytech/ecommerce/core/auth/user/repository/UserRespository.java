package pe.com.kusaytech.ecommerce.core.auth.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;

public interface UserRespository extends JpaRepository<UserModel, Long> {
    UserModel findByEmail(String email);
    boolean existsByEmail(String email);
}