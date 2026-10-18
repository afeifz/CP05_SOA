package br.com.fiap3esr.autoescola3esr.domain.instrutor;

import br.com.fiap3esr.autoescola3esr.domain.endereco.DadosEndereco;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstrutorTest {
    @Test
    void deveCadastrarInstrutorComEspecialidadeEAtivo() {
        var endereco = new DadosEndereco("Rua A", "10", "", "Centro", "São Paulo", "SP", "01000-000");
        var dados = new DadosCadastroInstrutor("Carlos Souza", "carlos@email.com", "11999999999", "123456789",
                Especialidade.CARROS, endereco);
        Instrutor instrutor = new Instrutor(dados);
        assertThat(instrutor.getEspecialidade()).isEqualTo(Especialidade.CARROS);
        assertThat(instrutor.isAtivo()).isTrue();
    }

    @Test
    void deveExcluirInstrutorLogicamente() {
        var endereco = new DadosEndereco("Rua A", "10", "", "Centro", "São Paulo", "SP", "01000-000");
        Instrutor instrutor = new Instrutor(new DadosCadastroInstrutor("Carlos", "carlos@email.com", "11999999999",
                "123456789", Especialidade.MOTOS, endereco));
        instrutor.excluir();
        assertThat(instrutor.isAtivo()).isFalse();
    }
}
