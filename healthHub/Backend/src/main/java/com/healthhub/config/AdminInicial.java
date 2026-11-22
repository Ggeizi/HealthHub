
package com.healthhub.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import com.healthhub.util.Validacao;

@Configuration
class AdminInicial {

    @Bean
    ApplicationRunner criarAdmin(
            JdbcClient banco,
            PasswordEncoder encoder,
            @Value("${healthhub.admin.login}") String login,
            @Value("${healthhub.admin.password}") String senha) {

        return args -> {
            if (login.isBlank() && senha.isBlank()) return;
            String loginValido = Validacao.email(login);
            String senhaValida = Validacao.senha(senha);
            banco.sql("""
            INSERT INTO usuarios (nome, login, senha_hash, perfil)
            VALUES ('Administrador', :login, :senha, 'ADMIN')
            ON DUPLICATE KEY UPDATE id = id
            """)
            .param("login", loginValido)
            .param("senha", encoder.encode(senhaValida))
            .update();
        };
    }
}
