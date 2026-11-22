import buscarCsrf from "./BuscarTokenSessao.js";

async function fotoBase64(arquivo) {
    if (!arquivo || !arquivo.size) return null;
    if (!['image/jpeg', 'image/png', 'image/webp'].includes(arquivo.type)) {
        throw new Error('Selecione uma imagem JPG, PNG ou WEBP.');
    }
    if (arquivo.size > 5 * 1024 * 1024) throw new Error('A imagem deve ter até 5 MB.');
    return new Promise((resolve, reject) => {
        const leitor = new FileReader();
        leitor.onload = () => resolve(leitor.result);
        leitor.onerror = () => reject(new Error('Não foi possível ler a imagem.'));
        leitor.readAsDataURL(arquivo);
    });
}

async function carregarHospitais() {
    const campo = document.getElementById('hospitalId');
    if (!campo) return;
    try {
        const resposta = await fetch('/api/admin/hospitais');
        if (!resposta.ok) throw new Error('Não foi possível carregar os hospitais.');
        const hospitais = await resposta.json();
        campo.replaceChildren(new Option('Selecione um hospital', ''));
        hospitais.forEach(h => campo.add(new Option(h.nome, h.id)));
    } catch (erro) {
        campo.replaceChildren(new Option(erro.message, ''));
    }
}

export function iniciarCadastro() {
    const form = document.querySelector('#formulario form');
    if (!form) return;
    carregarHospitais();
    form.addEventListener('submit', async event => {
        event.preventDefault();
        const botao = form.querySelector('button[type=submit]');
        const mensagem = form.querySelector('#mensagem-cadastro');
        botao.disabled = true;
        try {
            const dados = Object.fromEntries(new FormData(form));
            dados.foto = await fotoBase64(dados.foto);
            const csrf = await buscarCsrf();
            if (!csrf) throw new Error('Não foi possível obter a sessão.');
            const url = form.id === 'formulario-hospital'
                ? '/api/admin/hospitais' : '/api/admin/hospitais/recepcao';
            const resposta = await fetch(url, {
                method: 'POST', credentials: 'same-origin',
                headers: { 'Content-Type': 'application/json', [csrf.header]: csrf.token },
                body: JSON.stringify(dados)
            });
            const resultado = await resposta.json();
            if (!resposta.ok) throw new Error(resultado.erro || 'Erro ao cadastrar.');
            form.reset();
            mensagem.textContent = 'Cadastro realizado com sucesso!';
        } catch (erro) {
            mensagem.textContent = erro.message;
        } finally {
            botao.disabled = false;
        }
    });
}
