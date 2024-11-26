package pe.com.kusaytech.ecommerce.core.auth.address.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.auth.address.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.auth.address.model.UserAddressModel;
import pe.com.kusaytech.ecommerce.core.auth.address.service.UserAddressService;
import pe.com.kusaytech.ecommerce.core.auth.address.utils.Constants;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;
import pe.com.kusaytech.ecommerce.core.auth.user.service.UserService;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;

@RestController
@RequestMapping(Apis.SECURITY_API + "/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserAddressModel>>> getAllAddresses() {
        try {
            List<UserAddressModel> addresses = userAddressService.getAllAddresses();
            return ResponseEntity.ok(new ResponseDTO<>(200, "Found all addresses", addresses));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(401, "Unauthorized access", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserAddressModel>> getAddressById(@PathVariable Long id) {
        try {
            Optional<UserAddressModel> address = userAddressService.getAddressById(id);
            return address.map(value -> ResponseEntity.ok(new ResponseDTO<>(200, "Found the address", value)))
                    .orElseThrow(() -> new ValidationException("Address not found with id: " + id));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(401, "Unauthorized access", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<UserAddressModel>> createAddress(
            @RequestParam Long idUser,
            @RequestBody UserAddressModel userAddressModel) {
        try {
            if (idUser == null) {
                throw new ValidationException("idUser parameter is required");
            }
            Optional<UserModel> user = userService.getUserById(idUser);
            if (!user.isPresent()) {
                throw new ValidationException("User not found with id: " + idUser);
            }
            userAddressModel.setIdUser(user.get());
            userAddressModel.setCreatedAt(LocalDateTime.now());
            userAddressModel.setStatus("ACTIVE");
            UserAddressModel savedAddress = userAddressService.saveAddress(userAddressModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(201, "Address created successfully", savedAddress));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, e.getMessage(), null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(401, "Unauthorized access", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserAddressModel>> updateAddress(@PathVariable Long id, @RequestBody UserAddressModel updatedAddress) {
        try {
            Optional<UserAddressModel> existingAddress = userAddressService.getAddressById(id);
            if (existingAddress.isPresent()) {
                updatedAddress.setIdAddress(id);
                updatedAddress.setUpdatedAt(LocalDateTime.now());
                UserAddressModel savedAddress = userAddressService.saveAddress(updatedAddress);
                return ResponseEntity.ok(new ResponseDTO<>(200, "Address updated successfully", savedAddress));
            } else {
                throw new ValidationException("Address not found with id: " + id);
            }
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(401, "Unauthorized access", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteAddress(@PathVariable Long id) {
        try {
            Optional<UserAddressModel> existingAddress = userAddressService.getAddressById(id);
            if (existingAddress.isPresent()) {
                userAddressService.deleteAddress(id);
                return ResponseEntity.noContent().build();
            } else {
                throw new ValidationException("Address not found with id: " + id);
            }
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(401, "Unauthorized access", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, Constants.ERROR_SERVIDOR, null));
        }
    }
}