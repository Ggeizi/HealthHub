
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
    public long cadastrar(String nome, String cnpj, String endereco, String foto) {

        // variável que vai armazenar o id
        var chave = new GeneratedKeyHolder();

        // comando sql pada cadastro
        banco.sql("""
            INSERT INTO hospitais (nome, cnpj, endereco, foto)
            VALUES (:nome, :cnpj, :endereco, :foto)
            """)
            .param("nome", nome)
            .param("cnpj", cnpj)
            .param("endereco", endereco)
            .param("foto", foto)
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
            String hash,
            String foto) {

        banco.sql("""
            INSERT INTO usuarios
                (id_hospital, nome, login, senha_hash, perfil, foto)
            VALUES
                (:hospital, :nome, :email, :hash, 'RECEPCAO', :foto)
            """)
            .param("hospital", hospitalId)
            .param("nome", nome)
            .param("email", email)
            .param("hash", hash)
            .param("foto", foto)
            .update();
    }

    // Listar hospitais
    public boolean existe(long id) {
        return banco.sql("SELECT id FROM hospitais WHERE id = :id")
            .param("id", id).query(Long.class).optional().isPresent();
    }

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
