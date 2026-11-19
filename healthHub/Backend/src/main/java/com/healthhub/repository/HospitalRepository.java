
package com.healthhub.repository;

import java.util.List;
import java.util.Map;

// classe que conecta os comandos sql ao banco de dados
import org.springframework.jdbc.core.simple.JdbcClient;

// classe que guarda o id gerado pelo banco de dados
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class HospitalRepository {

    private final JdbcClient banco;

    public HospitalRepository(JdbcClient banco) {
        this.banco = banco;
    }

    // Cadastrar hospital
    public long cadastrar(String nome, String cnpj, String endereco) {

        // variável que vai armazenar o id
        var chave = new GeneratedKeyHolder();

        // comando sql pada cadastro
        banco.sql("""
            INSERT INTO hospitais (nome, cnpj, endereco)
            VALUES (:nome, :cnpj, :endereco)
            """)
            .param("nome", nome)
            .param("cnpj", cnpj)
            .param("endereco", endereco)
            .update(chave, "id");

        Number id = chave.getKey();

        if (id == null) {
            throw new IllegalStateException(
                "Não foi possível obter o ID do hospital."
            );
        }

        return id.longValue();
    }

    // Cadastrar recepcionista
    public void cadastrarRecepcionista(
            long hospitalId,
            String nome,
            String email,
            String hash) {

        banco.sql("""
            INSERT INTO usuarios
                (id_hospital, nome, login, senha_hash, perfil)
            VALUES
                (:hospital, :nome, :email, :hash, 'RECEPCAO')
            """)
            .param("hospital", hospitalId)
            .param("nome", nome)
            .param("email", email)
            .param("hash", hash)
            .update();
    }

    // Listar hospitais
    public List<Map<String, Object>> listar() {

        return banco.sql("""
            SELECT id, nome, cnpj, endereco
            FROM hospitais
            ORDER BY nome
            """)
            .query()
            .listOfRows();
    }
}
