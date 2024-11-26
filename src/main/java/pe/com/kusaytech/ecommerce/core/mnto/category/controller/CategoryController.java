package pe.com.kusaytech.ecommerce.core.mnto.category.controller;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.mnto.category.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.category.service.CategoryService;
import jakarta.validation.Valid;

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
@RequestMapping(Apis.ADMIN_API + "/categories")
@Validated
public class CategoryController {

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

    @PostMapping(path = "/save")
    public ResponseEntity<ResponseDTO<CategoryModel>> createCategory(@Valid @RequestBody CategoryModel categoryModel) {
        CategoryModel createdCategory = categoryService.saveCategory(categoryModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(HttpStatus.CREATED.value(), "Categoría creada", createdCategory));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO<CategoryModel>> updateCategory(@PathVariable Long id, @RequestBody CategoryModel updatedCategory) {
        Optional<CategoryModel> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isPresent()) {
        	updatedCategory.setCreated(existingCategory.get().getCreated());
            updatedCategory.setIdCategory(id);
            updatedCategory.setUpdated(LocalDateTime.now());
            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Categoría actualizada", categoryService.saveCategory(updatedCategory)));
        } else {
            String errorMessage = String.format("No existe una categoría con el ID: %d", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), errorMessage, null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Categoría eliminada", null));
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
