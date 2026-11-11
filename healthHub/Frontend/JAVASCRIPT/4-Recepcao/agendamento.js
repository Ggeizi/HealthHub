let medicos = [];
let pacientes = [];

async function carregarCadastros() {
    try {
        const resultados = await Promise.all([
            requisicao("/api/recepcao/medicos"),
            requisicao("/api/recepcao/pacientes")
        ]);

        medicos = resultados[0];
        pacientes = resultados[1];

        const selectMedico = document.getElementById(
            "medicoAgendamento"
        );

        const selectPaciente = document.getElementById(
            "pacienteAgendamento"
        );

        if (selectMedico) {
            selectMedico.innerHTML = `
                <option value="">
                    Selecione o médico
                </option>
            `;

            medicos.forEach(medico => {
                selectMedico.innerHTML += `
                    <option value="${medico.id}">
                        ${escaparHtml(medico.nome)}
                    </option>
                `;
            });
        }

        if (selectPaciente) {
            selectPaciente.innerHTML = `
                <option value="">
                    Selecione o paciente
                </option>
            `;

            pacientes.forEach(paciente => {
                selectPaciente.innerHTML += `
                    <option value="${paciente.id}">
                        ${escaparHtml(paciente.nome)}
                    </option>
                `;
            });
        }

    } catch (erro) {
        mostrarMensagem(
            "mensagem-agendamento",
            erro.message
        );
    }
}

async function criarAgendamento(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botao = formulario.querySelector("button[type='submit']");

    try {
        botao.disabled = true;
        botao.textContent = "Agendando...";

        await requisicao("/api/recepcao/agendamentos", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(
                dadosDoFormulario(formulario)
            )
        });

        formulario.reset();

        mostrarMensagem(
            "mensagem-agendamento",
            "Consulta agendada com sucesso.",
            "sucesso"
        );

        if (typeof carregarAgenda === "function") {
            carregarAgenda();
        }

    } catch (erro) {
        mostrarMensagem(
            "mensagem-agendamento",
            erro.message
        );
    } finally {
        botao.disabled = false;
        botao.textContent = "Agendar consulta";
    }
}

function inicializarAgendamento() {
    const formulario = document.getElementById(
        "formulario-agendamento"
    );

    if (formulario && !formulario.dataset.iniciado) {
        formulario.dataset.iniciado = "true";

        formulario.addEventListener(
            "submit",
            criarAgendamento
        );

        carregarCadastros();
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarAgendamento
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarAgendamento
);