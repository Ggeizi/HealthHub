package com.healthhub.model;

public class Prontuario {

    private long id;
    private String data;
    private String medico;
    private String paciente;
    private String hospital;
    private String relato;
    private String diagnostico;
    private String conduta;

    public Prontuario() {}

    public Prontuario(long id, String data, String medico,
                     String paciente, String hospital, String relato,
                     String diagnostico, String conduta) {
        this.id = id;
        this.data = data;
        this.medico = medico;
        this.paciente = paciente;
        this.hospital = hospital;
        this.relato = relato;
        this.diagnostico = diagnostico;
        this.conduta = conduta;
    }

        public long getId() {
        return id;
        }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRelato() {
        return relato;
    }

    public void setRelato(String relato) {
        this.relato = relato;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getConduta() {
        return conduta;
    }

    public void setConduta(String conduta) {
        this.conduta = conduta;
    }
}