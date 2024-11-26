package pe.com.kusaytech.ecommerce.core.auth.roles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;
import pe.com.kusaytech.ecommerce.core.auth.roles.repository.RolRepository;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public List<RolModel> getAllRoles() {
        return rolRepository.findAll();
    }

    public Optional<RolModel> getRoleById(Long id) {
        return rolRepository.findById(id);
    }

    public RolModel saveRole(RolModel rolModel) {
        return rolRepository.save(rolModel);
    }

    public void deleteRole(Long id) {
        rolRepository.deleteById(id);
    }

    @PostConstruct
    public void initializeDefaultRoles() {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");
    }

    private void createRoleIfNotFound(String roleName) {
        if (rolRepository.findByRoleName(roleName) == null) {
            RolModel role = new RolModel();
            role.setRoleName(roleName);
            role.setCreatedAt(LocalDateTime.now());
            role.setStatus("ACTIVE");
            rolRepository.save(role);
        }
    }
}
