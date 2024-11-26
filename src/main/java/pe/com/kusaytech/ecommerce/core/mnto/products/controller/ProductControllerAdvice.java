package pe.com.kusaytech.ecommerce.core.mnto.products.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.jsonwebtoken.security.SignatureException;

@ControllerAdvice
public class ProductControllerAdvice {
	
	@ResponseStatus(value=HttpStatus.CONFLICT,
            reason="Data integrity violation")
	@ExceptionHandler(SignatureException.class)
	public ResponseBodyAdvice<Map<Object, String>>handleSignatureException(){
		
		return null;
	}
}
