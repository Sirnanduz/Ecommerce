package pe.com.kusaytech.ecommerce.core.auth.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;

@Repository
public interface RolRepository extends JpaRepository<RolModel, Long> {
    RolModel findByRoleName(String roleName);
}