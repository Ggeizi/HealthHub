async function baixarPdf(event) {
    const botao = event.currentTarget;
    const id = botao.dataset.prontuarioId;

    try {
        botao.disabled = true;
        botao.textContent = "Gerando PDF...";

        const resposta = await fetch(
            `/api/paciente/prontuarios/${id}/pdf`,
            {
                credentials: "same-origin"
            }
        );

        if (!resposta.ok) {
            let erro = {};

            try {
                erro = await resposta.json();
            } catch (_) {
                erro = {};
            }

            throw new Error(
                erro.erro ||
                "Não foi possível gerar o PDF."
            );
        }

        const arquivo = await resposta.blob();
        const url = URL.createObjectURL(arquivo);

        const link = document.createElement("a");

        link.href = url;
        link.download = `prontuario-${id}.pdf`;

        document.body.appendChild(link);
        link.click();
        link.remove();

        URL.revokeObjectURL(url);

        mostrarMensagem(
            "Prontuário baixado com sucesso.",
            "sucesso"
        );

    } catch (erro) {
        mostrarMensagem(erro.message);
    } finally {
        botao.disabled = false;
        botao.textContent = "Baixar PDF";
    }
}