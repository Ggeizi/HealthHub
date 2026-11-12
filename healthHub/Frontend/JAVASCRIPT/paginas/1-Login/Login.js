const recepcao = document.getElementById("recepcao");
const medico = document.getElementById("medico");
const paciente = document.getElementById("paciente");

function selecionar(event) {

    const selecionado = event.currentTarget;
    const ativo = selecionado.classList.contains("selecionado");

    recepcao.classList.remove("selecionado");
    medico.classList.remove("selecionado");
    paciente.classList.remove("selecionado");

    if (!ativo) {
        selecionado.classList.add("selecionado");
    }
}

let formularioLoginInicializado = false;

function mostrarMensagem(texto, tipo = "erro") {
    const mensagem = document.getElementById("mensagem-login");

    if (!mensagem) {
        return;
    }

    mensagem.textContent = texto;
    mensagem.className = `mensagem ${tipo}`;
}

async function buscarCsrf() {
    const resposta = await fetch("/api/csrf", {
        method: "GET",
        credentials: "same-origin"
    });

    if (!resposta.ok) {
        return null;
    }

    return resposta.json();
}

async function obterUsuarioAtual() {
    const resposta = await fetch("/api/me", {
        method: "GET",
        credentials: "same-origin"
    });

    if (!resposta.ok) {
        throw new Error("Não foi possível identificar o usuário.");
    }

    return resposta.json();
}

function obterPaginaDoPerfil(perfil) {
    const paginas = {
        ADMIN: "/Admin/index.html",
        RECEPCAO: "/Recepcao/index.html",
        MEDICO: "/Medico/index.html",
        PACIENTE: "/Paciente/index.html"
    };

    return paginas[perfil];
}

async function realizarLogin(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botao = formulario.querySelector("button[type='submit']");

    const login = formulario.elements.login.value.trim();
    const senha = formulario.elements.senha.value;

    if (!login || !senha) {
        mostrarMensagem(
            "Informe o login e a senha.",
            "erro"
        );

        return;
    }

    botao.disabled = true;
    botao.textContent = "Entrando...";

    try {
        const csrf = await buscarCsrf();

        const cabecalhos = {
            "Content-Type": "application/x-www-form-urlencoded"
        };

        if (csrf) {
            cabecalhos[csrf.header] = csrf.token;
        }

        const dados = new URLSearchParams();

        dados.append("login", login);

        /*
            O backend recebe o campo como "password".
            O formulário visual usa "senha".
        */
        dados.append("password", senha);

        const resposta = await fetch("/api/login", {
            method: "POST",
            headers: cabecalhos,
            credentials: "same-origin",
            body: dados
        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(
                resultado.erro ||
                resultado.message ||
                "Login ou senha incorretos."
            );
        }

        const usuario = await obterUsuarioAtual();
        const pagina = obterPaginaDoPerfil(usuario.perfil);

        if (!pagina) {
            throw new Error("Perfil de usuário não reconhecido.");
        }

        mostrarMensagem(
            "Login realizado com sucesso.",
            "sucesso"
        );

        window.location.href = pagina;

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");

        botao.disabled = false;
        botao.textContent = "Entrar";
    }
}

function inicializarFormularioLogin() {
    const formulario = document.getElementById(
        "formulario-login"
    );

    if (!formulario || formularioLoginInicializado) {
        return;
    }

    formularioLoginInicializado = true;

    formulario.addEventListener(
        "submit",
        realizarLogin
    );
}

document.addEventListener(
    "DOMContentLoaded",
    inicializarFormularioLogin
);

document.addEventListener(
    "htmx:afterSwap",
    inicializarFormularioLogin
);