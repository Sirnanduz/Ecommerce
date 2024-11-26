package pe.com.kusaytech.ecommerce.core.store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;
import pe.com.kusaytech.ecommerce.core.mnto.brand.service.BrandService;
import pe.com.kusaytech.ecommerce.core.store.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.store.utils.Constants;

@RestController
@RequestMapping(Apis.BUSINESS_API + "/brands")
public class StoreBrandController {
	
	@Autowired
	private BrandService brandService;
	
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<BrandModel>>> getAllBrands() {
        List<BrandModel> brands = brandService.getAll();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_OK, brands));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<BrandModel>> getBrandById(@PathVariable Long id) {
        Optional<BrandModel> brandModel = brandService.getObjectById(id);
        return brandModel.map(brand -> ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_OK, brand)))
                .orElseGet(() -> {
                    String errorMessage = String.format(Constants.MSG_BRAND_NOTFOUND.concat("%d"), id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), errorMessage, null));
                });
    }
}
