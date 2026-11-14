package com.healthhub;

import java.util.Locale;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    UserDetailsService usuarios(JdbcTemplate banco) {
        return login -> banco.query("""
            SELECT login, senha_hash, perfil FROM usuarios WHERE login = ?
            """, (rs, linha) -> User.withUsername(rs.getString("login"))
                .password(rs.getString("senha_hash"))
                .roles(rs.getString("perfil"))
                .build(), login.strip().toLowerCase(Locale.ROOT))
            .stream().findFirst()
            .orElseThrow(() -> new UsernameNotFoundException("Login inválido."));
    }

    @Bean
    SecurityFilterChain regras(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/", "/Login/**", "/CSS/**", "/JAVASCRIPT/**",
                    "/api/csrf", "/api/login").permitAll()
                .requestMatchers("/Admin/**", "/Cadastro/**", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/Recepcao/**", "/api/recepcao/**").hasRole("RECEPCAO")
                .requestMatchers("/Medico/**", "/api/medico/**").hasRole("MEDICO")
                .requestMatchers("/Paciente/**", "/api/paciente/**").hasRole("PACIENTE")
                .requestMatchers("/api/me", "/api/logout").authenticated()
                .anyRequest().denyAll())
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((req, res, ex) -> {
                    if (req.getServletPath().startsWith("/api/")) {
                        json(res, 401, "{\"erro\":\"Entre para continuar.\"}");
                    } else {
                        res.sendRedirect("/Login/index.html");
                    }
                })
                .accessDeniedHandler((req, res, ex) ->
                    json(res, 403, "{\"erro\":\"Acesso negado ou sessão expirada.\"}")))
            .requestCache(cache -> cache.disable())
            .formLogin(form -> form
                .loginPage("/Login/index.html")
                .loginProcessingUrl("/api/login")
                .usernameParameter("login")
                .successHandler((req, res, auth) -> json(res, 200, "{\"ok\":true}"))
                .failureHandler((req, res, ex) ->
                    json(res, 401, "{\"erro\":\"Login ou senha incorretos.\"}")))
            .logout(logout -> logout.logoutUrl("/api/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((req, res, auth) -> json(res, 200, "{\"ok\":true}")))
            .build();
    }

    private static void json(jakarta.servlet.http.HttpServletResponse res, int status, String body)
            throws java.io.IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(body);
    }
}