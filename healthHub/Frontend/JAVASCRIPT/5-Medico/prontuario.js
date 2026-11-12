async function abrirProntuario(event) {
    const botao = event.currentTarget;

    const id = botao.dataset.agendamentoId;
    const paciente = botao.dataset.paciente;

    const formulario = document.getElementById(
        "formulario-prontuario"
    );

    formulario.elements.agendamentoId.value = id;

    document.getElementById(
        "paciente-selecionado"
    ).textContent = `Paciente: ${paciente}`;

    try {
        const prontuario = await requisicao(
            `/api/medico/agendamentos/${id}/prontuario`
        );

        formulario.elements.relato.value =
            prontuario.relato || "";

        formulario.elements.diagnostico.value =
            prontuario.diagnostico || "";

        formulario.elements.conduta.value =
            prontuario.conduta || "";

        document
            .getElementById("prontuario")
            .scrollIntoView({
                behavior: "smooth"
            });

    } catch (erro) {
        mostrarMensagem(
            "mensagem-prontuario",
            erro.message
        );
    }
}

async function salvarProntuario(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const id = formulario.elements.agendamentoId.value;
    const botao = formulario.querySelector(
        "button[type='submit']"
    );

    if (!id) {
        mostrarMensagem(
            "mensagem-prontuario",
            "Selecione um atendimento na agenda."
        );

        return;
    }

    try {
        botao.disabled = true;
        botao.textContent = "Salvando...";

        await requisicao(
            `/api/medico/agendamentos/${id}/prontuario`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(
                    dadosDoFormulario(formulario)
                )
            }
        );

        mostrarMensagem(
            "mensagem-prontuario",
            "Prontuário salvo com sucesso.",
            "sucesso"
        );

        if (
            typeof carregarAgendaMedica ===
            "function"
        ) {
            carregarAgendaMedica();
        }

    } catch (erro) {
        mostrarMensagem(
            "mensagem-prontuario",
            erro.message
        );
    } finally {
        botao.disabled = false;
        botao.textContent = "Salvar prontuário";
    }
}

function inicializarProntuario() {
    const formulario = document.getElementById(
        "formulario-prontuario"
    );

    if (
        formulario &&
        !formulario.dataset.iniciado
    ) {
        formulario.dataset.iniciado = "true";

        formulario.addEventListener(
            "submit",
            salvarProntuario
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarProntuario
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarProntuario
);