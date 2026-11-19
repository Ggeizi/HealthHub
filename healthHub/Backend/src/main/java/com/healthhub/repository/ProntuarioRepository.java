package com.healthhub.repository;

import java.util.List;
import java.util.Optional;

import com.healthhub.model.Prontuario;
import com.healthhub.model.TextoProntuario;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProntuarioRepository {

    private final JdbcClient banco;

    public ProntuarioRepository(JdbcClient banco) {
        this.banco = banco;
    }

    // Consultar o prontuário de um atendimento
    public Optional<TextoProntuario> buscarTexto(long id) {
        return banco.sql("""
            SELECT relato, diagnostico, conduta
            FROM prontuarios
            WHERE id_agendamento = :id
            """)
            .param("id", id)
            .query(TextoProntuario.class)
            .optional();
    }

    // Listar prontuários do paciente
    public List<Prontuario> listar(long paciente, long hospital) {
        return consultaPaciente(paciente, hospital,
                " ORDER BY a.inicio DESC, r.id DESC")
            .query(Prontuario.class)
            .list();
    }

    // Buscar prontuário pelo ID
    public Optional<Prontuario> buscarPorId(
            long id, long paciente, long hospital) {

        return consultaPaciente(paciente, hospital, " AND r.id = :id")
            .param("id", id)
            .query(Prontuario.class)
            .optional();
    }

    // Verificar o status do atendimento
    public Optional<String> buscarStatus(
            long id, long medico, long hospital, boolean bloquear) {

        return banco.sql("""
            SELECT status FROM agendamentos
            WHERE id = :id
              AND id_medico = :medico
              AND id_hospital = :hospital
            """ + (bloquear ? " FOR UPDATE" : ""))
            .param("id", id)
            .param("medico", medico)
            .param("hospital", hospital)
            .query(String.class)
            .optional();
    }

    // Inserir ou atualizar prontuário
    public void salvar(
            long id, String relato, String diagnostico, String conduta) {

        banco.sql("""
            INSERT INTO prontuarios (id_agendamento, relato, diagnostico, conduta)
            VALUES (:id, :relato, :diagnostico, :conduta)
            ON DUPLICATE KEY UPDATE
                relato = :relato,
                diagnostico = :diagnostico,
                conduta = :conduta
            """)
            .param("id", id)
            .param("relato", relato)
            .param("diagnostico", diagnostico)
            .param("conduta", conduta)
            .update();
    }

    // Concluir o agendamento
    public void concluirAgendamento(long id) {
        banco.sql("""
            UPDATE agendamentos
            SET status = 'CONCLUIDO'
            WHERE id = :id
            """)
            .param("id", id)
            .update();
    }

    // Consulta compartilhada para prontuários do paciente
    private JdbcClient.StatementSpec consultaPaciente(
            long paciente, long hospital, String complemento) {

        return banco.sql("""
            SELECT r.id,
                   DATE_FORMAT(a.inicio, '%Y-%m-%dT%H:%i:%s') AS data,
                   m.nome AS medico,
                   p.nome AS paciente,
                   h.nome AS hospital,
                   r.relato, r.diagnostico, r.conduta
            FROM prontuarios r
            JOIN agendamentos a ON a.id = r.id_agendamento
            JOIN usuarios m ON m.id = a.id_medico
            JOIN usuarios p ON p.id = a.id_paciente
            JOIN hospitais h ON h.id = a.id_hospital
            WHERE a.id_paciente = :paciente
              AND a.id_hospital = :hospital
            """ + complemento)
            .param("paciente", paciente)
            .param("hospital", hospital);
    }
}