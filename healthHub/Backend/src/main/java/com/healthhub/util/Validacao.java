
package com.healthhub.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Validacao {

    private Validacao() {}

    public static String texto(
            String valor, String campo, int minimo, int maximo) {

        String texto = valor == null ? "" : valor.strip();

        if (texto.length() < minimo || texto.length() > maximo) {
            throw invalido("Confira o campo " + campo + ".");
        }

        return texto;
    }

    public static String email(String valor) {

        String email = texto(valor, "login", 3, 254)
                .toLowerCase(Locale.ROOT);

        if (!email.matches("[^\\s@]+@[^\\s@]+")) {
            throw invalido("Informe um e-mail válido.");
        }

        return email;
    }

    public static String senha(String valor) {

        if (valor == null || valor.isBlank()
                || valor.length() < 8
                || valor.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw invalido(
                "A senha deve ter pelo menos 8 caracteres e até 72 bytes."
            );
        }

        return valor;
    }

    public static String documento(
            String valor, String campo, int tamanho) {

        String numero = texto(valor, campo, tamanho, 30)
                .replaceAll("[. /-]", "");

        if (!numero.matches("[0-9]{" + tamanho + "}")) {
            throw invalido("Confira o campo " + campo + ".");
        }

        return numero;
    }

    public static ResponseStatusException invalido(String mensagem) {
        return new ResponseStatusException(
            HttpStatus.BAD_REQUEST, mensagem
        );
    }

    public static ResponseStatusException ausente(String mensagem) {
        return new ResponseStatusException(
            HttpStatus.NOT_FOUND, mensagem
        );
    }

    public static ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(
            HttpStatus.CONFLICT, mensagem
        );
    }
}
