package com.healthhub.model;

// Identifica a classe como uma entidade JPA
import jakarta.persistence.Entity;

// Configura a geração automática do ID
import jakarta.persistence.GeneratedValue;

// Define a estratégia de geração do ID
import jakarta.persistence.GenerationType;

// Identifica a chave primária da entidade
import jakarta.persistence.Id;

// Define o nome da tabela no banco de dados
import jakarta.persistence.Table;

@Entity
@Table(name = "teste")
public class Teste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    



}