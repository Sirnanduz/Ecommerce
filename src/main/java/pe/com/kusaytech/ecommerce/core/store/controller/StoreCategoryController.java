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
import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.category.service.CategoryService;
import pe.com.kusaytech.ecommerce.core.store.controller.dto.ResponseDTO;

@RestController
@RequestMapping(Apis.BUSINESS_API + "/categories")
@Validated
public class StoreCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<CategoryModel>>> getAllCategories() {
        List<CategoryModel> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Categorías encontradas", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<CategoryModel>> getCategoryById(@PathVariable Long id) {
        Optional<CategoryModel> category = categoryService.getCategoryById(id);
        return category.map(cat -> ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Categoría encontrada", cat)))
                .orElseGet(() -> {
                    String errorMessage = String.format("No existe una categoría con el ID: %d", id);
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
