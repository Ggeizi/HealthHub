package com.healthhub.repository;


import java.util.Optional;

import com.healthhub.model.NovoAgendamento;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class AgendamentoRepository {

    private final JdbcClient banco;

    public AgendamentoRepository(JdbcClient banco) {
        this.banco = banco;
    }

    // Bloquear o hospital durante o agendamento
    public void bloquearHospital(long hospital) {
        banco.sql("""
            SELECT id FROM hospitais
            WHERE id = :hospital FOR UPDATE
            """)
            .param("hospital", hospital)
            .query(Long.class)
            .single();
    }

    // Verificar conflito de horário
    public boolean horarioOcupado(long hospital, NovoAgendamento dados) {
        return banco.sql("""
            SELECT id FROM agendamentos
            WHERE id_hospital = :hospital
              AND inicio = :inicio
              AND status <> 'CANCELADO'
              AND (id_medico = :medico OR id_paciente = :paciente)
            LIMIT 1 FOR UPDATE
            """)
            .param("hospital", hospital)
            .param("inicio", dados.getInicio())
            .param("medico", dados.getMedicoId())
            .param("paciente", dados.getPacienteId())
            .query(Long.class)
            .optional()
            .isPresent();
    }

    // Cadastrar agendamento
    public void cadastrar(long hospital, NovoAgendamento dados) {
        banco.sql("""
            INSERT INTO agendamentos
                (id_hospital, id_medico, id_paciente, inicio, observacao)
            VALUES (:hospital, :medico, :paciente, :inicio, :observacao)
            """)
            .param("hospital", hospital)
            .param("medico", dados.getMedicoId())
            .param("paciente", dados.getPacienteId())
            .param("inicio", dados.getInicio())
            .param("observacao", dados.getObservacao())
            .update();
    }

    // Consultar o status do agendamento
    public Optional<String> buscarStatus(long id, long hospital) {
        return banco.sql("""
            SELECT status FROM agendamentos
            WHERE id = :id AND id_hospital = :hospital
            FOR UPDATE
            """)
            .param("id", id)
            .param("hospital", hospital)
            .query(String.class)
            .optional();
    }

    // Cancelar agendamento
    public void cancelar(long id) {
        banco.sql("""
            UPDATE agendamentos
            SET status = 'CANCELADO'
            WHERE id = :id
            """)
            .param("id", id)
            .update();
    }
}