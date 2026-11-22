package com.healthhub.service;

import java.util.List;
import java.util.Map;
import com.healthhub.model.CadastroHospital;
import com.healthhub.model.Recepcao;
import com.healthhub.repository.HospitalRepository;
import com.healthhub.util.Validacao;
import com.healthhub.util.Imagem;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HospitalService {
    private final HospitalRepository repository;
    private final PasswordEncoder encoder;

    public HospitalService(HospitalRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public long cadastrar(CadastroHospital dados) {
        String nome = Validacao.texto(dados.getNome(), "nome", 3, 160);
        String cnpj = Validacao.documento(dados.getCnpj(), "CNPJ", 14);
        String endereco = Validacao.texto(dados.getEndereco(), "endereço", 5, 255);
        return repository.cadastrar(nome, cnpj, endereco, Imagem.validar(dados.getFoto()));
    }

    @Transactional
    public void cadastrarRecepcao(Recepcao dados) {
        if (dados.getHospitalId() == null || !repository.existe(dados.getHospitalId())) {
            throw Validacao.invalido("Selecione um hospital cadastrado.");
        }
        String nome = Validacao.texto(dados.getNome(), "nome", 3, 160);
        String login = Validacao.email(dados.getLogin());
        String senha = encoder.encode(Validacao.senha(dados.getSenha()));
        repository.cadastrarRecepcionista(dados.getHospitalId(), nome, login, senha,
                Imagem.validar(dados.getFoto()));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() { return repository.listar(); }
}
