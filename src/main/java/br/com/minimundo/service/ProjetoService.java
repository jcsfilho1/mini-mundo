package br.com.minimundo.service;

import br.com.minimundo.entity.Projeto;
import br.com.minimundo.entity.Tarefa; // <-- Importação da Tarefa adicionada
import br.com.minimundo.repository.ProjetoRepository;
import br.com.minimundo.repository.TarefaRepository; // <-- Importação do Repositório adicionada
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
    private TarefaRepository tarefaRepository; // <-- Injeção do repositório de tarefas adicionada

    // ... (outros métodos permanecem iguais)

    // 5. EXCLUIR UM PROJETO (Atualizado com a Regra de Negócio)
    @Transactional
    public void excluir(Long id) {
        // Verifica se o projeto existe antes de tentar deletar
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com o ID: " + id));

        // Regra de Negócio (02-projects.md): A exclusão só é permitida se não houver registros dependentes associados.
        // Buscamos se existem tarefas vinculadas a este ID de projeto
        List<Tarefa> tarefasDoProjeto = tarefaRepository.findByProjetoId(id);
        
        if (!tarefasDoProjeto.isEmpty()) {
            throw new IllegalStateException("Não é possível excluir o projeto porque ele possui tarefas associadas.");
        }
        
        projetoRepository.delete(projeto);
    }
}