package com.healthhub;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

final class ProntuarioPdf {
    private static final Charset WIN_ANSI = Charset.forName("windows-1252");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int LARGURA_LINHA = 84;
    private static final int LINHAS_POR_PAGINA = 48;

    private ProntuarioPdf() {}

    static byte[] gerar(Atendimentos.Prontuario prontuario) {
        List<String> linhas = new ArrayList<>();
        adicionar(linhas, "Prontuário de atendimento - HealthHub");
        adicionar(linhas, "Hospital: " + prontuario.hospital());
        adicionar(linhas, "Paciente: " + prontuario.paciente());
        adicionar(linhas, "Médico: " + prontuario.medico());
        adicionar(linhas, "Data: " + LocalDateTime.parse(prontuario.data()).format(DATA));
        adicionar(linhas, "\nRelato\n" + prontuario.relato());
        adicionar(linhas, "\nDiagnóstico\n" + prontuario.diagnostico());
        adicionar(linhas, "\nConduta\n" + prontuario.conduta());
        return montar(linhas);
    }

    private static void adicionar(List<String> linhas, String texto) {
        for (String paragrafo : texto.replace("\t", "    ").split("\\R", -1)) {
            String restante = paragrafo;
            while (restante.length() > LARGURA_LINHA) {
                int corte = restante.lastIndexOf(' ', LARGURA_LINHA);
                if (corte <= 0) {
                    corte = LARGURA_LINHA;
                    if (Character.isHighSurrogate(restante.charAt(corte - 1))) {
                        corte--;
                    }
                }
                linhas.add(restante.substring(0, corte));
                restante = restante.substring(corte).stripLeading();
            }
            linhas.add(restante);
        }
    }

    private static byte[] montar(List<String> linhas) {
        int paginas = (linhas.size() + LINHAS_POR_PAGINA - 1) / LINHAS_POR_PAGINA;
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        escrever(pdf, "%PDF-1.4\n%âãÏÓ\n");
        objeto(pdf, offsets, "<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder filhos = new StringBuilder();
        for (int pagina = 0; pagina < paginas; pagina++) {
            filhos.append(4 + pagina * 2).append(" 0 R ");
        }
        objeto(pdf, offsets, "<< /Type /Pages /Count " + paginas + " /Kids [" + filhos + "] >>");
        objeto(pdf, offsets, "<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>");

        for (int pagina = 0; pagina < paginas; pagina++) {
            int conteudo = 5 + pagina * 2;
            objeto(pdf, offsets, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]"
                + " /Resources << /Font << /F1 3 0 R >> >> /Contents " + conteudo + " 0 R >>");
            StringBuilder stream = new StringBuilder("BT /F1 10 Tf 15 TL 45 790 Td\n");
            int inicio = pagina * LINHAS_POR_PAGINA;
            int fim = Math.min(linhas.size(), inicio + LINHAS_POR_PAGINA);
            for (String linha : linhas.subList(inicio, fim)) {
                stream.append('(').append(escapar(linha)).append(") Tj T*\n");
            }
            stream.append("ET\n");
            objeto(pdf, offsets, "<< /Length " + stream.toString().getBytes(WIN_ANSI).length
                + " >>\nstream\n" + stream + "endstream");
        }

        int xref = pdf.size();
        escrever(pdf, "xref\n0 " + (offsets.size() + 1) + "\n0000000000 65535 f \n");
        for (int offset : offsets) {
            String posicao = Integer.toString(offset);
            escrever(pdf, "0".repeat(10 - posicao.length()) + posicao + " 00000 n \n");
        }
        escrever(pdf, "trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\n"
            + "startxref\n" + xref + "\n%%EOF\n");
        return pdf.toByteArray();
    }

    private static String escapar(String texto) {
        return texto.replaceAll("\\p{Cntrl}", " ")
            .replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void objeto(ByteArrayOutputStream pdf, List<Integer> offsets, String conteudo) {
        offsets.add(pdf.size());
        escrever(pdf, offsets.size() + " 0 obj\n" + conteudo + "\nendobj\n");
    }

    private static void escrever(ByteArrayOutputStream pdf, String texto) {
        pdf.writeBytes(texto.getBytes(WIN_ANSI));
    }
}

