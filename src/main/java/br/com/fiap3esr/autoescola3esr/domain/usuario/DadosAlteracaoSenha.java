package br.com.fiap3esr.autoescola3esr.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAlteracaoSenha(
        @NotBlank
        String senhaAtual,

        @NotBlank
        @Size(min = 6, message = "A nova senha deve possuir pelo menos 6 caracteres")
        String novaSenha) {
}
