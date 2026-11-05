package com.healthhub.config;

// indentifica classes de configuração do Spring
import org.springframework.context.annotation.Configuration;

// permite definir regras de CORS
import org.springframework.web.servlet.config.annotation.CorsRegistry;

// Permite personalizar a configuração do Spring MVC
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// A anotação @Configuration indentifica uma classe responsável por fornecer configurações para a aplicação
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");

    }
}
