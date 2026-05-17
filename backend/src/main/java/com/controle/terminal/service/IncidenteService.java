package com.controle.terminal.service;

import com.controle.terminal.domain.entity.*;
import com.controle.terminal.domain.enums.NivelGravidade;
import com.controle.terminal.domain.enums.StatusIncidente;
import com.controle.terminal.dto.request.EncerrarIncidenteRequest;
import com.controle.terminal.dto.request.IncidenteRequest;
import com.controle.terminal.dto.response.IncidenteResponse;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.exception.BusinessException;
import com.controle.terminal.exception.IncidentAlreadyClosedException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.IncidenteRepository;
import com.controle.terminal.security.AuthenticatedUser;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final TipoIncidenteService tipoIncidenteService;
    private final TerminalService terminalService;
    private final DocaService docaService;
    private final EstacionamentoService estacionamentoService;
    private final VagaService vagaService;
    private final OperacaoService operacaoService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public PageResponse<IncidenteResponse> list(StatusIncidente status, Long terminalId,
                                                NivelGravidade gravidade, LocalDateTime de,
                                                LocalDateTime ate, Pageable pageable) {
        Specification<Incidente> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (status != null) p.add(cb.equal(root.get("status"), status));
            if (terminalId != null) p.add(cb.equal(root.get("terminal").get("id"), terminalId));
            if (gravidade != null) p.add(cb.equal(root.get("tipo").get("nivelGravidade"), gravidade));
            if (de != null) p.add(cb.greaterThanOrEqualTo(root.get("ocorridoEm"), de));
            if (ate != null) p.add(cb.lessThanOrEqualTo(root.get("ocorridoEm"), ate));
            return p.isEmpty() ? cb.conjunction() : cb.and(p.toArray(new Predicate[0]));
        };
        Page<Incidente> page = incidenteRepository.findAll(spec, pageable);
        return PageResponse.of(page, IncidenteResponse::from);
    }

    @Transactional(readOnly = true)
    public IncidenteResponse findById(Long id) {
        return IncidenteResponse.from(loadEntity(id));
    }

    @Transactional
    public IncidenteResponse create(IncidenteRequest request) {
        if (request.ocorridoEm().isAfter(LocalDateTime.now())) {
            throw new BusinessException("INVALID_DATE",
                    "A data do incidente nao pode estar no futuro.");
        }
        if (request.docaId() == null && request.vagaId() == null
                && request.operacaoId() == null && request.estacionamentoId() == null) {
            throw new BusinessException("MISSING_LINK",
                    "Informe ao menos um vinculo (doca, estacionamento, vaga ou operacao).");
        }

        TipoIncidente tipo = tipoIncidenteService.loadEntity(request.tipoIncidenteId());
        Terminal terminal = terminalService.loadEntity(request.terminalId());
        Doca doca = request.docaId() != null ? docaService.loadEntity(request.docaId()) : null;
        Estacionamento est = request.estacionamentoId() != null
                ? estacionamentoService.loadEntity(request.estacionamentoId()) : null;
        VagaEstacionamento vaga = request.vagaId() != null ? vagaService.loadEntity(request.vagaId()) : null;
        OperacaoCarga operacao = request.operacaoId() != null
                ? operacaoService.loadEntity(request.operacaoId()) : null;

        Incidente entity = Incidente.builder()
                .tipo(tipo)
                .terminal(terminal)
                .doca(doca)
                .estacionamento(est)
                .vaga(vaga)
                .operacao(operacao)
                .usuarioRegistro(usuarioLogado())
                .ocorridoEm(request.ocorridoEm())
                .descricao(request.descricao())
                .status(StatusIncidente.ABERTO)
                .acaoTomada(request.acaoTomada())
                .build();
        return IncidenteResponse.from(incidenteRepository.save(entity));
    }

    @Transactional
    public IncidenteResponse update(Long id, IncidenteRequest request) {
        Incidente entity = loadEntity(id);
        if (entity.getStatus() == StatusIncidente.RESOLVIDO || entity.getStatus() == StatusIncidente.CANCELADO) {
            throw new IncidentAlreadyClosedException(id, entity.getStatus());
        }
        entity.setTipo(tipoIncidenteService.loadEntity(request.tipoIncidenteId()));
        entity.setTerminal(terminalService.loadEntity(request.terminalId()));
        entity.setDoca(request.docaId() != null ? docaService.loadEntity(request.docaId()) : null);
        entity.setEstacionamento(request.estacionamentoId() != null
                ? estacionamentoService.loadEntity(request.estacionamentoId()) : null);
        entity.setVaga(request.vagaId() != null ? vagaService.loadEntity(request.vagaId()) : null);
        entity.setOperacao(request.operacaoId() != null ? operacaoService.loadEntity(request.operacaoId()) : null);
        entity.setOcorridoEm(request.ocorridoEm());
        entity.setDescricao(request.descricao());
        entity.setAcaoTomada(request.acaoTomada());
        return IncidenteResponse.from(entity);
    }

    @Transactional
    public IncidenteResponse encerrar(Long id, EncerrarIncidenteRequest request) {
        Incidente entity = loadEntity(id);
        if (entity.getStatus() == StatusIncidente.RESOLVIDO || entity.getStatus() == StatusIncidente.CANCELADO) {
            throw new IncidentAlreadyClosedException(id, entity.getStatus());
        }
        entity.setAcaoTomada(request.acaoTomada());
        entity.setStatus(StatusIncidente.RESOLVIDO);
        entity.setEncerradoEm(LocalDateTime.now());
        return IncidenteResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Incidente entity = loadEntity(id);
        incidenteRepository.delete(entity);
    }

    public Incidente loadEntity(Long id) {
        return incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente", id));
    }

    private Usuario usuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthenticatedUser authUser) {
            return usuarioService.loadEntity(authUser.getId());
        }
        return null;
    }
}
