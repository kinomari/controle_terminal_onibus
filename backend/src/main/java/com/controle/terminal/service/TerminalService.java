package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Terminal;
import com.controle.terminal.dto.request.TerminalRequest;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.dto.response.TerminalResponse;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TerminalService {

    private final TerminalRepository terminalRepository;

    @Transactional(readOnly = true)
    public PageResponse<TerminalResponse> list(Pageable pageable) {
        return PageResponse.of(terminalRepository.findAll(pageable), TerminalResponse::from);
    }

    @Transactional(readOnly = true)
    public TerminalResponse findById(Long id) {
        return TerminalResponse.from(loadEntity(id));
    }

    @Transactional
    public TerminalResponse create(TerminalRequest request) {
        if (terminalRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new DuplicateResourceException("Terminal", "nome", request.nome());
        }
        Terminal entity = Terminal.builder()
                .nome(request.nome())
                .endereco(request.endereco())
                .cidade(request.cidade())
                .ativo(request.ativo() == null ? Boolean.TRUE : request.ativo())
                .build();
        return TerminalResponse.from(terminalRepository.save(entity));
    }

    @Transactional
    public TerminalResponse update(Long id, TerminalRequest request) {
        Terminal entity = loadEntity(id);
        if (!entity.getNome().equalsIgnoreCase(request.nome())
                && terminalRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new DuplicateResourceException("Terminal", "nome", request.nome());
        }
        entity.setNome(request.nome());
        entity.setEndereco(request.endereco());
        entity.setCidade(request.cidade());
        if (request.ativo() != null) {
            entity.setAtivo(request.ativo());
        }
        return TerminalResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Terminal entity = loadEntity(id);
        terminalRepository.delete(entity);
    }

    public Terminal loadEntity(Long id) {
        return terminalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Terminal", id));
    }
}
