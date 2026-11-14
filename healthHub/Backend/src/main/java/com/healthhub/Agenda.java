package com.healthhub;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
class Agenda {
    private final JdbcClient banco;

    Agenda(JdbcClient banco) {
        this.banco = banco;
    }

    List<Map<String, Object>> listar(Acesso.Usuario usuario) {
        String filtro = switch (usuario.perfil()) {
            case "RECEPCAO" -> "";
            case "MEDICO" -> " AND a.id_medico = :usuario";
            case "PACIENTE" -> " AND a.id_paciente = :usuario";
            default -> throw new IllegalArgumentException("Perfil sem agenda.");
        };
        var consulta = banco.sql("""
            SELECT a.id, DATE_FORMAT(a.inicio, '%Y-%m-%dT%H:%i:%s') AS inicio, a.status,
                   m.nome AS medico, p.nome AS paciente
            """ + ("PACIENTE".equals(usuario.perfil()) ? "" : ", a.observacao, r.id AS prontuarioId")
            + """
              FROM agendamentos a
              JOIN usuarios m ON m.id = a.id_medico
              JOIN usuarios p ON p.id = a.id_paciente
              LEFT JOIN prontuarios r ON r.id_agendamento = a.id
              WHERE a.id_hospital = :hospital
            """ + filtro + " ORDER BY a.inicio, a.id").param("hospital", usuario.hospitalId());
        if (!filtro.isEmpty()) {
            consulta.param("usuario", usuario.id());
        }
        return consulta.query().listOfRows();
    }
}
