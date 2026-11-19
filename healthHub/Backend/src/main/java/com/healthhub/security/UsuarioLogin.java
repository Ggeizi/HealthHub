package com.healthhub.security;

import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioLogin implements UserDetailsService {

    private final JdbcTemplate banco;

    public UsuarioLogin(JdbcTemplate banco) {
        this.banco = banco;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
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
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado."));
    }
}