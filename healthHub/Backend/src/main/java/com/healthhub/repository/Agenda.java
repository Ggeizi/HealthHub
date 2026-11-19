package com.healthhub.repository;

import java.util.List;
import java.util.Map;

import com.healthhub.model.Usuario;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class Agenda {

    private final JdbcClient banco;

    public Agenda(JdbcClient banco) {
        this.banco = banco;
    }

    public List<Map<String, Object>> listar(Usuario usuario) {

        String filtro = switch (usuario.getPerfil()) {
            case "RECEPCAO" -> "";
            case "MEDICO" -> " AND a.id_medico = :usuario";
            case "PACIENTE" -> " AND a.id_paciente = :usuario";
            default -> throw new IllegalArgumentException("Perfil sem agenda.");
        };

        var consulta = banco.sql("""
            SELECT a.id, DATE_FORMAT(a.inicio, '%Y-%m-%dT%H:%i:%s') AS inicio, a.status,
                   m.nome AS medico, p.nome AS paciente
            """ + ("PACIENTE".equals(usuario.getPerfil()) ? "" : ", a.observacao, r.id AS prontuarioId")
            + """
              FROM agendamentos a
              JOIN usuarios m ON m.id = a.id_medico
              JOIN usuarios p ON p.id = a.id_paciente
              LEFT JOIN prontuarios r ON r.id_agendamento = a.id
              WHERE a.id_hospital = :hospital
            """ + filtro + " ORDER BY a.inicio, a.id")
            .param("hospital", usuario.getHospitalId());

        if (!filtro.isEmpty()) {
            consulta = consulta.param("usuario", usuario.getId());
        }

        return consulta.query().listOfRows();
    }
}