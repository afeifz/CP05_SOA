
package br.com.fiap3esr.autoescola3esr.service.viacep;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ViaCepServiceTest {

    private final ViaCepService service = new ViaCepService();

    @Test
    void deveRejeitarCepComFormatoInvalidoAntesDeChamarServicoExterno() {
        assertThatThrownBy(() -> service.consultar("1234"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("CEP deve conter 8 dígitos");
    }
}
