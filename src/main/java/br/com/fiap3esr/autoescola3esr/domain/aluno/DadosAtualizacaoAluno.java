package br.com.fiap3esr.autoescola3esr.domain.aluno;

import br.com.fiap3esr.autoescola3esr.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoAluno(
        @NotNull
        Long id,
        String nome,
        String telefone,
        @Valid
        DadosEndereco endereco) {
}
