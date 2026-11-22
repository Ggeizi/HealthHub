async function carregarConsultas() {
    const lista = document.getElementById("lista-consultas");
    if (!lista || lista.dataset.iniciado) return;
    lista.dataset.iniciado = "true";
    try {
        const consultas = await requisicao("/api/paciente/agendamentos");
        if (!consultas.length) {
            lista.textContent = "Nenhuma consulta cadastrada.";
            return;
        }
        lista.innerHTML = consultas.map(consulta => `
            <li>${escaparHtml(new Date(consulta.inicio).toLocaleString("pt-BR"))}
                — ${escaparHtml(consulta.medico)} — ${escaparHtml(consulta.status)}</li>
        `).join("");
    } catch (erro) {
        lista.textContent = erro.message;
        delete lista.dataset.iniciado;
    }
}

document.addEventListener("DOMContentLoaded", carregarConsultas);
document.addEventListener("htmx:afterSwap", carregarConsultas);
