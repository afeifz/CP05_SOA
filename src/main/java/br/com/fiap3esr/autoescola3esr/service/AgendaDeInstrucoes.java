package br.com.fiap3esr.autoescola3esr.service;

import br.com.fiap3esr.autoescola3esr.domain.agenda.*;
import br.com.fiap3esr.autoescola3esr.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3esr.autoescola3esr.domain.aluno.Aluno;
import br.com.fiap3esr.autoescola3esr.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esr.autoescola3esr.domain.aluno.AlunoRepository;
import br.com.fiap3esr.autoescola3esr.domain.instrutor.Especialidade;
import br.com.fiap3esr.autoescola3esr.domain.instrutor.Instrutor;
import br.com.fiap3esr.autoescola3esr.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esr.autoescola3esr.domain.instrutor.InstrutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AgendaDeInstrucoes {
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private InstrutorRepository instrutorRepository;
    @Autowired private InstrucaoRepository repository;
    @Autowired private List<ValidadorAgendamento> validadoresAgendamento;

    @Transactional
    public DadosDetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }

        validadoresAgendamento.forEach(validador -> validador.validar(dados));

        Aluno aluno = alunoRepository.findById(dados.idAluno())
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado!"));
        Instrutor instrutor = escolherInstrutor(dados);

        if (instrutor == null) {
            throw new ValidacaoException("Não existe instrutor disponível para a data/hora informada!");
        }

        // Quando o instrutor foi escolhido automaticamente, ainda precisamos validar o conflito.
        if (repository.existsByInstrutorIdAndDataHoraAndCanceladaFalse(instrutor.getId(), dados.dataHora())) {
            throw new ValidacaoException("Instrutor indisponível na data/hora escolhida!");
        }

        Instrucao instrucao = new Instrucao(null, aluno, instrutor, dados.dataHora(), false, null, null);
        return new DadosDetalhamentoAgendamento(repository.save(instrucao));
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAgendamento detalhar(Long id) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Instrução não encontrada!"));
        return new DadosDetalhamentoAgendamento(instrucao);
    }

    @Transactional
    public DadosDetalhamentoAgendamento cancelar(Long id, DadosCancelamentoInstrucao dados) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Instrução não encontrada!"));

        if (instrucao.isCancelada()) {
            throw new ValidacaoException("A instrução já está cancelada!");
        }

        if (java.time.Duration.between(LocalDateTime.now(), instrucao.getDataHora()).toMinutes() < 24 * 60) {
            throw new ValidacaoException("A instrução só pode ser cancelada com antecedência mínima de 24 horas!");
        }

        instrucao.cancelar(dados.motivo());
        return new DadosDetalhamentoAgendamento(instrucao);
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.findById(dados.idInstrutor())
                    .orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado!"));
        }

        Especialidade especialidade = dados.especialidade();
        if (especialidade == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando o instrutor não for informado!");
        }

        List<Instrutor> candidatos = instrutorRepository.findAllByAtivoTrueAndEspecialidade(especialidade)
                .stream()
                .filter(i -> !repository.existsByInstrutorIdAndDataHoraAndCanceladaFalse(i.getId(), dados.dataHora()))
                .toList();

        if (candidatos.isEmpty()) return null;
        return candidatos.get(ThreadLocalRandom.current().nextInt(candidatos.size()));
    }
}
