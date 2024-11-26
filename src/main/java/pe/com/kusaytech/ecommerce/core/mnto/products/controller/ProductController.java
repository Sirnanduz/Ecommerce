package pe.com.kusaytech.ecommerce.core.mnto.products.controller;

import jakarta.validation.Valid;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;
import pe.com.kusaytech.ecommerce.core.mnto.brand.service.BrandService;
import pe.com.kusaytech.ecommerce.core.mnto.brand.utils.Constants;
import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.category.service.CategoryService;
import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;
import pe.com.kusaytech.ecommerce.core.mnto.discount.service.DiscountService;
import pe.com.kusaytech.ecommerce.core.mnto.products.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ImagesModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.model.ProductModel;
import pe.com.kusaytech.ecommerce.core.mnto.products.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Apis.ADMIN_API + "/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DiscountService discountService;

    @Autowired
	private BrandService brandService;
    
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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDTO<List<ProductModel>>> getProductsByCategory(@PathVariable Long categoryId) {
        CategoryModel category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        List<ProductModel> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Productos encontrados", products));
    }

    @PostMapping(path = "/save")
    public ResponseEntity<ResponseDTO<ProductModel>> createProduct(@Valid @RequestBody ProductModel productModel) {

        if (productService.existsByNameOrSku(productModel.getName(), productModel.getSku())) {
            throw new RuntimeException("Nombre o Codigo de producto estan registrados");
        }

        BrandModel brand = brandService.getObjectById(productModel.getBrand().getBrandId())
                .orElseThrow(() -> new RuntimeException(Constants.MSG_BRAND_NOTFOUND));

        CategoryModel category = categoryService.getCategoryById(productModel.getCategory().getIdCategory())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        DiscountModel discount = discountService.getDiscountById(productModel.getDiscount().getIdDiscount())
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));

        productModel.setBrand(brand);
        productModel.setCategory(category);
        productModel.setDiscount(discount);

        if (productModel.getImagenes() != null) {
            for (ImagesModel imagen : productModel.getImagenes()) {
                imagen.setProduct(productModel); 
            }
        }

        ProductModel createdProduct = productService.saveProduct(productModel);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO<>(HttpStatus.OK.value(), "Producto creado", createdProduct));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO<ProductModel>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductModel productModel) {

        ProductModel existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        BrandModel brand = brandService.getObjectById(productModel.getBrand().getBrandId())
                .orElseThrow(() -> new RuntimeException(Constants.MSG_BRAND_NOTFOUND));

        CategoryModel category = categoryService.getCategoryById(productModel.getCategory().getIdCategory())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        DiscountModel discount = discountService.getDiscountById(productModel.getDiscount().getIdDiscount())
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));
        
        existingProduct.setBrand(brand);
        existingProduct.setName(productModel.getName());
        existingProduct.setDescription(productModel.getDescription());
        existingProduct.setPrice(productModel.getPrice());
        existingProduct.setSku(productModel.getSku());
        existingProduct.setCategory(category);
        existingProduct.setDiscount(discount);
        
        if (productModel.getImagenes() != null) {
            for (ImagesModel imagen : productModel.getImagenes()) {
                imagen.setProduct(productModel); 
            }
        }

        ProductModel updatedProduct = productService.saveProduct(existingProduct);

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Producto actualizado", updatedProduct));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable Long id) {
        productService.getProductById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Producto eliminado", null));
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
