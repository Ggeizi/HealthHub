function formatarData(data) {
    return new Date(data).toLocaleString("pt-BR");
}

function classeStatus(status) {
    const classes = {
        CANCELADO: "tabela__status--cancelado",
        CONCLUIDO: "tabela__status--concluido"
    };

    return classes[status] || "";
}

async function carregarAgendaMedica() {
    const tabela = document.getElementById(
        "tabela-agenda-medica"
    );

    if (!tabela) {
        return;
    }

    try {
        const agendamentos = await requisicao(
            "/api/medico/agendamentos"
        );

        if (agendamentos.length === 0) {
            tabela.innerHTML = `
                <tr>
                    <td colspan="5">
                        Nenhum atendimento encontrado.
                    </td>
                </tr>
            `;

            return;
        }

        tabela.innerHTML = agendamentos.map(agendamento => `
            <tr>
                <td>
                    ${escaparHtml(
                        formatarData(agendamento.inicio)
                    )}
                </td>

                <td>
                    ${escaparHtml(agendamento.paciente)}
                </td>

                <td>
                    <span class="tabela__status ${classeStatus(
                        agendamento.status
                    )}">
                        ${escaparHtml(agendamento.status)}
                    </span>
                </td>

                <td>
                    ${escaparHtml(
                        agendamento.observacao
                    )}
                </td>

                <td>
                    ${
                        agendamento.status === "CANCELADO"
                            ? "—"
                            : `
                                <button
                                    type="button"
                                    class="botao-prontuario"
                                    data-agendamento-id="${agendamento.id}"
                                    data-paciente="${escaparHtml(
                                        agendamento.paciente
                                    )}"
                                >
                                    ${
                                        agendamento.prontuarioId
                                            ? "Editar"
                                            : "Preencher"
                                    }
                                </button>
                            `
                    }
                </td>
            </tr>
        `).join("");

        document
            .querySelectorAll(".botao-prontuario")
            .forEach(botao => {
                botao.addEventListener(
                    "click",
                    abrirProntuario
                );
            });

    } catch (erro) {
        mostrarMensagem(
            "mensagem-agenda-medica",
            erro.message
        );
    }
}

function inicializarAgendaMedica() {
    const tabela = document.getElementById(
        "tabela-agenda-medica"
    );

    const botaoAtualizar = document.getElementById(
        "atualizar-agenda-medica"
    );

    if (tabela && !tabela.dataset.iniciado) {
        tabela.dataset.iniciado = "true";
        carregarAgendaMedica();
    }

    if (
        botaoAtualizar &&
        !botaoAtualizar.dataset.iniciado
    ) {
        botaoAtualizar.dataset.iniciado = "true";

        botaoAtualizar.addEventListener(
            "click",
            carregarAgendaMedica
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarAgendaMedica
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarAgendaMedica
);