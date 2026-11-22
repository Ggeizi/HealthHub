// Obter dados do formulário
export default function obterDados(formulario) {
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