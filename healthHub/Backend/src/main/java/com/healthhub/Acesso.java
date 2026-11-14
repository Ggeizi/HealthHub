package com.healthhub;

import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
class Acesso {
    private final JdbcClient banco;

    Acesso(JdbcClient banco) {
        this.banco = banco;
    }

    @GetMapping("/")
    org.springframework.web.servlet.view.RedirectView inicio() {
        return new org.springframework.web.servlet.view.RedirectView("/Login/index.html");
    }

    @GetMapping("/api/csrf")
    Map<String, String> csrf(CsrfToken csrf) {
        return Map.of("header", csrf.getHeaderName(), "token", csrf.getToken());
    }

    @GetMapping("/api/me")
    Usuario usuario(Principal sessao) {
        return banco.sql("""
            SELECT id, nome, login, perfil, id_hospital AS hospitalId
            FROM usuarios WHERE login = :login
            """).param("login", sessao.getName()).query(Usuario.class).single();
    }

    record Usuario(long id, String nome, String login, String perfil, Long hospitalId) {}
}