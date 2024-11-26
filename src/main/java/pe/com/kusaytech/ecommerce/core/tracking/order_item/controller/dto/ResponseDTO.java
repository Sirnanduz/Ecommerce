package pe.com.kusaytech.ecommerce.core.tracking.order_item.controller.dto;

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