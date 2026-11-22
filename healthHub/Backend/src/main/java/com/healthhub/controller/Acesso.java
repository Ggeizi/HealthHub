package com.healthhub.controller;


// Importa a interface que representa o usuário autenticado
import java.security.Principal;

// Importa a interface que armazena dados em pares de chave e valor
import java.util.Map;

// Importa a classe usuário
import com.healthhub.model.Usuario;

// Importa a classe responsável pelo acesso aos usuários no banco de dados
import com.healthhub.repository.UsuarioRepository;

// Importa a interface que representa o token de proteção contra ataques CSRF
import org.springframework.security.web.csrf.CsrfToken;

// Importa a anotação que mapeia requisições HTTP GET para métodos
import org.springframework.web.bind.annotation.GetMapping;

// Importa a anotação que identifica uma classe como controlador de API REST
import org.springframework.web.bind.annotation.RestController;

// Importa a classe utilizada para redirecionar requisições para outra URL
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