package com.healthhub.controller;

import java.util.List;
import java.util.Map;

import com.healthhub.model.CadastroHospital;
import com.healthhub.service.HospitalService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hospitais")
public class Hospitais {

    private final HospitalService service;

    public Hospitais(HospitalService service) {
        this.service = service;
    }

    // Listar hospitais
    @GetMapping
    public List<Map<String, Object>> listar() {
        return service.listar();
    }

    // Cadastrar hospital e recepcionista
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> cadastrar(
            @RequestBody CadastroHospital dados) {

        long id = service.cadastrar(dados);

        return Map.of("id", id);
    }
}