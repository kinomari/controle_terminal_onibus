package com.controle.terminal.service;

import com.controle.terminal.domain.enums.NivelGravidade;
import com.controle.terminal.domain.enums.StatusDoca;
import com.controle.terminal.domain.enums.StatusIncidente;
import com.controle.terminal.domain.enums.StatusOperacao;
import com.controle.terminal.dto.response.DashboardResponse;
import com.controle.terminal.dto.response.DocaResponse;
import com.controle.terminal.dto.response.OperacaoResumoResponse;
import com.controle.terminal.repository.DocaRepository;
import com.controle.terminal.repository.IncidenteRepository;
import com.controle.terminal.repository.OperacaoCargaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DocaRepository docaRepository;
    private final OperacaoCargaRepository operacaoRepository;
    private final IncidenteRepository incidenteRepository;

    @Transactional(readOnly = true)
    public DashboardResponse resumo(Long terminalId) {
        long totalDocas;
        long docasOcupadas;
        if (terminalId != null) {
            totalDocas = docaRepository.findByTerminalIdOrderByCodigo(terminalId).size();
            docasOcupadas = docaRepository.countByTerminalIdAndStatus(terminalId, StatusDoca.OCUPADA);
        } else {
            totalDocas = docaRepository.count();
            docasOcupadas = docaRepository.countByStatus(StatusDoca.OCUPADA);
        }
        double taxaOcupacao = totalDocas == 0 ? 0.0 : (docasOcupadas * 1.0 / totalDocas);

        long agendadas;
        long andamento;
        if (terminalId != null) {
            agendadas = operacaoRepository.countByTerminalIdAndStatus(terminalId, StatusOperacao.AGENDADA);
            andamento = operacaoRepository.countByTerminalIdAndStatus(terminalId, StatusOperacao.EM_ANDAMENTO);
        } else {
            agendadas = operacaoRepository.countByStatus(StatusOperacao.AGENDADA);
            andamento = operacaoRepository.countByStatus(StatusOperacao.EM_ANDAMENTO);
        }

        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        long finalizadasHoje = operacaoRepository.countFinalizadasDesde(inicioDoDia, terminalId);
        Double tempoMedio = operacaoRepository.tempoMedioSegundosDesde(inicioDoDia, terminalId);

        long incidentesAbertos = incidenteRepository.countByStatus(StatusIncidente.ABERTO);
        Map<NivelGravidade, Long> incidentesPorGravidade = new EnumMap<>(NivelGravidade.class);
        for (NivelGravidade n : NivelGravidade.values()) {
            incidentesPorGravidade.put(n, 0L);
        }
        incidenteRepository.contagemAbertosPorGravidade(terminalId)
                .forEach(c -> incidentesPorGravidade.put(c.getNivel(), c.getTotal()));

        List<OperacaoResumoResponse> recentes = operacaoRepository
                .findRecentByStatuses(
                        List.of(StatusOperacao.EM_ANDAMENTO, StatusOperacao.AGENDADA, StatusOperacao.FINALIZADA),
                        PageRequest.of(0, 10))
                .stream()
                .map(OperacaoResumoResponse::from)
                .toList();

        List<DocaResponse> docas = (terminalId != null)
                ? docaRepository.findByTerminalIdOrderByCodigo(terminalId).stream()
                    .map(DocaResponse::from).toList()
                : docaRepository.findAll().stream()
                    .map(DocaResponse::from).toList();

        long veiculosNoPatio = agendadas + andamento;

        return new DashboardResponse(
                totalDocas,
                docasOcupadas,
                taxaOcupacao,
                agendadas,
                andamento,
                finalizadasHoje,
                veiculosNoPatio,
                tempoMedio,
                incidentesAbertos,
                incidentesPorGravidade,
                recentes,
                docas
        );
    }
}
