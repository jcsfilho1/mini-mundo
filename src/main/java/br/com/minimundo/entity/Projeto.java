package br.com.minimundo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Garante que o campo não seja nulo/vazio e define a coluna como única no banco de dados
    @NotBlank(message = "O nome do projeto é obrigatório.")
    @Column(nullable = false, unique = true)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Salva o Enum no banco como String (ex: "ATIVO") em vez do índice numérico (0, 1)
    @NotNull(message = "O status do projeto é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status = StatusProjeto.ATIVO; // Padrão inicial

    // Usando BigDecimal para valores monetários (orçamento disponível)
    private BigDecimal orcamento;

    // Construtor padrão obrigatório pelo Hibernate
    public Projeto() {
    }

    // Construtor completo para facilitar testes e instanciação
    public Projeto(Long id, String nome, String descricao, StatusProjeto status, BigDecimal orcamento) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.orcamento = orcamento;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    // Equals e HashCode baseados no ID para boa prática com coleções JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return Objects.equals(id, projeto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}