async function buscarCsrf() {
    const resposta = await fetch("/api/csrf", {
        credentials: "same-origin"
    });

    return resposta.json();
}

async function requisicao(url, opcoes = {}) {
    const configuracao = {
        credentials: "same-origin",
        ...opcoes
    };

    if (configuracao.method && configuracao.method !== "GET") {
        const csrf = await buscarCsrf();

        configuracao.headers = {
            ...configuracao.headers,
            [csrf.header]: csrf.token
        };
    }

    const resposta = await fetch(url, configuracao);

    if (!resposta.ok) {
        let erro = {};

        try {
            erro = await resposta.json();
        } catch (_) {
            erro = {};
        }

        throw new Error(
            erro.erro ||
            erro.message ||
            "Não foi possível concluir a operação."
        );
    }

    return resposta.json();
}

function dadosDoFormulario(formulario) {
    return Object.fromEntries(
        new FormData(formulario).entries()
    );
}

function escaparHtml(valor) {
    return String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function mostrarMensagem(id, texto, tipo = "erro") {
    const elemento = document.getElementById(id);

    if (!elemento) {
        return;
    }

    elemento.textContent = texto;
    elemento.style.color = tipo === "sucesso"
        ? "#126a54"
        : "#a32532";
}

async function sair() {
    await requisicao("/api/logout", {
        method: "POST"
    });

    window.location.href = "/Login/index.html";
}

function inicializarRecepcao() {
    const botaoSair = document.getElementById("logout");

    if (botaoSair && !botaoSair.dataset.iniciado) {
        botaoSair.dataset.iniciado = "true";
        botaoSair.addEventListener("click", sair);
    }
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarRecepcao
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarRecepcao
);