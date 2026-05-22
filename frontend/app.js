const API = "http://localhost:8080";

// VARIÁVEIS GLOBAIS DE CONTROLE DA DASHBOARD
let projetoSelecionadoId = null;
let projetoAtivoObjeto = null; 
let listaDeProjetosGlobal = []; // Guarda a cópia dos projetos vindos da API
let listaDeTarefasGlobal = [];  // Guarda a cópia das tarefas do projeto ativo
let tarefaSelecionadaId = null; // Controla qual tarefa está sendo editada

// === LÓGICA DE AUTENTICAÇÃO ===
async function register() {
    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    const response = await fetch(`${API}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, email, senha })
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

    if(!emailValido || !senhaValida) return;

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    try {
        const response = await fetch(`${API}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, senha })
        });

        if(response.ok) {
            const data = await response.json();
            const tokenFinal = data.token || data;

            localStorage.setItem("token", tokenFinal);
            window.location.href = "dashboard.html";
        } else {
            alert("E-mail ou senha inválidos");
        }
    } catch (error) {
        console.error("Erro ao conectar com a API:", error);
        alert("Não foi possível conectar ao servidor.");
    }
}

async function carregarDashboard() {
    const token = localStorage.getItem("token");

    if(!token) {
        window.location.href = "login.html";
        return;
    }

    try {
        document.getElementById("mensagem").innerText = "Bem-vindo de volta!";
        
        // Limpa barras de pesquisa ao carregar/recarregar a tela
        if(document.getElementById("busca-projeto-nome")) document.getElementById("busca-projeto-nome").value = "";
        if(document.getElementById("busca-projeto-status")) document.getElementById("busca-projeto-status").value = "TODOS";
        
        listarProjetos(); 
    } catch (error) {
        logout();
    }
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}

// === LÓGICA DE PROJETOS (CRIAR, VISUALIZAR, EDITAR, EXCLUIR) ===
async function listarProjetos() {
    const token = localStorage.getItem("token");
    const container = document.getElementById("lista-projetos");
    
    const response = await fetch(`${API}/api/projetos`, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    
    if (response.ok) {
        listaDeProjetosGlobal = await response.json();
        container.innerHTML = listaDeProjetosGlobal.length === 0 ? '<p class="aviso-vazio">Nenhum projeto encontrado.</p>' : '';
        
        listaDeProjetosGlobal.forEach(proj => {
            if (projetoSelecionadoId === proj.id) {
                projetoAtivoObjeto = proj;
            }

            container.innerHTML += `
                <div class="card card-projeto ${projetoSelecionadoId === proj.id ? 'ativo' : ''}" 
                     onclick="selecionarProjeto(${proj.id})">
                    <div style="display:flex; justify-content:space-between; align-items:center; pointer-events:none;">
                        <h4 style="margin:0;">${proj.nome}</h4>
                        <span class="badge ${proj.status}">${proj.status}</span>
                    </div>
                    <p style="pointer-events:none;">${proj.descricao || 'Sem descrição'}</p>
                    <span style="pointer-events:none;"><strong>Orçamento:</strong> R$ ${proj.orcamento || 0}</span>
                </div>
            `;
        });
    }
}

async function salvarProjeto() {
    const token = localStorage.getItem("token");
    const nome = document.getElementById("proj-nome").value.trim();
    const descricao = document.getElementById("proj-descricao").value;
    const orcamento = document.getElementById("proj-orcamento").value;
    const status = "ATIVO"; 

    if(!nome) {
        alert("O nome do projeto é obrigatório.");
        return;
    }

    const response = await fetch(`${API}/api/projetos`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ nome, descricao, orcamento, status })
    });

    if (response.ok) {
        alert("Projeto criado com sucesso!");
        document.getElementById("proj-nome").value = "";
        document.getElementById("proj-descricao").value = "";
        document.getElementById("proj-orcamento").value = "";
        fecharModalProjeto();
        listarProjetos();
    } else {
        const msg = await response.text();
        alert("Erro ao criar projeto: " + msg);
    }
}

