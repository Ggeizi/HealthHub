package com.healthhub;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
class Atendimentos {
    private static final String PRONTUARIOS_DO_PACIENTE = """
        SELECT r.id, DATE_FORMAT(a.inicio, '%Y-%m-%dT%H:%i:%s') AS data, m.nome AS medico,
               p.nome AS paciente, h.nome AS hospital, r.relato, r.diagnostico, r.conduta
        FROM prontuarios r
        JOIN agendamentos a ON a.id = r.id_agendamento
        JOIN usuarios m ON m.id = a.id_medico
        JOIN usuarios p ON p.id = a.id_paciente
        JOIN hospitais h ON h.id = a.id_hospital
        WHERE a.id_paciente = :paciente AND a.id_hospital = :hospital
        """;

    private final JdbcClient banco;
    private final Acesso acesso;
    private final Agenda agenda;

    Atendimentos(JdbcClient banco, Acesso acesso, Agenda agenda) {
        this.banco = banco;
        this.acesso = acesso;
        this.agenda = agenda;
    }

    @GetMapping({"/api/medico/agendamentos", "/api/paciente/agendamentos"})
    List<Map<String, Object>> agendamentos(Principal sessao) {
        return agenda.listar(acesso.usuario(sessao));
    }

    @GetMapping("/api/medico/agendamentos/{id}/prontuario")
    TextoProntuario abrir(Principal sessao, @PathVariable long id) {
        conferirAtendimento(acesso.usuario(sessao), id, false);
        return banco.sql("SELECT relato, diagnostico, conduta FROM prontuarios WHERE id_agendamento = :id")
            .param("id", id).query(TextoProntuario.class).optional()
            .orElse(new TextoProntuario("", "", ""));
    }

    @PutMapping("/api/medico/agendamentos/{id}/prontuario")
    @Transactional
    Map<String, Boolean> salvar(Principal sessao, @PathVariable long id,
                                @RequestBody TextoProntuario dados) {
        String relato = Validacao.texto(dados.relato(), "relato", 1, 10000);
        String diagnostico = Validacao.texto(dados.diagnostico(), "diagnóstico", 1, 10000);
        String conduta = Validacao.texto(dados.conduta(), "conduta", 1, 10000);
        conferirAtendimento(acesso.usuario(sessao), id, true);

        banco.sql("""
            INSERT INTO prontuarios (id_agendamento, relato, diagnostico, conduta)
            VALUES (:id, :relato, :diagnostico, :conduta)
            ON DUPLICATE KEY UPDATE relato = :relato, diagnostico = :diagnostico, conduta = :conduta
            """).param("id", id).param("relato", relato)
            .param("diagnostico", diagnostico).param("conduta", conduta).update();
        banco.sql("UPDATE agendamentos SET status = 'CONCLUIDO' WHERE id = :id")
            .param("id", id).update();
        return Map.of("ok", true);
    }

    @GetMapping("/api/paciente/prontuarios")
    List<Prontuario> prontuarios(Principal sessao) {
        Acesso.Usuario usuario = acesso.usuario(sessao);
        return consultaDoPaciente(usuario, " ORDER BY a.inicio DESC, r.id DESC")
            .query(Prontuario.class).list();
    }

    @GetMapping("/api/paciente/prontuarios/{id}/pdf")
    ResponseEntity<byte[]> baixar(Principal sessao, @PathVariable long id) {
        Acesso.Usuario usuario = acesso.usuario(sessao);
        Prontuario prontuario = consultaDoPaciente(usuario, " AND r.id = :id")
            .param("id", id).query(Prontuario.class).optional()
            .orElseThrow(() -> Validacao.ausente("Prontuário não encontrado."));
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename("prontuario-" + id + ".pdf").build().toString())
            .body(ProntuarioPdf.gerar(prontuario));
    }

    private JdbcClient.StatementSpec consultaDoPaciente(Acesso.Usuario usuario, String complemento) {
        return banco.sql(PRONTUARIOS_DO_PACIENTE + complemento)
            .param("paciente", usuario.id()).param("hospital", usuario.hospitalId());
    }

    private void conferirAtendimento(Acesso.Usuario usuario, long id, boolean bloquear) {
        String status = banco.sql("""
            SELECT status FROM agendamentos
            WHERE id = :id AND id_medico = :medico AND id_hospital = :hospital
            """ + (bloquear ? " FOR UPDATE" : ""))
            .param("id", id).param("medico", usuario.id()).param("hospital", usuario.hospitalId())
            .query(String.class).optional()
            .orElseThrow(() -> Validacao.ausente("Atendimento não encontrado."));
        if ("CANCELADO".equals(status)) {
            throw Validacao.conflito("O atendimento foi cancelado.");
        }
    }

    record TextoProntuario(String relato, String diagnostico, String conduta) {}
    record Prontuario(long id, String data, String medico, String paciente, String hospital,
                      String relato, String diagnostico, String conduta) {}
}
