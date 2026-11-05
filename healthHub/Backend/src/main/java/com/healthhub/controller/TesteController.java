package com.healthhub.controller;

// importa a anotação GetMapping que associa um método java a uma rota HTTP GET
import org.springframework.web.bind.annotation.GetMapping;

// importa a anotação RestController que indentifica uma classe como um controlador de uma api REST
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    @GetMapping("/teste")
    public String testar() {
        return "HealthHub funcionando!";
    }
}