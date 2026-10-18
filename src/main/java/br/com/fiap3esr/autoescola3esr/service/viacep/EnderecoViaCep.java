package br.com.fiap3esr.autoescola3esr.service.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EnderecoViaCep(
        String cep,
        @JsonProperty("logradouro") String logradouro,
        String complemento,
        String bairro,
        @JsonProperty("localidade") String cidade,
        @JsonProperty("uf") String uf,
        @JsonProperty("erro") Boolean erro) {
}
