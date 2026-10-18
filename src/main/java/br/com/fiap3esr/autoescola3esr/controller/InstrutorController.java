package br.com.fiap3esr.autoescola3esr.controller;

import br.com.fiap3esr.autoescola3esr.domain.instrutor.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instrutores")
@SecurityRequirement(name = "bearer-key")
public class InstrutorController {
    @Autowired
    private InstrutorRepository repository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoInstrutor> cadastrarInstrutor(
            @RequestBody @Valid DadosCadastroInstrutor dados,
            UriComponentsBuilder uriBuilder) {

        if (repository.existsByEmail(dados.email()) || repository.existsByCnh(dados.cnh())) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Instrutor instrutor = repository.save(new Instrutor(dados));
        URI uri = uriBuilder.path("/instrutores/{id}").buildAndExpand(instrutor.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoInstrutor(instrutor));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemInstrutor>> listarInstrutores(
            @ParameterObject
            @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        Page<DadosListagemInstrutor> page = repository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemInstrutor::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoInstrutor> detalharInstrutor(@PathVariable Long id) {
        Instrutor instrutor = repository.findById(id)
                .orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado!"));
        return ResponseEntity.ok(new DadosDetalhamentoInstrutor(instrutor));
    }

    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoInstrutor> atualizarInstrutor(
            @RequestBody @Valid DadosAtualizacaoInstrutor dados) {
        Instrutor instrutor = repository.findById(dados.id())
                .orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado!"));
        instrutor.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoInstrutor(instrutor));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirInstrutor(@PathVariable Long id) {
        Instrutor instrutor = repository.findById(id)
                .orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado!"));
        instrutor.excluir();
        return ResponseEntity.noContent().build();
    }
}
