package br.com.fiap3esr.autoescola3esr.controller;

import br.com.fiap3esr.autoescola3esr.domain.aluno.*;
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
@RequestMapping("/alunos")
@SecurityRequirement(name = "bearer-key")
public class AlunoController {
    @Autowired
    private AlunoRepository repository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> cadastrarAluno(
            @RequestBody @Valid DadosCadastroAluno dados,
            UriComponentsBuilder uriBuilder) {

        if (repository.existsByEmail(dados.email())) {
            return ResponseEntity.unprocessableEntity().build();
        }
        if (repository.existsByCpf(dados.cpf())) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Aluno aluno = repository.save(new Aluno(dados));
        URI uri = uriBuilder.path("/alunos/{id}").buildAndExpand(aluno.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoAluno(aluno));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemAluno>> listarAlunos(
            @ParameterObject
            @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {

        Page<DadosListagemAluno> page = repository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemAluno::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DadosDetalhamentoAluno> detalharAluno(@PathVariable Long id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado!"));
        return ResponseEntity.ok(new DadosDetalhamentoAluno(aluno));
    }

    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> atualizarAluno(
            @RequestBody @Valid DadosAtualizacaoAluno dados) {
        Aluno aluno = repository.findById(dados.id())
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado!"));
        aluno.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoAluno(aluno));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado!"));
        aluno.excluir();
        return ResponseEntity.noContent().build();
    }
}
