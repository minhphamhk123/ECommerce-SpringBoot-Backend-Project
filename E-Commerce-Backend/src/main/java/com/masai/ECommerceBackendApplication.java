package com.masai;

import com.masai.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.ApplicationContext;



@SpringBootApplication
public class ECommerceBackendApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ECommerceBackendApplication.class, args);
		AuthService test = context.getBean(AuthService.class);
		test.getBook();
	}
	
	@Bean
	public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
	   return new OpenAPI()
	    .components(new Components().addSecuritySchemes("basicScheme",
	            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
	    .info(new Info().title("E-Commerce Application REST API").version(appVersion)
	            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
