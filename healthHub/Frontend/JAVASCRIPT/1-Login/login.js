
const API = "";

function mostrarMensagem(texto, tipo = "erro") {
  const mensagem = document.getElementById("mensagem-login");

  if (!mensagem) return;

  mensagem.textContent = texto;
  mensagem.className = `mensagem ${tipo}`;
}

async function lerJson(resposta) {
  const texto = await resposta.text();

  if (!texto.trim()) return {};

  try {
    return JSON.parse(texto);
  } catch {
    throw new Error("O servidor retornou uma resposta inválida.");
  }
}

async function buscarCsrf() {
  const resposta = await fetch(`${API}/api/csrf`, {
    method: "GET",
    credentials: "include",
  });

  if (!resposta.ok) {
    if (resposta.status === 404) return null;
    throw new Error("Não foi possível obter o token de segurança.");
  }

  const csrf = await lerJson(resposta);

  return csrf.token && csrf.header ? csrf : null;
}

async function obterUsuarioAtual() {
  const resposta = await fetch(`${API}/api/me`, {
    method: "GET",
    credentials: "include",
  });

  if (!resposta.ok) {
    throw new Error("Não foi possível identificar o usuário.");
  }

  const usuario = await lerJson(resposta);

  if (!usuario.perfil) {
    throw new Error("O backend não retornou o perfil do usuário.");
  }

  return usuario;
}

function obterPaginaDoPerfil(perfil) {
  const paginas = {
    ADMIN: "/Admin/index.html",
    RECEPCAO: "/Recepcao/index.html",
    MEDICO: "/Medico/index.html",
    PACIENTE: "/Paciente/index.html",
  };

  return paginas[perfil];
}

async function realizarLogin(event) {
  event.preventDefault();

  const formulario = event.currentTarget;
  const botao = formulario.querySelector("button[type='submit']");

  const login = formulario.elements.login.value.trim().toLowerCase();
  const senha = formulario.elements.senha.value;

  if (!login || !senha) {
    mostrarMensagem("Informe o login e a senha.");
    return;
  }

  botao.disabled = true;
  botao.textContent = "Entrando...";

  try {
    const csrf = await buscarCsrf();

    const cabecalhos = {
      "Content-Type": "application/x-www-form-urlencoded",
    };

    if (csrf) {
      cabecalhos[csrf.header] = csrf.token;
    }

    const dados = new URLSearchParams();

    dados.append("login", login);
    dados.append("password", senha);

    const resposta = await fetch(`${API}/api/login`, {
      method: "POST",
      headers: cabecalhos,
      credentials: "include",
      body: dados,
    });

    if (!resposta.ok) {
      const resultado = await lerJson(resposta);

      if (resposta.status === 401) {
        throw new Error("Login ou senha incorretos.");
      }

      if (resposta.status === 403) {
        throw new Error("Acesso negado. Verifique o CSRF.");
      }

      throw new Error(
        resultado.erro ||
        resultado.message ||
        `Erro no servidor (${resposta.status}).`
      );
    }

    const usuario = await obterUsuarioAtual();
    const pagina = obterPaginaDoPerfil(usuario.perfil);

    if (!pagina) {
      throw new Error("Perfil de usuário não reconhecido.");
    }

    mostrarMensagem("Login realizado com sucesso.", "sucesso");

    window.location.href = pagina;

  } catch (erro) {
    mostrarMensagem(
      erro instanceof TypeError
        ? "Falha de conexão com o backend. Verifique o servidor e o CORS."
        : erro.message,
      "erro"
    );

  } finally {
    botao.disabled = false;
    botao.textContent = "Entrar";
  }
}

function inicializarFormularioLogin() {
  const formulario = document.getElementById("formulario-login");

  if (!formulario || formulario.dataset.loginInicializado) {
    return;
  }

  formulario.dataset.loginInicializado = "true";
  formulario.addEventListener("submit", realizarLogin);
}

document.addEventListener("DOMContentLoaded", inicializarFormularioLogin);
document.addEventListener("htmx:afterSwap", inicializarFormularioLogin);

if (document.readyState !== "loading") {
  inicializarFormularioLogin();
}
