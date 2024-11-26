package pe.com.kusaytech.ecommerce.core.store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;
import pe.com.kusaytech.ecommerce.core.mnto.discount.service.DiscountService;
import pe.com.kusaytech.ecommerce.core.store.controller.dto.ResponseDTO;

@RestController
@RequestMapping(Apis.BUSINESS_API + "/discounts")
@Validated
public class StoreDiscountController {

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