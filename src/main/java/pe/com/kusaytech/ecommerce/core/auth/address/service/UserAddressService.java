package pe.com.kusaytech.ecommerce.core.auth.address.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.auth.address.model.UserAddressModel;
import pe.com.kusaytech.ecommerce.core.auth.address.repository.UserAddressRepository;

import java.util.List;
import java.util.Optional;


@Service

public class UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    public List<UserAddressModel> getAllAddresses() {
        return userAddressRepository.findAll();
    }

    public Optional<UserAddressModel> getAddressById(Long id) {
        return userAddressRepository.findById(id);
    }

    public UserAddressModel saveAddress(UserAddressModel userAddressModel) {
        return userAddressRepository.save(userAddressModel);
    }

    public void deleteAddress(Long id) {
        userAddressRepository.deleteById(id);
    }
}
