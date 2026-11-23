function lerFoto(formulario) {
    const campoFoto = formulario.querySelector('[name="foto"]');
    const arquivo = campoFoto?.files?.[0];

    if (!arquivo) {
        return Promise.resolve(null);
    }

    return new Promise(resolve => {
        const leitor = new FileReader();

        leitor.onload = () => resolve(leitor.result);
        leitor.onerror = () => resolve(null);

        leitor.readAsDataURL(arquivo);
    });
}

async function cadastrarMedico(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botao = formulario.querySelector("button[type='submit']");

    try {
        botao.disabled = true;
        botao.textContent = "Cadastrando...";

        const dados = dadosDoFormulario(formulario);
        dados.foto = await lerFoto(formulario);

        await requisicao("/api/recepcao/medicos", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        formulario.reset();
        atualizarTotais();

        mostrarMensagem(
            "mensagem-medico",
            "Médico cadastrado com sucesso.",
            "sucesso"
        );

        if (typeof carregarCadastros === "function") {
            carregarCadastros();
        }

    } catch (erro) {
        mostrarMensagem(
            "mensagem-medico",
            erro.message
        );
    } finally {
        botao.disabled = false;
        botao.textContent = "Cadastrar médico";
    }
}

async function cadastrarPaciente(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botao = formulario.querySelector("button[type='submit']");

    try {
        botao.disabled = true;
        botao.textContent = "Cadastrando...";

        const dados = dadosDoFormulario(formulario);
        dados.foto = await lerFoto(formulario);

        await requisicao("/api/recepcao/pacientes", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        formulario.reset();
        atualizarTotais();

        mostrarMensagem(
            "mensagem-paciente",
            "Paciente cadastrado com sucesso.",
            "sucesso"
        );

        if (typeof carregarCadastros === "function") {
            carregarCadastros();
        }

    } catch (erro) {
        mostrarMensagem(
            "mensagem-paciente",
            erro.message
        );
    } finally {
        botao.disabled = false;
        botao.textContent = "Cadastrar paciente";
    }
}

function inicializarCadastros() {
    const formularioMedico = document.getElementById(
        "formulario-medico"
    );

    const formularioPaciente = document.getElementById(
        "formulario-paciente"
    );

    if (
        formularioMedico &&
        !formularioMedico.dataset.iniciado
    ) {
        formularioMedico.dataset.iniciado = "true";

        formularioMedico.addEventListener(
            "submit",
            cadastrarMedico
        );
    }

    if (
        formularioPaciente &&
        !formularioPaciente.dataset.iniciado
    ) {
        formularioPaciente.dataset.iniciado = "true";

        formularioPaciente.addEventListener(
            "submit",
            cadastrarPaciente
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarCadastros
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarCadastros
);