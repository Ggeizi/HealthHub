export default async function buscarCsrf() {
  const resposta = await fetch("/api/csrf", {
    credentials: "same-origin",
  });

  if (!resposta.ok) {
    return null;
  }

  return resposta.json();
}
