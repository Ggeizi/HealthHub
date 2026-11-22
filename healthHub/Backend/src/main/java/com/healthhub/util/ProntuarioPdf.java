
package com.healthhub.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.healthhub.model.Prontuario;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.AcroFields;

public final class ProntuarioPdf {

    private ProntuarioPdf() {}

    public static byte[] gerar(Prontuario prontuario) {
        try (
            InputStream modelo = ProntuarioPdf.class
                .getResourceAsStream("/pdf/modelo-prontuario.pdf");

            ByteArrayOutputStream saida = new ByteArrayOutputStream()
        ) {
            if (modelo == null) {
                throw new IllegalStateException(
                    "Modelo de prontuário não encontrado."
                );
            }

            PdfReader leitor = new PdfReader(modelo);

            try {
                PdfStamper editor = new PdfStamper(leitor, saida);

                try {
                    AcroFields campos = editor.getAcroFields();

                    campos.setField("hospital", prontuario.getHospital());
                    campos.setField("paciente", prontuario.getPaciente());
                    campos.setField("medico", prontuario.getMedico());
                    campos.setField("data", String.valueOf(prontuario.getData()));

                    campos.setField("relato", prontuario.getRelato());
                    campos.setField("diagnostico", prontuario.getDiagnostico());
                    campos.setField("conduta", prontuario.getConduta());

                    // Transforma os campos preenchidos em conteúdo fixo.
                    editor.setFormFlattening(true);

                } finally {
                    editor.close();
                }
            } finally {
                leitor.close();
            }

            return saida.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException(
                "Erro ao gerar PDF do prontuário.", e
            );
        }
    }
}