function selecionarProjeto(id) {
    projetoSelecionadoId = id;
    projetoAtivoObjeto = listaDeProjetosGlobal.find(p => p.id === id);
    
    if (projetoAtivoObjeto) {
        document.getElementById("nome-projeto-selecionado").innerText = projetoAtivoObjeto.nome;
    }
    
    document.getElementById("btn-nova-tarefa").removeAttribute("disabled");
    document.getElementById("btn-editar-projeto").removeAttribute("disabled");
    document.getElementById("btn-excluir-projeto").removeAttribute("disabled");
    
    const cards = document.querySelectorAll('.card-projeto');
    cards.forEach(card => card.classList.remove('ativo'));
    
    listarProjetos(); 
    listarTarefas();   
}

async function salvarEdicaoProjeto() {
    const token = localStorage.getItem("token");
    const nome = document.getElementById("edit-proj-nome").value.trim();
    const descricao = document.getElementById("edit-proj-descricao").value;
    const orcamento = document.getElementById("edit-proj-orcamento").value;
    const status = document.getElementById("edit-proj-status").value;

    if(!nome) {
        alert("O nome do projeto é obrigatório.");
        return;
    }

    const response = await fetch(`${API}/api/projetos/${projetoSelecionadoId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ nome, descricao, orcamento, status })
    });

    if (response.ok) {
        alert("Projeto updated com sucesso!");
        fecharModalEditarProjeto();
        document.getElementById("nome-projeto-selecionado").innerText = nome;
        listarProjetos();
    } else {
        const msg = await response.text();
        alert("Erro ao atualizar projeto: " + msg);
    }
}

async function executarExclusaoProjeto() {
    if (!projetoSelecionadoId || !projetoAtivoObjeto) {
        alert("Por favor, selecione um projeto na lista da esquerda primeiro.");
        return;
    }

    const nomeDoProjeto = projetoAtivoObjeto.nome;
    const mensagemConfirmacao = `⚠️ ATENÇÃO: Você está prestes a excluir o projeto "${nomeDoProjeto}".\n\n` +
                                `Esta ação só será permitida se o projeto NÃO tiver nenhuma tarefa vinculada.\n\n` +
                                `Deseja prosseguir com a exclusão definitiva?`;

    const usuarioConfirmou = confirm(mensagemConfirmacao);
    if (!usuarioConfirmou) return;

    const token = localStorage.getItem("token");
    const deleteUrl = `${API}/api/projetos/${projetoSelecionadoId}`;

    try {
        const response = await fetch(deleteUrl, {
            method: "DELETE",
            headers: { 
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            alert(`Projeto "${nomeDoProjeto}" excluído com sucesso!`);
            listaDeProjetosGlobal = listaDeProjetosGlobal.filter(p => p.id !== projetoSelecionadoId);
            
            projetoSelecionadoId = null;
            projetoAtivoObjeto = null;
            
            document.getElementById("nome-projeto-selecionado").innerText = "Selecione um projeto";
            document.getElementById("lista-tarefas").innerHTML = '<p class="aviso-vazio">Clique em um projeto para carregar suas respectivas tarefas.</p>';
            if(document.getElementById("progresso-container")) document.getElementById("progresso-container").style.display = "none";
            
            document.getElementById("btn-nova-tarefa").setAttribute("disabled", "true");
            document.getElementById("btn-editar-projeto").setAttribute("disabled", "true");
            document.getElementById("btn-excluir-projeto").setAttribute("disabled", "true");
            
            reconstruirListaProjetosHTML();
        } else {
            const msgErro = await response.text();
            alert(`Não foi possível excluir.\n\nMotivo do Servidor: ${msgErro || "Este projeto possui tarefas dependentes associadas."}`);
        }
    } catch (error) {
        alert("Erro ao conectar com o servidor Java.");
    }
}

function filtrarProjetos() {
    const termoNome = document.getElementById("busca-projeto-nome").value.toLowerCase();
    const filtroStatus = document.getElementById("busca-projeto-status").value;
    const container = document.getElementById("lista-projetos");
    
    const projetosFiltrados = listaDeProjetosGlobal.filter(proj => {
        const bateNome = proj.nome.toLowerCase().includes(termoNome);
        const bateStatus = (filtroStatus === "TODOS") || (proj.status === filtroStatus);
        return bateNome && bateStatus;
    });

    container.innerHTML = projetosFiltrados.length === 0 ? '<p class="aviso-vazio">Nenhum projeto corresponde à busca.</p>' : '';
    
    projetosFiltrados.forEach(proj => {
        container.innerHTML += `
            <div class="card card-projeto ${projetoSelecionadoId === proj.id ? 'ativo' : ''}" 
                 onclick="selecionarProjeto(${proj.id})">
                <div style="display:flex; justify-content:space-between; align-items:center; pointer-events:none;">
                    <h4 style="margin:0;">${proj.nome}</h4>
                    <span class="badge ${proj.status}">${proj.status}</span>
                </div>
                <p style="pointer-events:none;">${proj.descricao || 'Sem descrição'}</p>
                <span style="pointer-events:none;"><strong>Orçamento:</strong> R$ ${proj.orcamento || 0}</span>
            </div>
        `;
    });
}

function reconstruirListaProjetosHTML() {
    const container = document.getElementById("lista-projetos");
    container.innerHTML = listaDeProjetosGlobal.length === 0 ? '<p class="aviso-vazio">Nenhum projeto encontrado.</p>' : '';
    
    listaDeProjetosGlobal.forEach(proj => {
        container.innerHTML += `
            <div class="card card-projeto ${projetoSelecionadoId === proj.id ? 'ativo' : ''}" 
                 onclick="selecionarProjeto(${proj.id})">
                <div style="display:flex; justify-content:space-between; align-items:center; pointer-events:none;">
                    <h4 style="margin:0;">${proj.nome}</h4>
                    <span class="badge ${proj.status}">${proj.status}</span>
                </div>
                <p style="pointer-events:none;">${proj.descricao || 'Sem descrição'}</p>
                <span style="pointer-events:none;"><strong>Orçamento:</strong> R$ ${proj.orcamento || 0}</span>
            </div>
        `;
    });
}

// === LÓGICA DE TAREFAS (CRUD COMPLETO AJUSTADO CONFORME REGRAS) ===
async function listarTarefas() {
    if (!projetoSelecionadoId) return;
    
    const token = localStorage.getItem("token");
    const filtroStatus = document.getElementById("filtro-status").value;
    
    let url = `${API}/api/tarefas/projeto/${projetoSelecionadoId}`;
    if (filtroStatus !== "TODAS") {
        url += `?status=${filtroStatus}`;
    }

    const response = await fetch(url, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (response.ok) {
        listaDeTarefasGlobal = await response.json();
        
        // Regra de Negócio 7: Exibe a barra de progresso do projeto baseado nas tarefas
        calcularProgressoProjeto();
        
        // Limpa o termo de busca textual por descrição
        document.getElementById("busca-tarefa-descricao").value = "";
        
        renderizarTarefasHTML(listaDeTarefasGlobal);
        atualizarSelectPredecessoras();
    }
}

function renderizarTarefasHTML(tarefas) {
    const container = document.getElementById("lista-tarefas");
    container.innerHTML = tarefas.length === 0 ? '<p class="aviso-vazio">Nenhuma tarefa encontrada.</p>' : '';
    
    tarefas.forEach(tarefa => {
        const predTexto = tarefa.predecessora ? `<span style="color: #7f8c8d; font-size:12px;"> | ↩️ Predecessora: <strong>${tarefa.predecessora.descricao}</strong></span>` : '';
        const dataInicioTexto = tarefa.dataInicio ? tarefa.dataInicio : "Não informada";
        const dataFimTexto = tarefa.dataFim ? tarefa.dataFim : "Não informada";

        container.innerHTML += `
            <div class="card card-tarefa" style="cursor:default; padding: 15px; margin-bottom: 10px; border-left: 5px solid ${tarefa.status === 'CONCLUIDA' ? '#2ecc71' : '#e74c3c'};">
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                    <div>
                        <p style="margin: 0 0 5px 0;"><strong>Descrição:</strong> ${tarefa.descricao}</p>
                        <p style="margin: 0 0 5px 0; font-size: 13px; color: #555;">📅 ${dataInicioTexto} até ${dataFimTexto} ${predTexto}</p>
                        <span class="badge ${tarefa.status}">${tarefa.status === 'CONCLUIDA' ? 'Concluída' : 'Não Concluída'}</span>
                    </div>
                    <div style="display: flex; gap: 6px;">
                        <button onclick="abrirModalEditarTarefa(${tarefa.id})" class="btn-warn" style="padding: 4px 8px; font-size: 12px; cursor:pointer;">Editar</button>
                        <button onclick="executarExclusaoTarefa(${tarefa.id}, '${tarefa.descricao}')" class="btn-danger" style="padding: 4px 8px; font-size: 12px; cursor:pointer;">Excluir</button>
                    </div>
                </div>
            </div>
        `;
    });
}

// Filtro textual dinâmico em memória para Descrição de Tarefa
function filtrarTarefasEmMemoria() {
    const termo = document.getElementById("busca-tarefa-descricao").value.toLowerCase();
    const tarefasFiltradas = listaDeTarefasGlobal.filter(t => t.descricao.toLowerCase().includes(termo));
    renderizarTarefasHTML(tarefasFiltradas);
}

// Regra de Negócio 7: Calcular Progresso do Projeto
function calcularProgressoProjeto() {
    const containerProgresso = document.getElementById("progresso-container");
    if (!containerProgresso) return;

    if (listaDeTarefasGlobal.length === 0) {
        containerProgresso.style.display = "none";
        return;
    }
    
    containerProgresso.style.display = "block";
    const concluidas = listaDeTarefasGlobal.filter(t => t.status === "CONCLUIDA").length;
    const porcentagem = Math.round((concluidas / listaDeTarefasGlobal.length) * 100);
    
    document.getElementById("barra-progresso").style.width = `${porcentagem}%`;
    document.getElementById("texto-progresso").innerText = `Progresso do Projeto: ${porcentagem}%`;
}

// Preenche os selects dos modais com as tarefas do projeto para servir de Predecessora
function atualizarSelectPredecessoras() {
    const selects = [document.getElementById("tar-predecessora"), document.getElementById("edit-tar-predecessora")];
    
    selects.forEach(select => {
        if (!select) return;
        select.innerHTML = '<option value="">Nenhuma</option>';
        listaDeTarefasGlobal.forEach(t => {
            // Se for o de edição, impede que a tarefa selecione ela própria como dependência
            if (select.id === "edit-tar-predecessora" && t.id === tarefaSelecionadaId) return;
            select.innerHTML += `<option value="${t.id}">${t.descricao}</option>`;
        });
    });
}

async function salvarTarefa() {
    const token = localStorage.getItem("token");
    const descricao = document.getElementById("tar-descricao").value.trim();
    const dataInicio = document.getElementById("tar-data-inicio").value;
    const dataFim = document.getElementById("tar-data-fim").value;
    const predId = document.getElementById("tar-predecessora").value;
    const status = "NAO_CONCLUIDA";

    if (!descricao) {
        alert("A descrição da tarefa é obrigatória.");
        return;
    }

    // Regra de Negócio 3: Validação de consistência cronológica
    if (dataInicio && dataFim && new Date(dataFim) < new Date(dataInicio)) {
        alert("Erro: A Data de Término não pode ser anterior à Data de Início!");
        return;
    }

    const payload = {
        descricao,
        dataInicio: dataInicio || null,
        dataFim: dataFim || null,
        status,
        projeto: { id: projetoSelecionadoId }
    };

    if (predId) payload.predecessora = { id: parseInt(predId) };

    const response = await fetch(`${API}/api/tarefas`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        alert("Tarefa criada com sucesso!");
        fecharModalTarefa();
        listarTarefas();
    } else {
        const msg = await response.text();
        alert("Erro ao criar tarefa: " + msg);
    }
}

function abrirModalEditarTarefa(id) {
    tarefaSelecionadaId = id;
    const tarefa = listaDeTarefasGlobal.find(t => t.id === id);
    if (!tarefa) return;

    atualizarSelectPredecessoras();

    document.getElementById("edit-tar-descricao").value = tarefa.descricao;
    document.getElementById("edit-tar-data-inicio").value = tarefa.dataInicio || "";
    document.getElementById("edit-tar-data-fim").value = tarefa.dataFim || "";
    document.getElementById("edit-tar-status").value = tarefa.status;
    document.getElementById("edit-tar-predecessora").value = tarefa.predecessora ? tarefa.predecessora.id : "";

    document.getElementById("modal-editar-tarefa").style.display = "block";
}

async function salvarEdicaoTarefa() {
    const token = localStorage.getItem("token");
    const descricao = document.getElementById("edit-tar-descricao").value.trim();
    const dataInicio = document.getElementById("edit-tar-data-inicio").value;
    const dataFim = document.getElementById("edit-tar-data-fim").value;
    const status = document.getElementById("edit-tar-status").value;
    const predId = document.getElementById("edit-tar-predecessora").value;

    if (!descricao) {
        alert("A descrição da tarefa é obrigatória.");
        return;
    }

    // Regra de Negócio 3: Validação de consistência cronológica
    if (dataInicio && dataFim && new Date(dataFim) < new Date(dataInicio)) {
        alert("Erro: A Data de Término não pode ser anterior à Data de Início!");
        return;
    }

    const payload = {
        descricao,
        dataInicio: dataInicio || null,
        dataFim: dataFim || null,
        status,
        projeto: { id: projetoSelecionadoId }
    };

    if (predId) payload.predecessora = { id: parseInt(predId) };

    const response = await fetch(`${API}/api/tarefas/${tarefaSelecionadaId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        alert("Tarefa atualizada com sucesso!");
        fecharModalEditarTarefa();
        listarTarefas();
    } else {
        const msg = await response.text();
        alert("Erro ao atualizar tarefa: " + msg);
    }
}

async function executarExclusaoTarefa(id, descricao) {
    const confirmar = confirm(`Tem certeza que deseja excluir a tarefa "${descricao}"?\n\nEsta ação só funcionará se nenhuma outra tarefa depender dela.`);
    if (!confirmar) return;

    const token = localStorage.getItem("token");

    try {
        const response = await fetch(`${API}/api/tarefas/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
            alert("Tarefa excluída com sucesso!");
            listarTarefas();
        } else {
            const msg = await response.text();
            // Regra de Negócio 4: Alerta caso seja predecessora de outra tarefa
            alert(`Não foi possível excluir a tarefa.\n\nMotivo: ${msg || "Ela é predecessora de outra tarefa ativa."}`);
        }
    } catch (error) {
        alert("Erro de comunicação com o servidor.");
    }
}

function filtrarTarefas() { listarTarefas(); }

// === VALIDAÇÕES DE TELA ===
function validarEmail() {
    const email = document.getElementById("email").value;
    const erro = document.getElementById("emailErro");
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if(!regex.test(email)) {
        if(erro) {
            erro.innerText = "E-mail inválido";
            erro.style.color = "red";
        }
        return false;
    }
    if(erro) erro.innerText = "";
    return true;
}

function validarSenha() {
    const senha = document.getElementById("senha").value;
    const erro = document.getElementById("senhaErro");

    if(senha.trim() === "") {
        if(erro) {
            erro.innerText = "Senha obrigatória";
            erro.style.color = "red";
        }
        return false;
    }
    if(erro) erro.innerText = "";
    return true;
}

// === CONTROLE DOS MODAIS ===
function abrirModalProjeto() { 
    document.getElementById("modal-projeto").style.display = "block"; 
}
function fecharModalProjeto() { 
    document.getElementById("modal-projeto").style.display = "none"; 
}

function abrirModalEditarProjeto() { 
    if (!projetoAtivoObjeto) {
        alert("Selecione um projeto na lista primeiro.");
        return;
    }
    document.getElementById("edit-proj-nome").value = projetoAtivoObjeto.nome;
    document.getElementById("edit-proj-descricao").value = projetoAtivoObjeto.descricao || "";
    document.getElementById("edit-proj-orcamento").value = projetoAtivoObjeto.orcamento || 0;
    document.getElementById("edit-proj-status").value = projetoAtivoObjeto.status;

    document.getElementById("modal-editar-projeto").style.display = "block"; 
}

function fecharModalEditarProjeto() { 
    document.getElementById("modal-editar-projeto").style.display = "none"; 
}

function abrirModalTarefa() { 
    document.getElementById("tar-descricao").value = "";
    document.getElementById("tar-data-inicio").value = "";
    document.getElementById("tar-data-fim").value = "";
    atualizarSelectPredecessoras();
    document.getElementById("modal-tarefa").style.display = "block"; 
}
function fecharModalTarefa() { 
    document.getElementById("modal-tarefa").style.display = "none"; 
}

function fecharModalEditarTarefa() {
    document.getElementById("modal-editar-tarefa").style.display = "none";
}

// EXECUÇÃO AUTOMÁTICA SE FOR DASHBOARD
if(window.location.pathname.includes("dashboard.html")) {
    carregarDashboard();
}