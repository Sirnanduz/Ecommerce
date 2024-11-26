package pe.com.kusaytech.ecommerce.core.auth.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDTO<T> {
    private int codigo;
    private String mensaje;
    private T data;
}