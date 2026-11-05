package com.healthhub.service;

// importa a anotação service que indentifica uma classe como um serviço da aplicação Spring Boot
import org.springframework.stereotype.Service;

@Service
public class TesteService {

    public String testar() {
        return "service funcionando!";
    }
    
}
