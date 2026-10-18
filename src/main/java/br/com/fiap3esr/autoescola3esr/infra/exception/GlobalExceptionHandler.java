package br.com.fiap3esr.autoescola3esr.infra.exception;

import br.com.fiap3esr.autoescola3esr.domain.agenda.ValidacaoException;
import br.com.fiap3esr.autoescola3esr.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esr.autoescola3esr.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esr.autoescola3esr.domain.usuario.UsuarioNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({EntityNotFoundException.class, AlunoNotFoundException.class,
            InstrutorNotFoundException.class, UsuarioNotFoundException.class})
    public ResponseEntity<Erro> tratarNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Erro("Recurso não encontrado", e.getMessage()));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Erro> tratarRegraNegocio(ValidacaoException e) {
        return ResponseEntity.badRequest()
                .body(new Erro("Regra de negócio", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>> tratarBadRequest(MethodArgumentNotValidException e) {
        List<FieldError> erros = e.getFieldErrors();
        return ResponseEntity.badRequest()
                .body(erros.stream().map(DadosBadRequest::new).toList());
    }

    public record Erro(String tipo, String mensagem) {}

    public record DadosBadRequest(String campo, String mensagem) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
