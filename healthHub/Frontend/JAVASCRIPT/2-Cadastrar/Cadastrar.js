import validarDados from "./ValidarDados.js";
import obterDados from "./Formulario.js";
import buscarCsrf from "./BuscarTokenSessao.js";
import mostrarMensagem from "./MensagemErros.js";


const API_HOSPITAIS = "/api/admin/hospitais";


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
            headers[csrf.headeName] = csrf.token;
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
            window.location.href = "/Admin/index.html";
        }, 2500);

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    } finally {
        botao.disabled = false;
        botao.textContent = "Cadastrar hospital";
    }
}

export default cadastrarHospital;



