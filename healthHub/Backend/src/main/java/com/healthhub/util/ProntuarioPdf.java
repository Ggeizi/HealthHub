package com.healthhub.util;

import java.io.ByteArrayOutputStream;

import com.healthhub.model.Prontuario;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public final class ProntuarioPdf {

    private ProntuarioPdf() {}

    public static byte[] gerar(Prontuario prontuario) {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            Document pdf = new Document();

            PdfWriter.getInstance(pdf, saida);
            pdf.open();

            pdf.add(new Paragraph("PRONTUÁRIO - HEALTHHUB"));
            pdf.add(new Paragraph("Hospital: " + prontuario.getHospital()));
            pdf.add(new Paragraph("Paciente: " + prontuario.getPaciente()));
            pdf.add(new Paragraph("Médico: " + prontuario.getMedico()));
            pdf.add(new Paragraph("Data: " + prontuario.getData()));

            pdf.add(new Paragraph("\nRELATO\n" + prontuario.getRelato()));
            pdf.add(new Paragraph("\nDIAGNÓSTICO\n" + prontuario.getDiagnostico()));
            pdf.add(new Paragraph("\nCONDUTA\n" + prontuario.getConduta()));

            pdf.close();
            return saida.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar PDF", e);
        }
    }
}