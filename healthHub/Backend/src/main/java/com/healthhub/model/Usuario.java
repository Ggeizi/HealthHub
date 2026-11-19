package com.healthhub.model;

public class Usuario {

    private long id;
    private String nome;
    private String login;
    private String perfil;
    private Long hospitalId;

    public Usuario() {}

    public Usuario(long id, String nome, String login, String perfil, Long hospitalId) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
        this.hospitalId = hospitalId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
}