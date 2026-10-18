package br.com.fiap3esr.autoescola3esr.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoUsuario(
        @NotNull Long id,
        String login,
        Role perfil) {
}
