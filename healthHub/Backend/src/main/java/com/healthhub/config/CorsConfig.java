package com.healthhub.config;

// Insere uma anotação configuration que indentifica uma classe como fonte de configurações
import org.springframework.context.annotation.Configuration;

// Importar uma classe utilizada para registrar regras de CORS
import org.springframework.web.servlet.config.annotation.CorsRegistry;

// Importa uma interface que permite personalizar as configurações do spring
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry cors) {
        cors.addMapping("/**")
            .allowedOrigins(
                "http://localhost:5500",
                "http://127.0.0.1:5500"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}