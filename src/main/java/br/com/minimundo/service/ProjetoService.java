package br.com.minimundo.service;

import br.com.minimundo.entity.Projeto;
import br.com.minimundo.entity.Tarefa;
import br.com.minimundo.repository.ProjetoRepository;
import br.com.minimundo.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    // 1. LISTAR TODOS OS PROJETOS
    @Transactional(readOnly = true)
    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    // 2. BUSCAR PROJETO POR ID
    @Transactional(readOnly = true)
    public Optional<Projeto> buscarPorId(Long id) {
        return projetoRepository.findById(id);
    }

    // 3. CRIAR UM NOVO PROJETO
    @Transactional
    public Projeto criar(Projeto projeto) {
        // Regra de Negócio (02-projects.md): Nome do projeto é obrigatório e único.
        if (projetoRepository.existsByNome(projeto.getNome())) {
            throw new IllegalArgumentException("Já existe um projeto cadastrado com este nome.");
        }
        return projetoRepository.save(projeto);
    }

    // 4. EDITAR UM PROJETO EXISTENTE
    @Transactional
    public Projeto editar(Long id, Projeto projetoAtualizado) {
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com o ID: " + id));

        // Valida duplicidade de nome se o nome foi alterado para um que já existe em outro registro
        if (!projetoExistente.getNome().equals(projetoAtualizado.getNome()) && 
            projetoRepository.existsByNome(projetoAtualizado.getNome())) {
            throw new IllegalArgumentException("Já existe outro projeto cadastrado com este nome.");
        }

        // Atualiza os campos do objeto persistido
        projetoExistente.setNome(projetoAtualizado.getNome());
        projetoExistente.setDescricao(projetoAtualizado.getDescricao());
        projetoExistente.setStatus(projetoAtualizado.getStatus());
        projetoExistente.setOrcamento(projetoAtualizado.getOrcamento());

        return projetoRepository.save(projetoExistente);
    }

    // 5. EXCLUIR UM PROJETO
    @Transactional
    public void excluir(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com o ID: " + id));

        // Regra de Negócio (02-projects.md): A exclusão só é permitida se não houver registros dependentes associados.
        List<Tarefa> tarefasDoProjeto = tarefaRepository.findByProjetoId(id);
        if (!tarefasDoProjeto.isEmpty()) {
            throw new IllegalStateException("Não é possível excluir o projeto porque ele possui tarefas associadas.");
        }
        
        projetoRepository.delete(projeto);
    }
}