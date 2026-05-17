package com.controle.terminal.service;

import com.controle.terminal.domain.entity.TipoIncidente;
import com.controle.terminal.dto.request.TipoIncidenteRequest;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.dto.response.TipoIncidenteResponse;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.TipoIncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoIncidenteService {

    private final TipoIncidenteRepository tipoIncidenteRepository;

    @Transactional(readOnly = true)
    public PageResponse<TipoIncidenteResponse> list(Pageable pageable) {
        return PageResponse.of(tipoIncidenteRepository.findAll(pageable), TipoIncidenteResponse::from);
    }

    @Transactional(readOnly = true)
    public TipoIncidenteResponse findById(Long id) {
        return TipoIncidenteResponse.from(loadEntity(id));
    }

    @Transactional
    public TipoIncidenteResponse create(TipoIncidenteRequest request) {
        if (tipoIncidenteRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new DuplicateResourceException("Tipo de incidente", "nome", request.nome());
        }
        TipoIncidente entity = TipoIncidente.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .nivelGravidade(request.nivelGravidade())
                .build();
        return TipoIncidenteResponse.from(tipoIncidenteRepository.save(entity));
    }

    @Transactional
    public TipoIncidenteResponse update(Long id, TipoIncidenteRequest request) {
        TipoIncidente entity = loadEntity(id);
        if (!entity.getNome().equalsIgnoreCase(request.nome())
                && tipoIncidenteRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new DuplicateResourceException("Tipo de incidente", "nome", request.nome());
        }
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setNivelGravidade(request.nivelGravidade());
        return TipoIncidenteResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        TipoIncidente entity = loadEntity(id);
        tipoIncidenteRepository.delete(entity);
    }

    public TipoIncidente loadEntity(Long id) {
        return tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de incidente", id));
    }
}
