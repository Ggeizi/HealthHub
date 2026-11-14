package com.healthhub;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recepcao")
class Recepcao {
    private final JdbcClient banco;
    private final Acesso acesso;
    private final Agenda agenda;
    private final PasswordEncoder encoder;

    Recepcao(JdbcClient banco, Acesso acesso, Agenda agenda, PasswordEncoder encoder) {
        this.banco = banco;
        this.acesso = acesso;
        this.agenda = agenda;
        this.encoder = encoder;
    }

    @GetMapping("/medicos")
    List<Map<String, Object>> medicos(Principal sessao) {
        return listarUsuarios(hospital(sessao), "MEDICO");
    }

    @GetMapping("/pacientes")
    List<Map<String, Object>> pacientes(Principal sessao) {
        return listarUsuarios(hospital(sessao), "PACIENTE");
    }

    @PostMapping("/medicos")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Boolean> cadastrarMedico(Principal sessao, @RequestBody Medico dados) {
        banco.sql("""
            INSERT INTO usuarios (id_hospital, perfil, nome, login, senha_hash, crm, especialidade)
            VALUES (:hospital, 'MEDICO', :nome, :login, :senha, :crm, :especialidade)
            """).param("hospital", hospital(sessao))
            .param("nome", Validacao.texto(dados.nome(), "nome", 3, 160))
            .param("login", Validacao.email(dados.login()))
            .param("senha", encoder.encode(Validacao.senha(dados.senha())))
            .param("crm", Validacao.texto(dados.crm(), "CRM", 1, 40).toUpperCase(Locale.ROOT))
            .param("especialidade", Validacao.texto(dados.especialidade(), "especialidade", 1, 120))
            .update();
        return Map.of("ok", true);
    }

    @PostMapping("/pacientes")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Boolean> cadastrarPaciente(Principal sessao, @RequestBody Paciente dados) {
        if (dados.nascimento() == null || dados.nascimento().isAfter(LocalDate.now())
                || dados.nascimento().getYear() < 1000) {
            throw Validacao.invalido("Informe uma data de nascimento válida.");
        }
        banco.sql("""
            INSERT INTO usuarios (id_hospital, perfil, nome, login, senha_hash, cpf, nascimento, telefone)
            VALUES (:hospital, 'PACIENTE', :nome, :login, :senha, :cpf, :nascimento, :telefone)
            """).param("hospital", hospital(sessao))
            .param("nome", Validacao.texto(dados.nome(), "nome", 3, 160))
            .param("login", Validacao.email(dados.login()))
            .param("senha", encoder.encode(Validacao.senha(dados.senha())))
            .param("cpf", Validacao.documento(dados.cpf(), "CPF", 11))
            .param("nascimento", dados.nascimento())
            .param("telefone", Validacao.texto(dados.telefone(), "telefone", 1, 30))
            .update();
        return Map.of("ok", true);
    }

    @GetMapping("/agendamentos")
    List<Map<String, Object>> agendamentos(Principal sessao) {
        return agenda.listar(acesso.usuario(sessao));
    }

    @PostMapping("/agendamentos")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    Map<String, Boolean> agendar(Principal sessao, @RequestBody NovoAgendamento dados) {
        long hospital = hospital(sessao);
        if (dados.inicio() == null || dados.inicio().getYear() < 1000 || dados.inicio().getYear() > 9999
                || dados.inicio().getSecond() != 0 || dados.inicio().getNano() != 0) {
            throw Validacao.invalido("Informe a data e o horário em horas e minutos.");
        }
        String observacao = Validacao.texto(dados.observacao(), "observação", 0, 1000);
        conferirUsuario(dados.medicoId(), hospital, "MEDICO");
        conferirUsuario(dados.pacienteId(), hospital, "PACIENTE");

        // Serializa novos agendamentos do mesmo hospital para evitar reservas simultâneas.
        banco.sql("SELECT id FROM hospitais WHERE id = :id FOR UPDATE")
            .param("id", hospital).query(Long.class).single();
        boolean ocupado = banco.sql("""
            SELECT id FROM agendamentos
            WHERE id_hospital = :hospital AND inicio = :inicio AND status <> 'CANCELADO'
              AND (id_medico = :medico OR id_paciente = :paciente)
            LIMIT 1 FOR UPDATE
            """).param("hospital", hospital).param("inicio", dados.inicio())
            .param("medico", dados.medicoId()).param("paciente", dados.pacienteId())
            .query(Long.class).optional().isPresent();
        if (ocupado) {
            throw Validacao.conflito("Já existe uma consulta para o médico ou paciente nesse horário.");
        }

        banco.sql("""
            INSERT INTO agendamentos (id_hospital, id_medico, id_paciente, inicio, observacao)
            VALUES (:hospital, :medico, :paciente, :inicio, :observacao)
            """).param("hospital", hospital).param("medico", dados.medicoId())
            .param("paciente", dados.pacienteId()).param("inicio", dados.inicio())
            .param("observacao", observacao).update();
        return Map.of("ok", true);
    }

    @PatchMapping("/agendamentos/{id}/cancelar")
    @Transactional
    Map<String, Boolean> cancelar(Principal sessao, @PathVariable long id) {
        long hospital = hospital(sessao);
        String status = banco.sql("""
            SELECT status FROM agendamentos WHERE id = :id AND id_hospital = :hospital FOR UPDATE
            """).param("id", id).param("hospital", hospital).query(String.class).optional()
            .orElseThrow(() -> Validacao.ausente("Agendamento não encontrado."));
        if ("CONCLUIDO".equals(status)) {
            throw Validacao.conflito("Um atendimento concluído não pode ser cancelado.");
        }
        banco.sql("UPDATE agendamentos SET status = 'CANCELADO' WHERE id = :id")
            .param("id", id).update();
        return Map.of("ok", true);
    }

    private long hospital(Principal sessao) {
        return acesso.usuario(sessao).hospitalId();
    }

    private List<Map<String, Object>> listarUsuarios(long hospital, String perfil) {
        return banco.sql("""
            SELECT id, nome FROM usuarios WHERE id_hospital = :hospital AND perfil = :perfil
            ORDER BY nome, id
            """).param("hospital", hospital).param("perfil", perfil).query().listOfRows();
    }

    private void conferirUsuario(Long id, long hospital, String perfil) {
        if (id == null || banco.sql("""
            SELECT id FROM usuarios WHERE id = :id AND id_hospital = :hospital AND perfil = :perfil
            """).param("id", id).param("hospital", hospital).param("perfil", perfil)
                .query(Long.class).optional().isEmpty()) {
            throw Validacao.invalido("Selecione um médico e um paciente cadastrados neste hospital.");
        }
    }

    record Medico(String nome, String login, String senha, String crm, String especialidade) {}
    record Paciente(String nome, String login, String senha, String cpf, LocalDate nascimento,
                    String telefone) {}
    record NovoAgendamento(Long medicoId, Long pacienteId, LocalDateTime inicio, String observacao) {}
}
