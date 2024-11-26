package pe.com.kusaytech.ecommerce.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

@Configuration
public class SwaggerConfig {

  @Value("${kusaytech.openapi.dev-url}")
  private String devUrl;

  @Value("${kusaytech.openapi.prod-url}")
  private String prodUrl;

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl(devUrl);
    devServer.setDescription("Server URL in Development environment");

    Server prodServer = new Server();
    prodServer.setUrl(prodUrl);
    prodServer.setDescription("Server URL in Production environment");

    Contact contact = new Contact();
    contact.setEmail("support@kusaytech.com");
    contact.setName("Kusay Tech");
    contact.setUrl("https://kusaytech.com");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("Kusay Tech API")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage Kusay Tech services.")
        .termsOfService("https://kusaytech.com/terms")
        .license(mitLicense);

    // Define the security scheme
    SecurityScheme securityScheme = new SecurityScheme()
        .name("Bearer Authentication")
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");

    // Add the security requirement
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Authentication");

    return new OpenAPI()
        .info(info)
        .servers(List.of(devServer, prodServer))
        .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme))
        .addSecurityItem(securityRequirement);
  }
}