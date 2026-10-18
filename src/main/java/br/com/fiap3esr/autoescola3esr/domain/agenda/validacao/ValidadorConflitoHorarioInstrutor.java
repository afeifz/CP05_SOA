package br.com.fiap3esr.autoescola3esr.domain.agenda.validacao;

import br.com.fiap3esr.autoescola3esr.domain.agenda.DadosAgendamento;
import br.com.fiap3esr.autoescola3esr.domain.agenda.InstrucaoRepository;
import br.com.fiap3esr.autoescola3esr.domain.agenda.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorConflitoHorarioInstrutor implements ValidadorAgendamento {
    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        if (dados.idInstrutor() == null) return;

        if (repository.existsByInstrutorIdAndDataHoraAndCanceladaFalse(dados.idInstrutor(), dados.dataHora())) {
            throw new ValidacaoException("Instrutor indisponível na data/hora escolhida!");
        }
    }
}
