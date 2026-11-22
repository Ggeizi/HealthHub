
package com.healthhub.security;

// Importa a classe que representa erros de entrada e saída de dados
import java.io.IOException;

// Importa o enum que identifica o tipo de encaminhamento de uma requisição
import jakarta.servlet.DispatcherType;

// Importa a interface que permite configurar respostas HTTP
import jakarta.servlet.http.HttpServletResponse;

// Importa a anotação que registra objetos gerenciados pelo Spring
import org.springframework.context.annotation.Bean;

// Importa a anotação que identifica uma classe de configuração do Spring
import org.springframework.context.annotation.Configuration;

// Importa a classe que permite configurar a segurança das requisições HTTP
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// Importa a classe que implementa a criptografia de hash de senhas com BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Importa a interface responsável por codificar e verificar senhas
import org.springframework.security.crypto.password.PasswordEncoder;

// Importa a interface que representa a cadeia de filtros de segurança HTTP
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class Seguranca {

    @Bean
    PasswordEncoder senhas() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain regras(HttpSecurity http) throws Exception {

        // Permitir CORS usando CorsConfig.java
        http.cors(cors -> {});

        // Permissões por perfil
        http.authorizeHttpRequests(acesso -> acesso
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .requestMatchers(
                "/", "/Login/**", "/CSS/**", "/JAVASCRIPT/**",
                "/api/csrf", "/api/login"
            ).permitAll()
            .requestMatchers("/Admin/**", "/Cadastro/**", "/api/admin/**")
                .hasRole("ADMIN")
            .requestMatchers("/Recepcao/**", "/api/recepcao/**")
                .hasRole("RECEPCAO")
            .requestMatchers("/Medico/**", "/api/medico/**")
                .hasRole("MEDICO")
            .requestMatchers("/Paciente/**", "/api/paciente/**")
                .hasRole("PACIENTE")
            .requestMatchers("/api/me", "/api/logout").authenticated()
            .anyRequest().denyAll()
        );

        // Login
        http.formLogin(login -> login
            .loginPage("/Login/index.html")
            .loginProcessingUrl("/api/login")
            .usernameParameter("login")
            .successHandler((req, res, usuario) ->
                json(res, 200, "{\"ok\":true}"))
            .failureHandler((req, res, erro) ->
                json(res, 401, "{\"erro\":\"Login incorreto.\"}"))
        );

        // Logout
        http.logout(logout -> logout
            .logoutUrl("/api/logout")
            .deleteCookies("JSESSIONID")
            .logoutSuccessHandler((req, res, usuario) ->
                json(res, 200, "{\"ok\":true}"))
        );

        // Tratamento de erros
        http.exceptionHandling(erros -> erros
            .authenticationEntryPoint((req, res, erro) -> {
                if (req.getServletPath().startsWith("/api/")) {
                    json(res, 401, "{\"erro\":\"Faça login.\"}");
                } else {
                    res.sendRedirect("/Login/index.html");
                }
            })
            .accessDeniedHandler((req, res, erro) ->
                json(res, 403, "{\"erro\":\"Acesso negado.\"}"))
        );

        http.requestCache(cache -> cache.disable());

        return http.build();
    }

    // Método que verifica se o login foi realizado com sucesso ou se o acesso foi negado
    private static void json(
            HttpServletResponse res, int status, String conteudo
    ) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(conteudo);
    }
}
