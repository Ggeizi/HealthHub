package com.healthhub.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.healthhub.model.Medico;
import com.healthhub.model.Paciente;
import com.healthhub.repository.UsuarioRepository;
import com.healthhub.util.Imagem;
import com.healthhub.util.Validacao;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RecepcaoService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public RecepcaoService(
            UsuarioRepository usuarios,
            PasswordEncoder encoder) {

        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    // Listar médicos ou pacientes
    public List<Map<String, Object>> listar(long hospital, String perfil) {
        return usuarios.listar(hospital, perfil);
    }

    // Cadastrar médico
    public void cadastrarMedico(long hospital, Medico medico) {

        medico.setNome(Validacao.texto(medico.getNome(), "nome", 3, 160));
        medico.setLogin(Validacao.email(medico.getLogin()));
        medico.setCrm(Validacao.texto(
            medico.getCrm(), "CRM", 1, 40).toUpperCase(Locale.ROOT));
        medico.setEspecialidade(Validacao.texto(
            medico.getEspecialidade(), "especialidade", 1, 120));
        medico.setFoto(Imagem.validar(medico.getFoto()));

        String hash = encoder.encode(Validacao.senha(medico.getSenha()));

        usuarios.cadastrarMedico(hospital, medico, hash);
    }

    // Cadastrar paciente
    public void cadastrarPaciente(long hospital, Paciente paciente) {

        LocalDate nascimento = paciente.getNascimento();

        if (nascimento == null || nascimento.isAfter(LocalDate.now())
                || nascimento.getYear() < 1000) {
            throw Validacao.invalido("Informe uma data de nascimento válida.");
        }

        paciente.setNome(Validacao.texto(paciente.getNome(), "nome", 3, 160));
        paciente.setLogin(Validacao.email(paciente.getLogin()));
        paciente.setCpf(Validacao.documento(paciente.getCpf(), "CPF", 11));
        paciente.setTelefone(Validacao.texto(
            paciente.getTelefone(), "telefone", 1, 30));
        paciente.setFoto(Imagem.validar(paciente.getFoto()));

        String hash = encoder.encode(Validacao.senha(paciente.getSenha()));

        usuarios.cadastrarPaciente(hospital, paciente, hash);
    }
}