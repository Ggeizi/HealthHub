package com.healthhub.service;

import com.healthhub.model.TextoProntuario;
import com.healthhub.model.Usuario;
import com.healthhub.repository.ProntuarioRepository;
import com.healthhub.util.Validacao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtendimentoService {

    private final ProntuarioRepository prontuarios;

    public AtendimentoService(ProntuarioRepository prontuarios) {
        this.prontuarios = prontuarios;
    }

    // Verificar se o médico pode acessar o atendimento
    public void conferirAtendimento(Usuario usuario, long id, boolean bloquear) {

        String status = prontuarios.buscarStatus(
            id,
            usuario.getId(),
            usuario.getHospitalId(),
            bloquear
        ).orElseThrow(() ->
            Validacao.ausente("Atendimento não encontrado.")
        );

        if ("CANCELADO".equals(status)) {
            throw Validacao.conflito("O atendimento foi cancelado.");
        }
    }

    // Validar e salvar o prontuário
    @Transactional
    public void salvar(Usuario usuario, long id, TextoProntuario dados) {

        String relato = Validacao.texto(
            dados.getRelato(), "relato", 1, 10000
        );

        String diagnostico = Validacao.texto(
            dados.getDiagnostico(), "diagnóstico", 1, 10000
        );

        String conduta = Validacao.texto(
            dados.getConduta(), "conduta", 1, 10000
        );

        conferirAtendimento(usuario, id, true);

        prontuarios.salvar(id, relato, diagnostico, conduta);
        prontuarios.concluirAgendamento(id);
    }
}