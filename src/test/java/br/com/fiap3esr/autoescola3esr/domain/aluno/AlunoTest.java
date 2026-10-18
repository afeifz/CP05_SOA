package br.com.fiap3esr.autoescola3esr.domain.aluno;

import br.com.fiap3esr.autoescola3esr.domain.endereco.DadosEndereco;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlunoTest {
    private DadosCadastroAluno cadastro() {
        return new DadosCadastroAluno("Ana Silva", "ana@email.com", "11999999999", "12345678901",
                new DadosEndereco("Rua A", "10", "", "Centro", "São Paulo", "SP", "01000-000"));
    }

    @Test
    void deveCadastrarAlunoComoAtivo() {
        Aluno aluno = new Aluno(cadastro());
        assertThat(aluno.getNome()).isEqualTo("Ana Silva");
        assertThat(aluno.isAtivo()).isTrue();
    }

    @Test
    void deveExcluirAlunoLogicamente() {
        Aluno aluno = new Aluno(cadastro());
        aluno.excluir();
        assertThat(aluno.isAtivo()).isFalse();
    }
}
