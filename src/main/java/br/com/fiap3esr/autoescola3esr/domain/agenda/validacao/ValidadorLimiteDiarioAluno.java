package br.com.fiap3esr.autoescola3esr.domain.agenda.validacao;

import br.com.fiap3esr.autoescola3esr.domain.agenda.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.domain.agenda.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.domain.agenda.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDate dia = dados.dataHora().toLocalDate();
        long quantidade = repository.countByAlunoIdAndDataHoraBetweenAndCanceladaFalse(
                dados.idAluno(),
                dia.atStartOfDay(),
                dia.atTime(23, 59, 59)
        );

        if (quantidade >= 2) {
            throw new ValidacaoException("O aluno já possui duas instruções agendadas para este dia!");
        }
    }
}
