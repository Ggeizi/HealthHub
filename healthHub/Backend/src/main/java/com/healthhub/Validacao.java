package com.healthhub;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

final class Validacao {
    private Validacao() {}

    static String texto(String valor, String campo, int minimo, int maximo) {
        String texto = valor == null ? "" : valor.strip();
        if (texto.length() < minimo || texto.length() > maximo) {
            throw invalido("Confira o campo " + campo + ".");
        }
        return texto;
    }

    static String email(String valor) {
        String email = texto(valor, "login", 3, 254).toLowerCase(Locale.ROOT);
        if (!email.matches("[^\\s@]+@[^\\s@]+")) {
            throw invalido("Informe um e-mail válido.");
        }
        return email;
    }

    static String senha(String valor) {
        if (valor == null || valor.isBlank() || valor.length() < 8
                || valor.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw invalido("A senha deve ter pelo menos 8 caracteres e até 72 bytes.");
        }
        return valor;
    }

    static String documento(String valor, String campo, int tamanho) {
        String numero = texto(valor, campo, tamanho, 30).replaceAll("[.\\-/ ]", "");
        if (!numero.matches("[0-9]{" + tamanho + "}")) {
            throw invalido("Confira o campo " + campo + ".");
        }
        return numero;
    }

    static ResponseStatusException invalido(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

    static ResponseStatusException ausente(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }

    static ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }
}

@RestControllerAdvice
class Erros {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> erro(ResponseStatusException ex) {
        String mensagem = ex.getReason() == null ? "Não foi possível concluir." : ex.getReason();
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("erro", mensagem));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Map<String, String>> duplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("erro", "Já existe um cadastro com esse login ou documento."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> formatoInvalido() {
        return ResponseEntity.badRequest().body(Map.of("erro", "Confira os dados enviados."));
    }
}