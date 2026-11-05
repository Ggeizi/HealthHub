package com.healthhub.security;

// importa a anotação bean que indica que o objeto retornado pelo método é um bean gerenciado pelo Spring
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

// importa a classe httpsecurity que configura as regras de segurança das requisições http
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

//importa a classe user que representa um usuário do sistema
import org.springframework.security.core.userdetails.User;

// importa uma interface responsável por localizar dados do usuário
import org.springframework.security.core.userdetails.UserDetailsService;

// importa uma classe que implementa userdetailsservice e armazena os dados do usuário na memória
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

// importa uma interface que representa uma cadeia de fitros de segurança
import org.springframework.security.web.SecurityFilterChain;

// importa a interface que define métodos para proteger e verificar senhas
import org.springframework.security.crypto.password.PasswordEncoder;

// importa uma classe que implementa passwordencoder e utiliza o algoritmo bcrypt para codificar senhas
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Teste {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService usuarios(PasswordEncoder encoder) {

        var admin = User.withUsername("admin")
                .password(encoder.encode("12345"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}