package br.com.fiap3esr.autoescola3esr.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosCadastroUsuario(
        @NotBlank
        String login,

        @NotBlank
        @Size(min = 6, message = "A senha deve possuir pelo menos 6 caracteres")
        String senha,

        @NotNull
        Role perfil) {
}
