package com.healthhub;

// Importa a classe responsável por iniciar a aplicação Spring Boot
import org.springframework.boot.SpringApplication;

// Importa a anotação que identifica a aplicação Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthHub {

    public static void main(String[] args) {
        SpringApplication.run(HealthHub.class, args);
    }
}