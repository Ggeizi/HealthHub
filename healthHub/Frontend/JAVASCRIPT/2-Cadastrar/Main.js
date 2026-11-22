import { iniciarCadastro } from "./TelaCadastro.js";

document.addEventListener("DOMContentLoaded", () => {
    htmx.ajax("GET", "cabecalho.html", { target: "#cabecalho" });
    const hospital = new URLSearchParams(location.search).get("formulario") === "hospital";
    const pagina = hospital ? "formulario-cadastro-hospital.html" : "formulario-recepcao.html";
    htmx.ajax("GET", pagina, { target: "#formulario" });
});

document.addEventListener("htmx:afterSwap", event => {
    if (event.detail.target.id === "formulario") iniciarCadastro();
});
