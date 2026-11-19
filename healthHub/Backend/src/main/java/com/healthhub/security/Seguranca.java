package com.healthhub.security;

import java.io.IOException;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class Seguranca {

    @Bean
    PasswordEncoder senhas() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain regras(HttpSecurity http) throws Exception {

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

        http.formLogin(login -> login
            .loginPage("/Login/index.html")
            .loginProcessingUrl("/api/login")
            .usernameParameter("login")
            .successHandler((req, res, usuario) ->
                json(res, 200, "{\"ok\":true}"))
            .failureHandler((req, res, erro) ->
                json(res, 401, "{\"erro\":\"Login incorreto.\"}"))
        );

        http.logout(logout -> logout
            .logoutUrl("/api/logout")
            .deleteCookies("JSESSIONID")
            .logoutSuccessHandler((req, res, usuario) ->
                json(res, 200, "{\"ok\":true}"))
        );

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

    private static void json(
            HttpServletResponse res, int status, String conteudo
    ) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(conteudo);
    }
}