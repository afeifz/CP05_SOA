package br.com.fiap3esr.autoescola3esr.controller;

import br.com.fiap3esr.autoescola3esr.domain.agenda.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.domain.agenda.DadosCancelamentoInstrucao;
import br.com.fiap3esr.autoescola3esr.domain.agenda.DadosDetalhamentoAgendamento;
import br.com.fiap3esr.autoescola3esr.service.AgendaDeInstrucoes;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrucoes")
@SecurityRequirement(name = "bearer-key")
public class InstrucaoController {
    @Autowired
    private AgendaDeInstrucoes agenda;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAgendamento> agendarInstrucao(
            @RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAgendamento> detalharInstrucao(@PathVariable Long id) {
        return ResponseEntity.ok(agenda.detalhar(id));
    }

    @PostMapping("/{id}/cancelamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAgendamento> cancelarInstrucao(
            @PathVariable Long id,
            @RequestBody @Valid DadosCancelamentoInstrucao dados) {
        return ResponseEntity.ok(agenda.cancelar(id, dados));
    }
}
