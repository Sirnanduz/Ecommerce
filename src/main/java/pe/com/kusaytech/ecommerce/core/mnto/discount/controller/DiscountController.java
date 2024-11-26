package pe.com.kusaytech.ecommerce.core.mnto.discount.controller;

import jakarta.validation.Valid;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.mnto.discount.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;
import pe.com.kusaytech.ecommerce.core.mnto.discount.service.DiscountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Apis.ADMIN_API + "/discounts")
@Validated
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<DiscountModel>>> getAllDiscounts() {
        List<DiscountModel> discounts = discountService.getAllDiscount();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Descuentos encontrados", discounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<DiscountModel>> getDiscountById(@PathVariable Long id) {
        Optional<DiscountModel> discount = discountService.getDiscountById(id);
        return discount.map(discountModel -> ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Descuento encontrado", discountModel)))
                .orElseGet(() -> {
                    String errorMessage = String.format("No existe un descuento con el ID: %d", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), errorMessage, null));
                });
    }

    @PostMapping(path = "/save")
    public ResponseEntity<ResponseDTO<DiscountModel>> createDiscount(@Valid @RequestBody DiscountModel discountModel) {
        DiscountModel createdDiscount = discountService.saveDiscount(discountModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(HttpStatus.CREATED.value(), "Descuento creado", createdDiscount));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO<DiscountModel>> updateDiscount(@PathVariable Long id, @Valid @RequestBody DiscountModel updatedDiscount) {
        Optional<DiscountModel> existingDiscount = discountService.getDiscountById(id);
        if (existingDiscount.isPresent()) {
            updatedDiscount.setIdDiscount(id);
            updatedDiscount.setCreated(existingDiscount.get().getCreated());
            updatedDiscount.setUpdated(LocalDateTime.now());
            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Descuento actualizado", discountService.saveDiscount(updatedDiscount)));
        } else {
            String errorMessage = String.format("No existe un descuento con el ID: %d", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), errorMessage, null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Descuento eliminado", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Error: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append(". ")
        );
        return ResponseEntity.badRequest()
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString().trim(), null));
    }
}