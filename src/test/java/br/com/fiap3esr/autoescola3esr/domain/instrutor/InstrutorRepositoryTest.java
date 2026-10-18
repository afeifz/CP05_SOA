package br.com.fiap3esr.autoescola3esr.domain.instrutor;

import br.com.fiap3esr.autoescola3esr.domain.agenda.Instrucao;
import br.com.fiap3esr.autoescola3esr.domain.aluno.Aluno;
import br.com.fiap3esr.autoescola3esr.domain.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.stream.events.EndElement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InstrutorRepositoryTest {
    @Autowired
    InstrutorRepository repository;

    @Autowired
    TestEntityManager testeEntity;

    @Test
    @DisplayName("Expectativa: Retornar null quando o instrutor estiver ocupado.")
    void escolherInstrutorAleatorioDisponivelCenario1() {
        LocalDateTime proximaSegundaAs10 = LocalDateTime
                .now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);


        //cadastrar aluno
        Aluno aluno = cadastrarAluno();


        //cadastrar instrutor
        Instrutor instrutor = cadastrarInstrutor();


        //agendar instrução
        agendarInstrucao(aluno,instrutor,proximaSegundaAs10);

        //act or when
        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );
        //assert or then
        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: Retornar null quando o instrutor estiver ocupado.")
    void escolherInstrutorAleatorioDisponivelCenario2() {
        LocalDateTime proximaSegundaAs10 = LocalDateTime
                .now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);


        //cadastrar instrutor
        Instrutor instrutor = cadastrarInstrutor();


        //act or when
        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );
        //assert or then
        assertThat(instrutorDisponivel).isEqualTo(instrutor);
    }
    private Endereco dadosEndereco() {
        return new Endereco(
                "Rua Teste",
                "800",
                "Apto 80",
                "Vila test",
                "test city",
                "TS",
                "04662002"
        );
    }

    private Aluno cadastrarAluno() {
        Aluno aluno = new Aluno(
                null,
                "Aluno Teste",
                "alunoteste@gmail.com.br",
                "(11) 993202045",
                "12345678901",
                dadosEndereco(),
                true
        );
        testeEntity.persist(aluno);
        return aluno;
    }

    private Instrutor cadastrarInstrutor() {
        Instrutor instrutor = new Instrutor(
                null,
                "instrutor teste",
                "instrut@gmail.com",
                "(11) 99330017",
                "4583924492",
                Especialidade.MOTOS,
                dadosEndereco(),
                true
        );
        testeEntity.persist(instrutor);
        return instrutor;
    }

    private void agendarInstrucao(
            Aluno aluno,
            Instrutor instrutor,
            LocalDateTime dataHora
    ){
        Instrucao instrucao = new Instrucao(
                null,
                aluno,
                instrutor,
                dataHora,
                false,
                null,
                null
        );
        testeEntity.persist(instrucao);
    }
}
