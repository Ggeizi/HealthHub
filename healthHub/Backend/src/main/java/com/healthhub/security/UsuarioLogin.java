package com.healthhub.security;

// Permite configurar regras de idioma, país e região
import java.util.Locale;

// Importa a classe que facilita a execução de operações SQL no banco de dados
import org.springframework.jdbc.core.JdbcTemplate;

// Importa a classe que representa um usuário autenticável no Spring Security
import org.springframework.security.core.userdetails.User;

// Importa a interface que representa os dados de autenticação de um usuário
import org.springframework.security.core.userdetails.UserDetails;

// Importa a interface responsável por carregar os dados do usuário para autenticação
import org.springframework.security.core.userdetails.UserDetailsService;

// Importa a exceção lançada quando um usuário não é encontrado
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Importa a anotação que identifica uma classe como componente de serviço do Spring
import org.springframework.stereotype.Service;;

@Service
public class UsuarioLogin implements UserDetailsService {

    private final JdbcTemplate banco;

    public UsuarioLogin(JdbcTemplate banco) {
        this.banco = banco;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {

        // Formatação e padronização do email 
        String email = login.strip().toLowerCase(Locale.ROOT);

        return banco.query("""
                SELECT login, senha_hash, perfil
                FROM usuarios
                WHERE login = ?
                """,
                (rs, linha) -> User.withUsername(rs.getString("login"))
                    .password(rs.getString("senha_hash"))
                    .roles(rs.getString("perfil"))
                    .build(),
                email
            )

            // Tranforma a lista do resultado em uma stream
            .stream()

            // Busca o primeiro elemento do stram
            .findFirst()

            // Caso a consulta não tenha resultados lanca o erro
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado."));
    }
}