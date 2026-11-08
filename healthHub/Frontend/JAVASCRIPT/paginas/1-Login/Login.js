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