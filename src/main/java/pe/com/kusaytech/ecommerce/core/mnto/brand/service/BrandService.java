package pe.com.kusaytech.ecommerce.core.mnto.brand.service;

import java.util.List;
import java.util.Optional;

import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;

public interface BrandService {
	public List<BrandModel>getAll();
	public Optional<BrandModel> getObjectById(Long id);
	public BrandModel save(BrandModel model);
}
