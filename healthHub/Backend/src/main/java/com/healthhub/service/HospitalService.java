
package com.healthhub.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthhub.model.CadastroHospital;
import com.healthhub.repository.HospitalRepository;
import com.healthhub.util.Validacao;

@Service
public class HospitalService {

    private final HospitalRepository repository;
    private final PasswordEncoder encoder;

    public HospitalService(HospitalRepository repository,PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    // Cadastrar hospital e recepcionista
    @Transactional
    public long cadastrar(CadastroHospital dados) {

        String nome = Validacao.texto(
            dados.getNome(), "nome", 3, 160
        );

        String cnpj = Validacao.documento(
            dados.getCnpj(), "CNPJ", 14
        );

        String endereco = Validacao.texto(
            dados.getEndereco(), "endereço", 5, 255
        );

        String recepcionista = Validacao.texto(
            dados.getRecepcionista(), "recepcionista", 3, 160
        );

        String email = Validacao.email(dados.getLogin());
        String senha = Validacao.senha(dados.getSenha());

        String hash = encoder.encode(senha);

        long hospitalId = repository.cadastrar(
            nome, cnpj, endereco
        );

        repository.cadastrarRecepcionista(
            hospitalId, recepcionista, email, hash
        );

        return hospitalId;
    }

    // Listar hospitais
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        return repository.listar();
    }
}
