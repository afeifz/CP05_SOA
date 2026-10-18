package br.com.fiap3esr.autoescola3esr.controller;

import br.com.fiap3esr.autoescola3esr.service.viacep.EnderecoViaCep;
import br.com.fiap3esr.autoescola3esr.service.viacep.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
public class ViaCepController {
    private final ViaCepService viaCepService;

    public ViaCepController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consultar endereço por CEP", description = "Consulta o serviço externo ViaCEP e retorna os dados do endereço.")
    public ResponseEntity<EnderecoViaCep> consultarCep(@PathVariable String cep) {
        return ResponseEntity.ok(viaCepService.consultar(cep));
    }
}
