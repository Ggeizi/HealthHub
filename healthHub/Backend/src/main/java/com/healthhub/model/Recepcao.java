
package com.healthhub.model;

public class Recepcao {

    private Long hospitalId;
    private String foto;
    private String nome;
    private String login;
    private String senha;

    public Recepcao() {}

    public Long getHospitalId() { return hospitalId; }

    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
