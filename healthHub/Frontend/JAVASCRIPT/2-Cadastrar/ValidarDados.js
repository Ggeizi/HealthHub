// validando os campos de formulário

export default function validarDados(dados) {
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
