package pe.com.kusaytech.ecommerce.core.auth.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestResetPasswordDTO {
    private String token;
    private String newPassword;
}