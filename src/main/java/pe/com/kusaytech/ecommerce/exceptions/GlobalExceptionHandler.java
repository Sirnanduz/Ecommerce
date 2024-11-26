package pe.com.kusaytech.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import pe.com.kusaytech.ecommerce.exceptions.dto.ResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO<Void>> validationExceptionHandler(ValidationException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseDTO<>(400, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> globalExceptionHandler(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseDTO<>(500, "Error en servicio interno", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}