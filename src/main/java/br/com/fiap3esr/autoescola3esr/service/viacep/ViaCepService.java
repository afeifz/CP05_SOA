package br.com.fiap3esr.autoescola3esr.service.viacep;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ViaCepService {

    private final RestClient restClient;

    public ViaCepService() {
        this.restClient = RestClient.create("https://viacep.com.br");
    }

    public EnderecoViaCep consultar(String cepInformado) {
        if (cepInformado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP é obrigatório.");
        }
        String cep = cepInformado.replaceAll("\\D", "");
        if (!cep.matches("\\d{8}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP deve conter 8 dígitos.");
        }

        try {
            EnderecoViaCep endereco = restClient.get()
                    .uri("/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(EnderecoViaCep.class);
            if (endereco == null || Boolean.TRUE.equals(endereco.erro())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
            }
            return endereco;
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível consultar o serviço ViaCEP no momento.", exception);
        }
    }
}
