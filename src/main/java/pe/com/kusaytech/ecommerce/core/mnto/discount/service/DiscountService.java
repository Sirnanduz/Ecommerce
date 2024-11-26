package pe.com.kusaytech.ecommerce.core.mnto.discount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.mnto.discount.model.DiscountModel;
import pe.com.kusaytech.ecommerce.core.mnto.discount.repository.DiscountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<DiscountModel> getAllDiscount() {
        return discountRepository.findAll();
    }

    public Optional<DiscountModel> getDiscountById(Long id) { return discountRepository.findById(id);}

    public DiscountModel saveDiscount(DiscountModel discountModel) {
        return discountRepository.save(discountModel);
    }

    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }


}
