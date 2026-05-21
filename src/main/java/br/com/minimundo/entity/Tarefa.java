package br.com.minimundo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição da tarefa é obrigatória.")
    @Column(nullable = false)
    private String descricao;

    // Relacionamento Obrigatório: Toda tarefa pertence a um projeto
    @NotNull(message = "O projeto associado é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    // Auto-relacionamento Opcional: Uma tarefa pode ter uma predecessora
    @ManyToOne
    @JoinColumn(name = "tarefa_predecessora_id")
    private Tarefa tarefaPredecessora;

    @NotNull(message = "O status da tarefa é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status = StatusTarefa.NAO_CONCLUIDA; // Padrão inicial

    // Construtor padrão
    public Tarefa() {
    }

    // Construtor completo
    public Tarefa(Long id, String description, Projeto projeto, LocalDate dataInicio, LocalDate dataFim, Tarefa tarefaPredecessora, StatusTarefa status) {
        this.id = id;
        this.descricao = description;
        this.projeto = projeto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tarefaPredecessora = tarefaPredecessora;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Tarefa getTarefaPredecessora() {
        return tarefaPredecessora;
    }

    public void setTarefaPredecessora(Tarefa tarefaPredecessora) {
        this.tarefaPredecessora = tarefaPredecessora;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarefa tarefa = (Tarefa) o;
        return Objects.equals(id, tarefa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}