function selecionarMenu(event) {

    const links = document.querySelectorAll(".sidebar a");

    links.forEach(link => {
        link.classList.remove("ativo");
    });

    event.currentTarget.classList.add("ativo");
}