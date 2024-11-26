package pe.com.kusaytech.ecommerce.core.mnto.brand.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private int codigo;
    private String mensaje;
    private T data;
}