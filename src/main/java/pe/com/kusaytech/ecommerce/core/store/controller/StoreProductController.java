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
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.service.ProductService;
import pe.com.kusaytech.ecommerce.core.store.controller.dto.ResponseDTO;

@RestController
@RequestMapping(Apis.BUSINESS_API + "/products")
@Validated
public class StoreProductController {

    @Autowired
    private ProductService productService;
    
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<ProductModel>>> getAllProducts() {
        List<ProductModel> products = productService.getAllProduct();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Productos encontrados", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductModel>> getProductById(@PathVariable Long id) {
        Optional<ProductModel> product = productService.getProductById(id);
        return product.map(productModel -> ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Producto encontrado", productModel)))
                .orElseGet(() -> {
                    String errorMessage = String.format("No existe un producto con el ID: %d", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), errorMessage, null));
                });
    }
  
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Error: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append(".")
        );
        return ResponseEntity.badRequest()
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString().trim(), null));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO<String>> handleRuntimeException(RuntimeException ex) {
    	StringBuilder errorMessage = new StringBuilder("Error: ");
    	errorMessage.append(ex.getLocalizedMessage()).append(".");
    	return ResponseEntity.badRequest()
    			.body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString().trim(), null));
    }

}
