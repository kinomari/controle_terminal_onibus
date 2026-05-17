package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Doca;
import com.controle.terminal.domain.entity.Terminal;
import com.controle.terminal.domain.enums.StatusDoca;
import com.controle.terminal.dto.request.DocaRequest;
import com.controle.terminal.dto.request.DocaStatusRequest;
import com.controle.terminal.dto.response.DocaResponse;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.DocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocaService {

    private final DocaRepository docaRepository;
    private final TerminalService terminalService;

    @Transactional(readOnly = true)
    public PageResponse<DocaResponse> list(Long terminalId, Pageable pageable) {
        Page<Doca> page = (terminalId == null)
                ? docaRepository.findAll(pageable)
                : docaRepository.findByTerminalId(terminalId, pageable);
        return PageResponse.of(page, DocaResponse::from);
    }

    @Transactional(readOnly = true)
    public List<DocaResponse> listByTerminal(Long terminalId) {
        return docaRepository.findByTerminalIdOrderByCodigo(terminalId).stream()
                .map(DocaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocaResponse findById(Long id) {
        return DocaResponse.from(loadEntity(id));
    }

    @Transactional
    public DocaResponse create(DocaRequest request) {
        Terminal terminal = terminalService.loadEntity(request.terminalId());
        if (docaRepository.existsByTerminalIdAndCodigoIgnoreCase(terminal.getId(), request.codigo())) {
            throw new DuplicateResourceException("Doca", "codigo", request.codigo());
        }
        Doca entity = Doca.builder()
                .terminal(terminal)
                .codigo(request.codigo())
                .localizacao(request.localizacao())
                .status(request.status() == null ? StatusDoca.DISPONIVEL : request.status())
                .build();
        return DocaResponse.from(docaRepository.save(entity));
    }

    @Transactional
    public DocaResponse update(Long id, DocaRequest request) {
        Doca entity = loadEntity(id);
        Terminal terminal = terminalService.loadEntity(request.terminalId());

        boolean codigoMudou = !entity.getCodigo().equalsIgnoreCase(request.codigo());
        boolean terminalMudou = !entity.getTerminal().getId().equals(terminal.getId());
        if ((codigoMudou || terminalMudou)
                && docaRepository.existsByTerminalIdAndCodigoIgnoreCase(terminal.getId(), request.codigo())) {
            throw new DuplicateResourceException("Doca", "codigo", request.codigo());
        }

        entity.setTerminal(terminal);
        entity.setCodigo(request.codigo());
        entity.setLocalizacao(request.localizacao());
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        return DocaResponse.from(entity);
    }

    @Transactional
    public DocaResponse updateStatus(Long id, DocaStatusRequest request) {
        Doca entity = loadEntity(id);
        entity.setStatus(request.status());
        return DocaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Doca entity = loadEntity(id);
        docaRepository.delete(entity);
    }

    public Doca loadEntity(Long id) {
        return docaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doca", id));
    }
}
