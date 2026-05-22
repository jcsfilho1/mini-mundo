package br.com.minimundo.service;

import br.com.minimundo.entity.Tarefa;
import br.com.minimundo.entity.StatusTarefa;
import br.com.minimundo.entity.Projeto;
import br.com.minimundo.repository.TarefaRepository;
import br.com.minimundo.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    // 1. LISTAR TAREFAS DE UM PROJETO (Com filtro opcional por Status)
    @Transactional(readOnly = true)
    public List<Tarefa> listarPorProjeto(Long projetoId, StatusTarefa status) {
        if (!projetoRepository.existsById(projetoId)) {
            throw new IllegalArgumentException("Projeto não encontrado com o ID: " + projetoId);
        }
        
        if (status != null) {
            return tarefaRepository.findByProjetoIdAndStatus(projetoId, status);
        }
        return tarefaRepository.findByProjetoId(projetoId);
    }

    // 2. BUSCAR TAREFA POR ID
    @Transactional(readOnly = true)
    public Optional<Tarefa> buscarPorId(Long id) {
        return tarefaRepository.findById(id);
    }

    // 3. CRIAR UMA TAREFA
    @Transactional
    public Tarefa criar(Tarefa tarefa) {
        validarRegrasTarefa(tarefa);
        return tarefaRepository.save(tarefa);
    }

    // 4. EDITAR UMA TAREFA
    @Transactional
    public Tarefa editar(Long id, Tarefa tarefaAtualizada) {
        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada com o ID: " + id));

        // Atualiza os campos permitidos
        tarefaExistente.setDescricao(tarefaAtualizada.getDescricao());
        tarefaExistente.setDataInicio(tarefaAtualizada.getDataInicio());
        tarefaExistente.setDataFim(tarefaAtualizada.getDataFim());
        tarefaExistente.setStatus(tarefaAtualizada.getStatus());
        tarefaExistente.setTarefaPredecessora(tarefaAtualizada.getTarefaPredecessora());
        tarefaExistente.setProjeto(tarefaAtualizada.getProjeto());

        validarRegrasTarefa(tarefaExistente);
        return tarefaRepository.save(tarefaExistente);
    }

    // 5. EXCLUIR UMA TAREFA
    @Transactional
    public void excluir(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new IllegalArgumentException("Tarefa não encontrada com o ID: " + id);
        }

        if (tarefaRepository.existsByTarefaPredecessoraId(id)) {
            throw new IllegalStateException("Não é possível excluir esta tarefa pois ela é predecessora de outra tarefa ativa.");
        }

        tarefaRepository.deleteById(id);
    }

    // MÉTODO AUXILIAR PARA VALIDAÇÕES DAS REGRAS DE NEGÓCIO AJUSTADO
    private void validarRegrasTarefa(Tarefa tarefa) {
        // Regra 2: Validar se o projeto associado realmente existe
        if (tarefa.getProjeto() == null || tarefa.getProjeto().getId() == null) {
            throw new IllegalArgumentException("A tarefa deve estar vinculada a um projeto válido.");
        }
        
        projetoRepository.findById(tarefa.getProjeto().getId())
                .orElseThrow(() -> new IllegalArgumentException("O projeto associado a esta tarefa não existe."));

        // Regra 3: Garantir que a Data de Fim não seja anterior à Data de Início
        if (tarefa.getDataInicio() != null && tarefa.getDataFim() != null) {
            if (tarefa.getDataFim().isBefore(tarefa.getDataInicio())) {
                throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
            }
        }

        // Validações da Tarefa Predecessora
        if (tarefa.getTarefaPredecessora() != null && tarefa.getTarefaPredecessora().getId() != null) {
            Long predId = tarefa.getTarefaPredecessora().getId();
            
            // Evitar auto-referência
            if (tarefa.getId() != null && predId.equals(tarefa.getId())) {
                throw new IllegalArgumentException("Uma tarefa não pode ser predecessora de si mesma.");
            }

            // Busca a predecessora real no banco para validar integridade
            Tarefa predecessoraBanco = tarefaRepository.findById(predId)
                    .orElseThrow(() -> new IllegalArgumentException("Tarefa predecessora informada não existe."));

            // 🛡️ TRAVA DE SEGURANÇA: Garante que a predecessora pertence ao MESMO projeto
            if (!predecessoraBanco.getProjeto().getId().equals(tarefa.getProjeto().getId())) {
                throw new IllegalArgumentException("A tarefa predecessora deve pertencer ao mesmo projeto.");
            }
            
            // Atualiza a referência com o objeto completo carregado do banco
            tarefa.setTarefaPredecessora(predecessoraBanco);
        }
    }
}