package pe.com.kusaytech.ecommerce.core.auth.user.controller.dto;

public class RequestEmailDTO {
    private String email;

    public RequestEmailDTO() {}

    public RequestEmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}