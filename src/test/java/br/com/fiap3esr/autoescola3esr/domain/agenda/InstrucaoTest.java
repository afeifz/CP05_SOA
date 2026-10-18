package br.com.fiap3esr.autoescola3esr.domain.agenda;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class InstrucaoTest {
    @Test
    void deveRegistrarCancelamentoEMotivo() {
        Instrucao instrucao = new Instrucao(null, null, null, LocalDateTime.now().plusDays(3), false, null, null);
        instrucao.cancelar(MotivoCancelamento.ALUNO_DESISTIU);
        assertThat(instrucao.isCancelada()).isTrue();
        assertThat(instrucao.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
        assertThat(instrucao.getDataCancelamento()).isNotNull();
    }
}
