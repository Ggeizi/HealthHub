
package com.healthhub.config;


// Executa uma rotina automaticamente durante a inicialização do Spring Boot
import org.springframework.boot.ApplicationRunner;

// Importa a anotação Bean que registra e gerencia objetos no Spring
import org.springframework.context.annotation.Bean;

// Importa uma classe que contém configurações do Spring
import org.springframework.context.annotation.Configuration;

// Facilita a execução de consultas SQL utilizando JDBC
import org.springframework.jdbc.core.simple.JdbcClient;

// Interface responsável por codificar e verificar senhas
import org.springframework.security.crypto.password.PasswordEncoder;

// Injeta valores definidos nas propriedades da aplicação
import org.springframework.beans.factory.annotation.Value;

// Importa a classe personalizada de validações do HealthHub
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
