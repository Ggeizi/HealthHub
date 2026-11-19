package com.healthhub.error;

import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class Erros {

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> erro(ResponseStatusException ex) {
        String mensagem = ex.getReason() == null
                ? "Não foi possível concluir."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("erro", mensagem));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Map<String, String>> duplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "Já existe um cadastro com esse login ou documento."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> formatoInvalido() {
        return ResponseEntity.badRequest()
                .body(Map.of("erro", "Confira os dados enviados."));
    }
}