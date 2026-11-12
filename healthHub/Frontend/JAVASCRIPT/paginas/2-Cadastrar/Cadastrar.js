const API_HOSPITAIS = "/api/admin/hospitais";

let formularioInicializado = false;

function mostrarMensagem(texto, tipo) {
    const mensagem = document.getElementById("mensagem-cadastro");

    if (!mensagem) {
        return;
    }

    mensagem.textContent = texto;
    mensagem.className = `mensagem ${tipo}`;
}

function aplicarMascaraCnpj() {
    const campoCnpj = document.getElementById("cnpj");

    if (!campoCnpj || campoCnpj.dataset.mascara === "true") {
        return;
    }

    campoCnpj.dataset.mascara = "true";

    campoCnpj.addEventListener("input", function () {
        let valor = campoCnpj.value.replace(/\D/g, "");

        valor = valor.substring(0, 14);
        valor = valor.replace(/^(\d{2})(\d)/, "$1.$2");
        valor = valor.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
        valor = valor.replace(/\.(\d{3})(\d)/, ".$1/$2");
        valor = valor.replace(/(\d{4})(\d)/, "$1-$2");

        campoCnpj.value = valor;
    });
}

function obterDados(formulario) {
    const dados = new FormData(formulario);

    return {
        nome: dados.get("nomeHospital").trim(),
        cnpj: dados.get("cnpj").replace(/\D/g, ""),
        endereco: dados.get("endereco").trim(),
        recepcionista: dados.get("nomeRecepcionista").trim(),
        login: dados.get("loginRecepcao").trim().toLowerCase(),
        senha: dados.get("senhaRecepcao")
    };
}

function validarDados(dados) {
    if (dados.nome.length < 3) {
        return "Informe o nome do hospital.";
    }

    if (dados.cnpj.length !== 14) {
        return "Informe um CNPJ válido.";
    }

    if (dados.endereco.length < 5) {
        return "Informe o endereço do hospital.";
    }

    if (dados.recepcionista.length < 3) {
        return "Informe o nome do recepcionista.";
    }

    if (!dados.login.includes("@")) {
        return "Informe um e-mail válido.";
    }

    if (dados.senha.length < 8) {
        return "A senha deve ter pelo menos 8 caracteres.";
    }

    return null;
}

async function buscarCsrf() {
    const resposta = await fetch("/api/csrf", {
        credentials: "same-origin"
    });

    if (!resposta.ok) {
        return null;
    }

    return resposta.json();
}

async function cadastrarHospital(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botao = formulario.querySelector("button[type='submit']");
    const dados = obterDados(formulario);
    const erro = validarDados(dados);

    if (erro) {
        mostrarMensagem(erro, "erro");
        return;
    }

    botao.disabled = true;
    botao.textContent = "Cadastrando...";

    try {
        const csrf = await buscarCsrf();

        const headers = {
            "Content-Type": "application/json"
        };

        if (csrf) {
            headers[csrf.header] = csrf.token;
        }

        const resposta = await fetch(API_HOSPITAIS, {
            method: "POST",
            headers: headers,
            credentials: "same-origin",
            body: JSON.stringify(dados)
        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(
                resultado.erro ||
                resultado.message ||
                "Erro ao cadastrar o hospital."
            );
        }

        mostrarMensagem(
            "Hospital cadastrado com sucesso!",
            "sucesso"
        );

        formulario.reset();

        setTimeout(function () {
            window.location.href = "../../Login/index.html";
        }, 2500);

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    } finally {
        botao.disabled = false;
        botao.textContent = "Cadastrar hospital";
    }
}

function inicializarFormulario() {
    const formulario = document.getElementById(
        "formulario-cadastro-hospital"
    );

    if (!formulario || formularioInicializado) {
        return;
    }

    formularioInicializado = true;

    formulario.addEventListener(
        "submit",
        cadastrarHospital
    );

    aplicarMascaraCnpj();
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarFormulario
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarFormulario
);