async function inicializarUsuario() {
    const nome = document.getElementById("nome");
    const foto = document.getElementById("foto");
    if (!nome || nome.dataset.usuarioCarregado) return;
    nome.dataset.usuarioCarregado = "true";
    try {
        const resposta = await fetch("/api/me", { credentials: "same-origin" });
        if (!resposta.ok) throw new Error("Não foi possível carregar o usuário.");
        const usuario = await resposta.json();
        nome.textContent = usuario.nome;
        if (foto && usuario.foto) foto.src = usuario.foto;
        else if (foto) foto.hidden = true;
    } catch (erro) {
        delete nome.dataset.usuarioCarregado;
        console.error(erro);
    }
}
document.addEventListener("DOMContentLoaded", inicializarUsuario);
document.addEventListener("htmx:afterSwap", inicializarUsuario);
