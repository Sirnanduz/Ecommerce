package pe.com.kusaytech.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class PropertiesConfig {

	@Value("${chanel.client.web.url}")
	private String chanelClientWebUrl;
	
	@Value("${local.client.web.url}")
	private String localClientWebUrl;

}
