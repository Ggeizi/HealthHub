package com.healthhub.service;

import com.healthhub.model.NovoAgendamento;
import com.healthhub.repository.AgendamentoRepository;
import com.healthhub.repository.UsuarioRepository;
import com.healthhub.util.Validacao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentos;
    private final UsuarioRepository usuarios;

    public AgendamentoService(
            AgendamentoRepository agendamentos,
            UsuarioRepository usuarios) {

        this.agendamentos = agendamentos;
        this.usuarios = usuarios;
    }

    // Cadastrar agendamento
    @Transactional
    public void agendar(long hospital, NovoAgendamento dados) {

        if (dados.getInicio() == null
                || dados.getInicio().getYear() < 1000
                || dados.getInicio().getYear() > 9999
                || dados.getInicio().getSecond() != 0
                || dados.getInicio().getNano() != 0) {

            throw Validacao.invalido(
                "Informe a data e o horário em horas e minutos."
            );
        }

        dados.setObservacao(Validacao.texto(
            dados.getObservacao(), "observação", 0, 1000
        ));

        conferirUsuario(dados.getMedicoId(), hospital, "MEDICO");
        conferirUsuario(dados.getPacienteId(), hospital, "PACIENTE");

        agendamentos.bloquearHospital(hospital);

        if (agendamentos.horarioOcupado(hospital, dados)) {
            throw Validacao.conflito(
                "Já existe uma consulta para o médico ou paciente nesse horário."
            );
        }

        agendamentos.cadastrar(hospital, dados);
    }

    // Cancelar agendamento
    @Transactional
    public void cancelar(long hospital, long id) {

        String status = agendamentos.buscarStatus(id, hospital)
            .orElseThrow(() ->
                Validacao.ausente("Agendamento não encontrado.")
            );

        if ("CONCLUIDO".equals(status)) {
            throw Validacao.conflito(
                "Um atendimento concluído não pode ser cancelado."
            );
        }

        agendamentos.cancelar(id);
    }

    // Validar médico e paciente
    private void conferirUsuario(Long id, long hospital, String perfil) {

        if (id == null || !usuarios.existeNoHospital(id, hospital, perfil)) {
            throw Validacao.invalido(
                "Selecione um médico e um paciente cadastrados neste hospital."
            );
        }
    }
}