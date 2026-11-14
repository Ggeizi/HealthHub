package com.healthhub;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hospitais")
class Hospitais {
    private final JdbcClient banco;
    private final PasswordEncoder encoder;

    Hospitais(JdbcClient banco, PasswordEncoder encoder) {
        this.banco = banco;
        this.encoder = encoder;
    }

    @GetMapping
    List<Map<String, Object>> listar() {
        return banco.sql("""
            SELECT h.id, h.nome, h.cnpj, h.endereco, u.nome AS recepcionista, u.login
            FROM hospitais h
            JOIN usuarios u ON u.id_hospital = h.id AND u.perfil = 'RECEPCAO'
            ORDER BY h.nome, h.id
            """).query().listOfRows();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    Map<String, Long> cadastrar(@RequestBody Cadastro dados) {
        String nome = Validacao.texto(dados.nome(), "nome do hospital", 3, 160);
        String cnpj = Validacao.documento(dados.cnpj(), "CNPJ", 14);
        String endereco = Validacao.texto(dados.endereco(), "endereço", 5, 255);
        String recepcionista = Validacao.texto(dados.recepcionista(), "recepcionista", 3, 160);
        String email = Validacao.email(dados.login());
        String hash = encoder.encode(Validacao.senha(dados.senha()));

        var chave = new GeneratedKeyHolder();
        banco.sql("INSERT INTO hospitais (nome, cnpj, endereco) VALUES (:nome, :cnpj, :endereco)")
            .param("nome", nome).param("cnpj", cnpj).param("endereco", endereco)
            .update(chave, "id");
        long hospital = chave.getKey().longValue();
        banco.sql("""
            INSERT INTO usuarios (id_hospital, nome, login, senha_hash, perfil)
            VALUES (:hospital, :nome, :login, :hash, 'RECEPCAO')
            """).param("hospital", hospital).param("nome", recepcionista)
            .param("login", email).param("hash", hash).update();
        return Map.of("id", hospital);
    }

    record Cadastro(String nome, String cnpj, String endereco, String recepcionista,
                    String login, String senha) {}
}
