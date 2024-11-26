package pe.com.kusaytech.ecommerce.security;


import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;

@Getter
@Setter
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String token;
    private final UserModel user;

    public JwtResponse(String jwttoken,UserModel user) {
        this.token = jwttoken;
        this.user=user;
    }
}