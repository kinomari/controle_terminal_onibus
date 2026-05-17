package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Estacionamento;
import com.controle.terminal.domain.entity.Terminal;
import com.controle.terminal.domain.enums.StatusEstacionamento;
import com.controle.terminal.dto.request.EstacionamentoRequest;
import com.controle.terminal.dto.response.EstacionamentoResponse;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.EstacionamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final TerminalService terminalService;

    @Transactional(readOnly = true)
    public PageResponse<EstacionamentoResponse> list(Long terminalId, Pageable pageable) {
        Page<Estacionamento> page = (terminalId == null)
                ? estacionamentoRepository.findAll(pageable)
                : estacionamentoRepository.findByTerminalId(terminalId, pageable);
        return PageResponse.of(page, EstacionamentoResponse::from);
    }

    @Transactional(readOnly = true)
    public EstacionamentoResponse findById(Long id) {
        return EstacionamentoResponse.from(loadEntity(id));
    }

    @Transactional
    public EstacionamentoResponse create(EstacionamentoRequest request) {
        Terminal terminal = terminalService.loadEntity(request.terminalId());
        Estacionamento entity = Estacionamento.builder()
                .terminal(terminal)
                .nome(request.nome())
                .capacidade(request.capacidade())
                .status(request.status() == null ? StatusEstacionamento.ATIVO : request.status())
                .build();
        return EstacionamentoResponse.from(estacionamentoRepository.save(entity));
    }

    @Transactional
    public EstacionamentoResponse update(Long id, EstacionamentoRequest request) {
        Estacionamento entity = loadEntity(id);
        Terminal terminal = terminalService.loadEntity(request.terminalId());
        entity.setTerminal(terminal);
        entity.setNome(request.nome());
        entity.setCapacidade(request.capacidade());
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        return EstacionamentoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Estacionamento entity = loadEntity(id);
        estacionamentoRepository.delete(entity);
    }

    public Estacionamento loadEntity(Long id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estacionamento", id));
    }
}
