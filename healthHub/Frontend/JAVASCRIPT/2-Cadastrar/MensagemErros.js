export default function mostrarMensagem(texto, tipo) {
  const mensagem = document.getElementById("mensagem-cadastro");

  if (!mensagem) {
    return;
  }

  mensagem.textContent = texto;
  mensagem.className = `mensagem ${tipo}`;
}
