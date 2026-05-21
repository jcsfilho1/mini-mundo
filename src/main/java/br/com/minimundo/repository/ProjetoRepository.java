package com.exemplo.minimundo.repository;

import com.exemplo.minimundo.entity.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    // Método que verifica se já existe algum projeto com o nome informado
    boolean existsByNome(String nome);

    // Método opcional (mas muito útil) para buscar um projeto pelo nome exato
    Optional<Projeto> findByNome(String nome);
}