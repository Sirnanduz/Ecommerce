package pe.com.kusaytech.ecommerce.core.auth.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;
import pe.com.kusaytech.ecommerce.core.auth.roles.repository.RolRepository;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserDetailsAdapter;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;
import pe.com.kusaytech.ecommerce.core.auth.user.repository.UserRespository;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;
import pe.com.kusaytech.ecommerce.security.utils.JwtTokenUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${reset.password.url}")
    private String resetPasswordUrl;

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public UserModel saveUser(UserModel userModel) {
        initializeDefaultRole();
        if (userModel.getRole()==null || userModel.getRole().getIdRole() == null) {
            RolModel defaultRole = rolRepository.findByRoleName("USER");
            userModel.setRole(defaultRole);
        }
        userModel.setCreatedAt(LocalDateTime.now());
        return userRepository.save(userModel);
    }

    private void initializeDefaultRole() {
        RolModel defaultRole = rolRepository.findByRoleName("USER");
        if (defaultRole == null) {
            defaultRole = new RolModel();
            defaultRole.setRoleName("USER");
            defaultRole.setCreatedAt(LocalDateTime.now());
            defaultRole.setStatus("A");
            rolRepository.save(defaultRole);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserModel getUserByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void requestPasswordReset(String email) {
        UserModel user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ValidationException("User not found with email: " + email);
        }

        UserDetails userDetails = new UserDetailsAdapter(user);
        String token = jwtTokenUtil.generateToken(userDetails);
        String resetLink = resetPasswordUrl + "?token=" + token;
        emailService.sendEmail(email, "Password Reset Request", "Click the link to reset your password: " + resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        String email = jwtTokenUtil.getUsernameFromToken(token);
        UserModel user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ValidationException("Invalid token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}