package br.com.minimundo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank
    @Size(min = 3)
    private String nome;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String senha;
}