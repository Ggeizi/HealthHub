package com.healthhub.controller;

import java.security.Principal;
import java.util.Map;

import com.healthhub.model.Usuario;
import com.healthhub.repository.UsuarioRepository;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Acesso {

    private final UsuarioRepository usuarios;

    public Acesso(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/")
    public RedirectView inicio() {
        return new RedirectView("/Login/index.html");
    }

    @GetMapping("/api/csrf")
    public Map<String, String> csrf(CsrfToken csrf) {
        return Map.of(
            "header", csrf.getHeaderName(),
            "token", csrf.getToken()
        );
    }

    @GetMapping("/api/me")
    public Usuario usuario(Principal sessao) {
        return usuarios.buscarPorLogin(sessao.getName());
    }
}