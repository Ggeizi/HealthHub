package com.healthhub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class AdminInicial {
    @Bean
    ApplicationRunner criarAdmin(JdbcClient banco, PasswordEncoder encoder,
            @Value("${healthhub.admin.login}") String login,
            @Value("${healthhub.admin.password}") String senha) {
        return args -> {
            if (login.isBlank() && senha.isEmpty()) {
                return;
            }
            String email = Validacao.email(login);
            String hash = encoder.encode(Validacao.senha(senha));
            banco.sql("""
                INSERT INTO usuarios (nome, login, senha_hash, perfil)
                VALUES ('Administrador', :login, :hash, 'ADMIN')
                ON DUPLICATE KEY UPDATE id = id
                """).param("login", email).param("hash", hash).update();
            String perfil = banco.sql("SELECT perfil FROM usuarios WHERE login = :login")
                .param("login", email).query(String.class).single();
            if (!"ADMIN".equals(perfil)) {
                throw new IllegalStateException("ADMIN_LOGIN já pertence a outro perfil.");
            }
        };
    }
}