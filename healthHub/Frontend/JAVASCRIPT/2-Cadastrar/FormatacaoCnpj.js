document.addEventListener("DOMContentLoaded",aplicarMascaraCnpj);

// formatar o campo cnpj
function aplicarMascaraCnpj() {
    const campoCnpj = document.getElementById("cnpj");

    if (!campoCnpj || campoCnpj.dataset.mascara === "true") {
        return;
    }

    campoCnpj.dataset.mascara = "true";

    campoCnpj.addEventListener("input", function () {
        let valor = campoCnpj.value.replace(/\D/g, "");

        valor = valor.substring(0, 14);
        valor = valor.replace(/^(\d{2})(\d)/, "$1.$2");
        valor = valor.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
        valor = valor.replace(/\.(\d{3})(\d)/, ".$1/$2");
        valor = valor.replace(/(\d{4})(\d)/, "$1-$2");

        campoCnpj.value = valor;
    });
}


