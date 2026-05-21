package br.com.minimundo.repository;

import br.com.minimundo.entity.Tarefa;
import br.com.minimundo.entity.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // 1. Permite listar todas as tarefas associadas a um projeto específico pelo ID do projeto
    List<Tarefa> findByProjetoId(Long projetoId);

    // 2. Permite listar e filtrar as tarefas de um projeto pelo ID do projeto E pelo seu Status
    List<Tarefa> findByProjetoIdAndStatus(Long projetoId, StatusTarefa status);

    // 3. Verifica se existe alguma tarefa que tem o ID informado como sua predecessora
    // Retorna true se a tarefa for predecessora de outra, impedindo a sua exclusão
    boolean existsByTarefaPredecessoraId(Long tarefaPredecessoraId);
}