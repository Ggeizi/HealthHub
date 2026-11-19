package com.healthhub.model;

public class TextoProntuario {

    private String relato;
    private String diagnostico;
    private String conduta;

    public TextoProntuario() {}

    public TextoProntuario(
            String relato,
            String diagnostico,
            String conduta) {

        this.relato = relato;
        this.diagnostico = diagnostico;
        this.conduta = conduta;
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