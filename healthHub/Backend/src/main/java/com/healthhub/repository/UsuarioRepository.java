
package com.healthhub.repository;

import java.util.List;
import java.util.Map;

import com.healthhub.model.Usuario;
import com.healthhub.model.Medico;
import com.healthhub.model.Paciente;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    private final JdbcClient banco;

    public UsuarioRepository(JdbcClient banco) {
        this.banco = banco;
    }

    // Buscar usuário pelo login
    public Usuario buscarPorLogin(String login) {
        return banco.sql("""
            SELECT id, nome, login, perfil, id_hospital, foto
            FROM usuarios WHERE login = :login
            """)
            .param("login", login)
            .query((rs, linha) -> {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setPerfil(rs.getString("perfil"));
                usuario.setFoto(rs.getString("foto"));
                usuario.setHospitalId(
                    rs.getObject("id_hospital", Long.class));
                return usuario;
            })
            .single();
    }

    // Listar médicos ou pacientes
    public List<Map<String, Object>> listar(long hospital, String perfil) {
        return banco.sql("""
            SELECT id, nome FROM usuarios
            WHERE id_hospital = :hospital AND perfil = :perfil
            ORDER BY nome, id
            """)
            .param("hospital", hospital)
            .param("perfil", perfil)
            .query()
            .listOfRows();
    }

    // Cadastrar médico
    public void cadastrarMedico(long hospital, Medico medico, String hash) {
        banco.sql("""
            INSERT INTO usuarios
                (id_hospital, perfil, nome, login, senha_hash, crm, especialidade)
            VALUES (:hospital, 'MEDICO', :nome, :login, :senha, :crm, :especialidade)
            """)
            .param("hospital", hospital)
            .param("nome", medico.getNome())
            .param("login", medico.getLogin())
            .param("senha", hash)
            .param("crm", medico.getCrm())
            .param("especialidade", medico.getEspecialidade())
            .update();
    }

    // Cadastrar paciente
    public void cadastrarPaciente(long hospital, Paciente paciente, String hash) {
        banco.sql("""
            INSERT INTO usuarios
                (id_hospital, perfil, nome, login, senha_hash, cpf, nascimento, telefone)
            VALUES (:hospital, 'PACIENTE', :nome, :login, :senha, :cpf, :nascimento, :telefone)
            """)
            .param("hospital", hospital)
            .param("nome", paciente.getNome())
            .param("login", paciente.getLogin())
            .param("senha", hash)
            .param("cpf", paciente.getCpf())
            .param("nascimento", paciente.getNascimento())
            .param("telefone", paciente.getTelefone())
            .update();
    }

    // Verificar se o usuário pertence ao hospital
    public boolean existeNoHospital(Long id, long hospital, String perfil) {
        return banco.sql("""
            SELECT id FROM usuarios
            WHERE id = :id
              AND id_hospital = :hospital
              AND perfil = :perfil
            """)
            .param("id", id)
            .param("hospital", hospital)
            .param("perfil", perfil)
            .query(Long.class)
            .optional()
            .isPresent();
    }
}
