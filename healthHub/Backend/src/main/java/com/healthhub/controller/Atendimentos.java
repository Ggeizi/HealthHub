package com.healthhub.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.healthhub.model.Prontuario;
import com.healthhub.model.TextoProntuario;
import com.healthhub.model.Usuario;
import com.healthhub.repository.Agenda;
import com.healthhub.repository.ProntuarioRepository;
import com.healthhub.service.AtendimentoService;
import com.healthhub.util.ProntuarioPdf;
import com.healthhub.util.Validacao;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Atendimentos {

    private final Acesso acesso;
    private final Agenda agenda;
    private final ProntuarioRepository prontuarios;
    private final AtendimentoService service;

    public Atendimentos(
            Acesso acesso,
            Agenda agenda,
            ProntuarioRepository prontuarios,
            AtendimentoService service) {

        this.acesso = acesso;
        this.agenda = agenda;
        this.prontuarios = prontuarios;
        this.service = service;
    }

    // Listar agendamentos
    @GetMapping({"/api/medico/agendamentos", "/api/paciente/agendamentos"})
    public List<Map<String, Object>> agendamentos(Principal sessao) {
        return agenda.listar(acesso.usuario(sessao));
    }

    // Abrir prontuário
    @GetMapping("/api/medico/agendamentos/{id}/prontuario")
    public TextoProntuario abrir(Principal sessao, @PathVariable long id) {

        service.conferirAtendimento(acesso.usuario(sessao), id, false);

        return prontuarios.buscarTexto(id)
            .orElse(new TextoProntuario("", "", ""));
    }

    // Salvar prontuário
    @PutMapping("/api/medico/agendamentos/{id}/prontuario")
    public Map<String, Boolean> salvar(
            Principal sessao,
            @PathVariable long id,
            @RequestBody TextoProntuario dados) {

        service.salvar(acesso.usuario(sessao), id, dados);

        return Map.of("ok", true);
    }

    // Listar prontuários do paciente
    @GetMapping("/api/paciente/prontuarios")
    public List<Prontuario> prontuarios(Principal sessao) {

        Usuario usuario = acesso.usuario(sessao);

        return prontuarios.listar(
            usuario.getId(),
            usuario.getHospitalId()
        );
    }

    // Baixar prontuário em PDF
    @GetMapping("/api/paciente/prontuarios/{id}/pdf")
    public ResponseEntity<byte[]> baixar(
            Principal sessao,
            @PathVariable long id) {

        Usuario usuario = acesso.usuario(sessao);

        Prontuario prontuario = prontuarios.buscarPorId(
            id,
            usuario.getId(),
            usuario.getHospitalId()
        ).orElseThrow(() ->
            Validacao.ausente("Prontuário não encontrado.")
        );

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/pdf"))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename("prontuario-" + id + ".pdf")
                    .build()
                    .toString()
            )
            .body(ProntuarioPdf.gerar(prontuario));
    }
}