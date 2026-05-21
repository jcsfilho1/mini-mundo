package br.com.minimundo.service;

import br.com.minimundo.dto.LoginRequestDTO;
import br.com.minimundo.dto.LoginResponseDTO;
import br.com.minimundo.dto.RegisterRequestDTO;
import br.com.minimundo.entity.User;
import br.com.minimundo.repository.UserRepository;
import br.com.minimundo.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JWTService jwtService;

    public void register(RegisterRequestDTO dto) {

        if(repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        User user = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(encoder.encode(dto.getSenha()))
                .build();

        repository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha incorretos"));

        boolean senhaValida = encoder.matches(dto.getSenha(), user.getSenha());

        if(!senhaValida) {
            throw new RuntimeException("E-mail ou senha incorretos");
        }

        String token = jwtService.gerarToken(user.getEmail());

        return new LoginResponseDTO(token);
    }
}