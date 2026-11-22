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

async function carregarAgenda() {
    const tabela = document.getElementById(
        "tabela-agenda"
    );

    if (!tabela) {
        return;
    }

    try {
        const agendamentos = await requisicao(
            "/api/recepcao/agendamentos"
        );

        if (agendamentos.length === 0) {
            tabela.innerHTML = `
                <tr>
                    <td colspan="5">
                        Nenhum agendamento encontrado.
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
                    ${escaparHtml(agendamento.medico)}
                </td>

                <td>
                    <span class="tabela__status ${classeStatus(
                        agendamento.status
                    )}">
                        ${escaparHtml(agendamento.status)}
                    </span>
                </td>

                <td>
                    ${
                        agendamento.status === "AGENDADO"
                            ? `
                                <button
                                    class="botao-cancelar"
                                    data-id="${agendamento.id}"
                                    type="button"
                                >
                                    Cancelar
                                </button>
                            `
                            : "—"
                    }
                </td>
            </tr>
        `).join("");

        document
            .querySelectorAll(".botao-cancelar")
            .forEach(botao => {
                botao.addEventListener(
                    "click",
                    cancelarAgendamento
                );
            });

    } catch (erro) {
        mostrarMensagem(
            "mensagem-agenda",
            erro.message
        );
    }
}

async function cancelarAgendamento(event) {
    const id = event.currentTarget.dataset.id;

    if (!window.confirm("Deseja cancelar este agendamento?")) {
        return;
    }

    try {
        await requisicao(
            `/api/recepcao/agendamentos/${id}/cancelar`,
            {
                method: "PATCH"
            }
        );

        await carregarAgenda();

    } catch (erro) {
        mostrarMensagem(
            "mensagem-agenda",
            erro.message
        );
    }
}

function inicializarAgenda() {
    const tabela = document.getElementById(
        "tabela-agenda"
    );

    const botaoAtualizar = document.getElementById(
        "atualizar-agenda"
    );

    if (tabela && !tabela.dataset.iniciado) {
        tabela.dataset.iniciado = "true";
        carregarAgenda();
    }

    if (
        botaoAtualizar &&
        !botaoAtualizar.dataset.iniciado
    ) {
        botaoAtualizar.dataset.iniciado = "true";

        botaoAtualizar.addEventListener(
            "click",
            carregarAgenda
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarAgenda
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarAgenda
);