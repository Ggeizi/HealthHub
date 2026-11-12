function formatarData(data) {
    return new Date(data).toLocaleString("pt-BR");
}

function criarProntuarioHtml(prontuario) {
    return `
        <article class="paciente__prontuario">

            <div class="paciente__prontuario-cabecalho">

                <div>
                    <h2>
                        Atendimento de
                        ${escaparHtml(
                            formatarData(prontuario.data)
                        )}
                    </h2>

                    <p class="paciente__prontuario-data">
                        Médico:
                        ${escaparHtml(prontuario.medico)}
                    </p>
                </div>

                <button
                    type="button"
                    class="botao-pdf"
                    data-prontuario-id="${prontuario.id}"
                >
                    Baixar PDF
                </button>

            </div>

            <div class="paciente__campo">
                <strong>Relato</strong>

                <p>
                    ${escaparHtml(prontuario.relato)}
                </p>
            </div>

            <div class="paciente__campo">
                <strong>Diagnóstico</strong>

                <p>
                    ${escaparHtml(prontuario.diagnostico)}
                </p>
            </div>

            <div class="paciente__campo">
                <strong>Conduta</strong>

                <p>
                    ${escaparHtml(prontuario.conduta)}
                </p>
            </div>

        </article>
    `;
}

async function carregarProntuarios() {
    const lista = document.getElementById(
        "lista-prontuarios"
    );

    if (!lista) {
        return;
    }

    lista.innerHTML = `
        <p class="paciente__carregando">
            Carregando prontuários...
        </p>
    `;

    try {
        const prontuarios = await requisicao(
            "/api/paciente/prontuarios"
        );

        if (prontuarios.length === 0) {
            lista.innerHTML = `
                <p class="paciente__vazio">
                    Você ainda não possui prontuários.
                </p>
            `;

            return;
        }

        lista.innerHTML = prontuarios
            .map(criarProntuarioHtml)
            .join("");

        document
            .querySelectorAll("[data-prontuario-id]")
            .forEach(botao => {
                botao.addEventListener(
                    "click",
                    baixarPdf
                );
            });

    } catch (erro) {
        lista.innerHTML = "";
        mostrarMensagem(erro.message);
    }
}

function inicializarProntuarios() {
    const lista = document.getElementById(
        "lista-prontuarios"
    );

    const botaoAtualizar = document.getElementById(
        "atualizar-prontuarios"
    );

    if (lista && !lista.dataset.iniciado) {
        lista.dataset.iniciado = "true";
        carregarProntuarios();
    }

    if (
        botaoAtualizar &&
        !botaoAtualizar.dataset.iniciado
    ) {
        botaoAtualizar.dataset.iniciado = "true";

        botaoAtualizar.addEventListener(
            "click",
            carregarProntuarios
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarProntuarios
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarProntuarios
);