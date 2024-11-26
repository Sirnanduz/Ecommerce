package pe.com.kusaytech.ecommerce.core.mnto.brand.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.mnto.brand.model.BrandModel;
import pe.com.kusaytech.ecommerce.core.mnto.brand.repository.BrandRepository;
import pe.com.kusaytech.ecommerce.core.mnto.brand.service.BrandService;

@Service
public class BrandServiceImpl implements BrandService{

	@Autowired
	private BrandRepository repository;
	
	@Override
	public List<BrandModel> getAll() {
		return repository.findAll();
	}

	@Override
	public Optional<BrandModel> getObjectById(Long id) {
		return repository.findById(id);
	}

	@Override
	public BrandModel save(BrandModel model) {
		return repository.save(model);
	}


}
