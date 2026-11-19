package com.healthhub.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.healthhub.model.Medico;
import com.healthhub.model.Paciente;
import com.healthhub.model.NovoAgendamento;
import com.healthhub.repository.Agenda;
import com.healthhub.service.RecepcaoService;
import com.healthhub.service.AgendamentoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recepcao")
public class Recepcao {

    private final Acesso acesso;
    private final Agenda agenda;
    private final RecepcaoService recepcao;
    private final AgendamentoService agendamentos;

    public Recepcao(Acesso acesso, Agenda agenda,
                    RecepcaoService recepcao,
                    AgendamentoService agendamentos) {
        this.acesso = acesso;
        this.agenda = agenda;
        this.recepcao = recepcao;
        this.agendamentos = agendamentos;
    }

    private long hospital(Principal sessao) {
        return acesso.usuario(sessao).getHospitalId();
    }

    @GetMapping("/medicos")
    public List<Map<String, Object>> medicos(Principal sessao) {
        return recepcao.listar(hospital(sessao), "MEDICO");
    }

    @GetMapping("/pacientes")
    public List<Map<String, Object>> pacientes(Principal sessao) {
        return recepcao.listar(hospital(sessao), "PACIENTE");
    }

    @PostMapping("/medicos")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> cadastrarMedico(
            Principal sessao, @RequestBody Medico dados) {
        recepcao.cadastrarMedico(hospital(sessao), dados);
        return Map.of("ok", true);
    }

    @PostMapping("/pacientes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> cadastrarPaciente(
            Principal sessao, @RequestBody Paciente dados) {
        recepcao.cadastrarPaciente(hospital(sessao), dados);
        return Map.of("ok", true);
    }

    @GetMapping("/agendamentos")
    public List<Map<String, Object>> listarAgendamentos(Principal sessao) {
        return agenda.listar(acesso.usuario(sessao));
    }

    @PostMapping("/agendamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> agendar(
            Principal sessao, @RequestBody NovoAgendamento dados) {
        agendamentos.agendar(hospital(sessao), dados);
        return Map.of("ok", true);
    }

    @PatchMapping("/agendamentos/{id}/cancelar")
    public Map<String, Boolean> cancelar(
            Principal sessao, @PathVariable long id) {
        agendamentos.cancelar(hospital(sessao), id);
        return Map.of("ok", true);
    }
}