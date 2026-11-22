
package com.healthhub.util;

import java.util.Base64;

public final class Imagem {

    private Imagem() {}

    public static String validar(String foto) {
        if (foto == null || foto.isBlank()) return null;

        int virgula = foto.indexOf(',');

        if (virgula < 0 || foto.length() > 7_000_000) {
            throw Validacao.invalido("Imagem inválida ou maior que 5 MB.");
        }

        String tipo = foto.substring(0, virgula);

        if (!tipo.equals("data:image/png;base64")
                && !tipo.equals("data:image/jpeg;base64")
                && !tipo.equals("data:image/webp;base64")) {
            throw Validacao.invalido("Use uma imagem JPG, PNG ou WEBP.");
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(
                foto.substring(virgula + 1)
            );

            if (bytes.length == 0 || bytes.length > 5 * 1024 * 1024) {
                throw Validacao.invalido("A imagem deve ter até 5 MB.");
            }

        } catch (IllegalArgumentException ex) {
            throw Validacao.invalido("Imagem inválida.");
        }

        return foto;
    }
}
