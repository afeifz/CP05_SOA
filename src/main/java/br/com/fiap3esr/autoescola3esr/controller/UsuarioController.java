package br.com.fiap3esr.autoescola3esr.controller;

import br.com.fiap3esr.autoescola3esr.domain.usuario.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> cadastrarUsuario(
            @RequestBody @Valid DadosCadastroUsuario dados,
            UriComponentsBuilder uriBuilder) {
        if (repository.existsByLogin(dados.login())) {
            return ResponseEntity.unprocessableEntity().build();
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        Usuario usuario = repository.save(new Usuario(dados, senhaCriptografada));
        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemUsuario(usuario));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DadosListagemUsuario>> listarUsuarios(
            @ParameterObject
            @PageableDefault(size = 10, sort = "login") Pageable paginacao) {
        return ResponseEntity.ok(
                repository.findAll(paginacao).map(DadosListagemUsuario::new)
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> detalharUsuario(@PathVariable Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado!"));
        return ResponseEntity.ok(new DadosListagemUsuario(usuario));
    }

    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosListagemUsuario> atualizarUsuario(
            @RequestBody @Valid DadosAtualizacaoUsuario dados) {
        Usuario usuario = repository.findById(dados.id())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado!"));

        if (dados.login() != null && !dados.login().equals(usuario.getLogin()) && repository.existsByLogin(dados.login())) {
            return ResponseEntity.unprocessableEntity().build();
        }

        usuario.atualizarPerfil(dados);
        return ResponseEntity.ok(new DadosListagemUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new UsuarioNotFoundException("Usuário não encontrado!");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/minha-senha")
    @Transactional
    public ResponseEntity<Void> alterarMinhaSenha(
            @RequestBody @Valid DadosAlteracaoSenha dados,
            Authentication authentication) {

        Usuario usuario = repository.findByLogin(authentication.getName())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado!"));

        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getPassword())) {
            return ResponseEntity.status(403).build();
        }

        usuario.alterarSenha(passwordEncoder.encode(dados.novaSenha()));

        return ResponseEntity.noContent().build();
    }
}
