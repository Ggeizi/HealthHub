let painelInicializado = false;

function escaparHtml(valor) {
    return String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

async function requisicao(url, opcoes = {}) {
    const configuracao = {
        credentials: "same-origin",
        ...opcoes
    };

    if (configuracao.method && configuracao.method !== "GET") {
        const csrf = await fetch("/api/csrf", {
            credentials: "same-origin"
        }).then(resposta => resposta.json());

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

function mostrarMensagem(texto, tipo = "erro") {
    const mensagem = document.getElementById("mensagem-hospitais");

    if (!mensagem) {
        return;
    }

    mensagem.textContent = texto;
    mensagem.style.color = tipo === "sucesso"
        ? "#126a54"
        : "#a32532";
}

function preencherTabela(hospitais) {
    const tabela = document.getElementById("tabela-hospitais");
    const quantidade = document.getElementById("quantidade-hospitais");

    if (!tabela || !quantidade) {
        return;
    }

    quantidade.textContent = hospitais.length;

    if (hospitais.length === 0) {
        tabela.innerHTML = `
            <tr>
                <td colspan="3">
                    Nenhum hospital cadastrado.
                </td>
            </tr>
        `;

        return;
    }

    tabela.innerHTML = hospitais.map(hospital => `
        <tr>
            <td>${escaparHtml(hospital.nome)}</td>
            <td>${escaparHtml(hospital.cnpj)}</td>
            <td>${escaparHtml(hospital.endereco)}</td>
        </tr>
    `).join("");
}

async function carregarHospitais() {
    try {
        mostrarMensagem("Carregando hospitais...", "sucesso");

        const hospitais = await requisicao(
            "/api/admin/hospitais"
        );

        preencherTabela(hospitais);

        mostrarMensagem(
            `${hospitais.length} hospital(is) encontrado(s).`,
            "sucesso"
        );

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

async function carregarAdministrador() {
    const administrador = await requisicao("/api/me");

    const nomeAdministrador = document.getElementById(
        "nome-administrador"
    );

    if (nomeAdministrador) {
        nomeAdministrador.textContent = administrador.nome;
    }
}

async function sair() {
    try {
        await requisicao("/api/logout", {
            method: "POST"
        });

        window.location.href = "/Login/index.html";

    } catch (erro) {
        alert(erro.message);
    }
}

function inicializarPainel() {
    const botaoSair = document.getElementById("logout");
    if (botaoSair && !botaoSair.dataset.iniciado) {
        botaoSair.dataset.iniciado = "true";
        botaoSair.addEventListener("click", sair);
    }
    const nome = document.getElementById("nome-administrador");
    if (nome && !nome.dataset.iniciado) {
        nome.dataset.iniciado = "true";
        carregarAdministrador().catch(erro => console.error(erro));
    }
    if (painelInicializado) return;

    const tabela = document.getElementById(
        "tabela-hospitais"
    );

    if (!tabela) {
        return;
    }

    painelInicializado = true;

    carregarHospitais();
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarPainel
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarPainel
);
