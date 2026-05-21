package com.exemplo.minimundo.controller;

import com.exemplo.minimundo.entity.Projeto;
import com.exemplo.minimundo.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
// @PreAuthorize("isAuthenticated()") // Descomente caso seu JWT use segurança por anotação global
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    // 1. LISTAR TODOS OS PROJETOS
    // GET http://localhost:8080/api/projetos
    @GetMapping
    public ResponseEntity<List<Projeto>> listarTodos() {
        List<Projeto> projetos = projetoService.listarTodos();
        return ResponseEntity.ok(projetos);
    }

    // 2. BUSCAR PROJETO POR ID
    // GET http://localhost:8080/api/projetos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        return projetoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CRIAR UM NOVO PROJETO
    // POST http://localhost:8080/api/projetos
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Projeto projeto) {
        try {
            Projeto novoProjeto = projetoService.criar(projeto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProjeto);
        } catch (IllegalArgumentException e) {
            // Retorna o erro de nome duplicado de forma amigável para o front
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. EDITAR UM PROJETO EXISTENTE
    // PUT http://localhost:8080/api/projetos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Projeto projeto) {
        try {
            Projeto projetoEditado = projetoService.editar(id, proyecto);
            return ResponseEntity.ok(projetoEditado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. EXCLUIR UM PROJETO
    // DELETE http://localhost:8080/api/projetos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            projetoService.excluir(id);
            return ResponseEntity.noContent().build(); // Retorna o status 24
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}