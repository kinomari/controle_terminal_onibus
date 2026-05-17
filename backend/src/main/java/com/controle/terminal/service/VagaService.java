package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Estacionamento;
import com.controle.terminal.domain.entity.VagaEstacionamento;
import com.controle.terminal.domain.enums.StatusVaga;
import com.controle.terminal.dto.request.VagaRequest;
import com.controle.terminal.dto.request.VagaStatusRequest;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.dto.response.VagaResponse;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.VagaEstacionamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaEstacionamentoRepository vagaRepository;
    private final EstacionamentoService estacionamentoService;

    @Transactional(readOnly = true)
    public PageResponse<VagaResponse> list(Long estacionamentoId, Pageable pageable) {
        Page<VagaEstacionamento> page = (estacionamentoId == null)
                ? vagaRepository.findAll(pageable)
                : vagaRepository.findByEstacionamentoId(estacionamentoId, pageable);
        return PageResponse.of(page, VagaResponse::from);
    }

    @Transactional(readOnly = true)
    public VagaResponse findById(Long id) {
        return VagaResponse.from(loadEntity(id));
    }

    @Transactional
    public VagaResponse create(VagaRequest request) {
        Estacionamento estacionamento = estacionamentoService.loadEntity(request.estacionamentoId());
        if (vagaRepository.existsByEstacionamentoIdAndCodigoIgnoreCase(estacionamento.getId(), request.codigo())) {
            throw new DuplicateResourceException("Vaga", "codigo", request.codigo());
        }
        VagaEstacionamento entity = VagaEstacionamento.builder()
                .estacionamento(estacionamento)
                .codigo(request.codigo())
                .status(request.status() == null ? StatusVaga.LIVRE : request.status())
                .build();
        return VagaResponse.from(vagaRepository.save(entity));
    }

    @Transactional
    public VagaResponse update(Long id, VagaRequest request) {
        VagaEstacionamento entity = loadEntity(id);
        Estacionamento estacionamento = estacionamentoService.loadEntity(request.estacionamentoId());

        boolean codigoMudou = !entity.getCodigo().equalsIgnoreCase(request.codigo());
        boolean estacionamentoMudou = !entity.getEstacionamento().getId().equals(estacionamento.getId());
        if ((codigoMudou || estacionamentoMudou)
                && vagaRepository.existsByEstacionamentoIdAndCodigoIgnoreCase(estacionamento.getId(), request.codigo())) {
            throw new DuplicateResourceException("Vaga", "codigo", request.codigo());
        }

        entity.setEstacionamento(estacionamento);
        entity.setCodigo(request.codigo());
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        return VagaResponse.from(entity);
    }

    @Transactional
    public VagaResponse updateStatus(Long id, VagaStatusRequest request) {
        VagaEstacionamento entity = loadEntity(id);
        entity.setStatus(request.status());
        return VagaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        VagaEstacionamento entity = loadEntity(id);
        vagaRepository.delete(entity);
    }

    public VagaEstacionamento loadEntity(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga", id));
    }
}
