package pe.com.kusaytech.ecommerce.core.auth.roles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.auth.roles.utils.Constants;
import pe.com.kusaytech.ecommerce.core.auth.roles.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;
import pe.com.kusaytech.ecommerce.core.auth.roles.service.RolService;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Apis.SECURITY_API + "/roles")
public class RolController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<RolModel>>> getAllRoles() {
        try {
            List<RolModel> roles = rolService.getAllRoles();
            if (roles.isEmpty()) {
                LOGGER.warn("Error 204: No roles found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDTO<>(204, "No roles found", null));
            }

            LOGGER.info("Success 200: Found all roles");
            return ResponseEntity.ok(new ResponseDTO<>(200, "Found all roles", roles));
        } catch (Exception e) {
            LOGGER.error("Error 500: " + Constants.ERROR_SERVIDOR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<RolModel>> getRoleById(@PathVariable Long id) {
        try {
            Optional<RolModel> role = rolService.getRoleById(id);
            return role.map(value -> {
                LOGGER.info("Success 200: Found the role");
                return ResponseEntity.ok(new ResponseDTO<>(200, "Found the role", value));
            }).orElseThrow(() -> new ValidationException("Role not found with id: " + id));
        } catch (ValidationException e) {
            LOGGER.warn("Error 404: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error("Error 500: " + Constants.ERROR_SERVIDOR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<RolModel>> createRole(@RequestBody RolModel rolModel) {
        try {
            rolModel.setCreatedAt(LocalDateTime.now());
            rolModel.setStatus("ACTIVE");
            RolModel savedRole = rolService.saveRole(rolModel);
            LOGGER.info("Success 201: Role created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(201, "Role created successfully", savedRole));
        } catch (Exception e) {
            LOGGER.error("Error 500: " + Constants.ERROR_SERVIDOR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<RolModel>> updateRole(@PathVariable Long id, @RequestBody RolModel updatedRole) {
        try {
            Optional<RolModel> existingRole = rolService.getRoleById(id);
            if (existingRole.isPresent()) {
                updatedRole.setIdRole(id);
                updatedRole.setUpdatedAt(LocalDateTime.now());
                RolModel savedRole = rolService.saveRole(updatedRole);
                LOGGER.info("Success 200: Role updated successfully");
                return ResponseEntity.ok(new ResponseDTO<>(200, "Role updated successfully", savedRole));
            } else {
                throw new ValidationException("Role not found with id: " + id);
            }
        } catch (ValidationException e) {
            LOGGER.warn("Error 404: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error("Error 500: " + Constants.ERROR_SERVIDOR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Object>> deleteRole(@PathVariable Long id) {
        try {
            Optional<RolModel> existingRole = rolService.getRoleById(id);
            if (existingRole.isPresent()) {
                rolService.deleteRole(id);
                LOGGER.info("Success 200: Role deleted successfully");
                return ResponseEntity.ok(new ResponseDTO<>(200, "Role deleted successfully", null));
            } else {
                throw new ValidationException("Role not found with id: " + id);
            }
        } catch (ValidationException e) {
            LOGGER.warn("Error 404: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error("Error 500: " + Constants.ERROR_SERVIDOR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }
}