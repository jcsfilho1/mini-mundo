package br.com.minimundo.controller;

import br.com.minimundo.entity.Tarefa;
import br.com.minimundo.entity.StatusTarefa;
import br.com.minimundo.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    // 1. LISTAR TAREFAS DE UM PROJETO (Com filtro por status opcional)
    // GET http://localhost:8080/api/tarefas/projeto/{projetoId}?status=CONCLUIDA
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<?> listarPorProjeto(
            @PathVariable Long projetoId,
            @RequestParam(required = false) StatusTarefa status) {
        try {
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(projetoId, status);
            return ResponseEntity.ok(tarefas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. BUSCAR TAREFA POR ID
    // GET http://localhost:8080/api/tarefas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CRIAR UMA NOVA TAREFA
    // POST http://localhost:8080/api/tarefas
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Tarefa tarefa) {
        try {
            Tarefa novaTarefa = tarefaService.criar(tarefa);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. EDITAR UMA TAREFA EXISTENTE
    // PUT http://localhost:8080/api/tarefas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Tarefa tarefa) {
        try {
            Tarefa tarefaEditada = tarefaService.editar(id, tarefa);
            return ResponseEntity.ok(tarefaEditada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. EXCLUIR UMA TAREFA
    // DELETE http://localhost:8080/api/tarefas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            tarefaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Captura tanto erro de ID inexistente quanto violação da regra de predecessora
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}