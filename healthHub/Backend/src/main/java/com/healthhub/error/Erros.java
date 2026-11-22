package com.healthhub.error;

import java.util.Map;

// Importa a exceção lançada quando ocorre uma violação de chave única ou primária no banco de dados
import org.springframework.dao.DuplicateKeyException;

// Importa o enum que representa os códigos de status HTTP, como 200, 400, 404 e 500
import org.springframework.http.HttpStatus;

// Importa a classe que permite construir respostas HTTP com status, cabeçalhos e corpo
import org.springframework.http.ResponseEntity;

// Importa a exceção lançada quando o corpo de uma requisição HTTP não pode ser interpretado
import org.springframework.http.converter.HttpMessageNotReadableException;

// Importa a anotação que identifica métodos responsáveis por tratar exceções
import org.springframework.web.bind.annotation.ExceptionHandler;

// Importa a anotação que permite tratar exceções globalmente em controladores REST
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Importa a exceção que permite associar um erro a um status HTTP específico
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
public class Erros {
    
    // Trata erros gerais
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> erro(ResponseStatusException ex) {
        String mensagem = ex.getReason() == null
                ? "Não foi possível concluir."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("erro", mensagem));
    }

    // Tratando duplicidade
    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Map<String, String>> duplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "Já existe um cadastro com esse login ou documento."));
    }

    // Tratando erro de formato dos dados enviados
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> formatoInvalido() {
        return ResponseEntity.badRequest()
                .body(Map.of("erro", "Confira os dados enviados."));
    }
}