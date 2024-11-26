package pe.com.kusaytech.ecommerce.core.mnto.category.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.mnto.category.model.CategoryModel;
import pe.com.kusaytech.ecommerce.core.mnto.category.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryModel> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryModel> getCategoryById(Long id) { return categoryRepository.findById(id);}

    public CategoryModel saveCategory(CategoryModel categoryModel) {
        return categoryRepository.save(categoryModel);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

}
