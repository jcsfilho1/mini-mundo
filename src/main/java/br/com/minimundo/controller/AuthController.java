package br.com.minimundo.controller;

import br.com.minimundo.dto.LoginRequestDTO;
import br.com.minimundo.dto.LoginResponseDTO;
import br.com.minimundo.dto.RegisterRequestDTO;
import br.com.minimundo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterRequestDTO dto
    ) {

        service.register(dto);

        return ResponseEntity.ok("Cadastro realizado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto
    ) {

        return ResponseEntity.ok(service.login(dto));
    }
}