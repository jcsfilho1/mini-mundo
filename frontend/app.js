const API = "http://localhost:8080";


async function register() {

    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    const response = await fetch(`${API}/auth/register`, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            nome,
            email,
            senha
        })
    });

    if(response.ok) {

        alert("Cadastro realizado, Faça login para continuar!");

        window.location.href = "login.html";

    } else {

        const erro = await response.text();

        alert(erro);
    }
}


async function login() {

    const emailValido = validarEmail();
    const senhaValida = validarSenha();

    if(!emailValido || !senhaValida) {
        return;
    }

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    const response = await fetch(`${API}/auth/login`, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            email,
            senha
        })
    });

    if(response.ok) {

        const data = await response.json();

        localStorage.setItem("token", data.token);

        window.location.href = "dashboard.html";

    } else {

        alert("E-mail ou senha inválidos");
    }
}


async function carregarDashboard() {

    const token = localStorage.getItem("token");

    if(!token) {

        window.location.href = "login.html";
        return;
    }

    const response = await fetch(`${API}/teste`, {

        method: "GET",

        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if(response.ok) {

        const texto = await response.text();

        document.getElementById("mensagem")
            .innerText = texto;

    } else {

        alert("Sessão expirada");

        logout();
    }
}


function logout() {

    localStorage.removeItem("token");

    window.location.href = "login.html";
}


function validarEmail() {

    const email =
        document.getElementById("email").value;

    const erro =
        document.getElementById("emailErro");

    const regex =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if(!regex.test(email)) {

        erro.innerText = "E-mail inválido";
        erro.style.color = "red";

        return false;
    }

    erro.innerText = "";

    return true;
}


function validarSenha() {

    const senha =
        document.getElementById("senha").value;

    const erro =
        document.getElementById("senhaErro");

    if(senha.trim() === "") {

        erro.innerText =
            "Senha obrigatória";

        erro.style.color = "red";

        return false;
    }

    erro.innerText = "";

    return true;
}


if(window.location.pathname.includes("dashboard.html")) {

    carregarDashboard();
}