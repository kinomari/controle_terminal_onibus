package com.controle.terminal.service;

import com.controle.terminal.domain.entity.*;
import com.controle.terminal.domain.enums.StatusDoca;
import com.controle.terminal.domain.enums.StatusOperacao;
import com.controle.terminal.domain.enums.StatusVaga;
import com.controle.terminal.dto.request.CheckinRequest;
import com.controle.terminal.dto.response.DocumentoCargaResponse;
import com.controle.terminal.dto.response.OperacaoResponse;
import com.controle.terminal.dto.response.OperacaoResumoResponse;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.exception.*;
import com.controle.terminal.repository.DocumentoCargaRepository;
import com.controle.terminal.repository.OperacaoCargaRepository;
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
public class OperacaoService {

    private final OperacaoCargaRepository operacaoRepository;
    private final DocumentoCargaRepository documentoRepository;
    private final TerminalService terminalService;
    private final DocaService docaService;
    private final VagaService vagaService;
    private final VeiculoService veiculoService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public PageResponse<OperacaoResumoResponse> list(StatusOperacao status, Long terminalId, Pageable pageable) {
        Specification<OperacaoCarga> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (terminalId != null) {
                predicates.add(cb.equal(root.get("terminal").get("id"), terminalId));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<OperacaoCarga> page = operacaoRepository.findAll(spec, pageable);
        return PageResponse.of(page, OperacaoResumoResponse::from);
    }

    @Transactional(readOnly = true)
    public OperacaoResponse findById(Long id) {
        OperacaoCarga entity = loadEntity(id);
        List<DocumentoCargaResponse> docs = documentoRepository.findByOperacaoIdOrderById(id).stream()
                .map(DocumentoCargaResponse::from)
                .toList();
        return OperacaoResponse.from(entity, docs);
    }

    @Transactional
    public OperacaoResponse checkin(CheckinRequest request) {
        if (request.docaId() != null && request.vagaId() != null) {
            throw new BusinessException("DOCA_OR_VAGA_ONLY",
                    "Informe somente doca ou vaga, nao ambos.");
        }

        Terminal terminal = terminalService.loadEntity(request.terminalId());
        Veiculo veiculo = veiculoService.loadEntity(request.veiculoId());

        Doca doca = null;
        VagaEstacionamento vaga = null;
        if (request.docaId() != null) {
            doca = docaService.loadEntity(request.docaId());
            if (doca.getStatus() != StatusDoca.DISPONIVEL) {
                throw new DocaUnavailableException(doca.getCodigo(), doca.getStatus());
            }
            doca.setStatus(StatusDoca.OCUPADA);
        }
        if (request.vagaId() != null) {
            vaga = vagaService.loadEntity(request.vagaId());
            if (vaga.getStatus() != StatusVaga.LIVRE) {
                throw new VagaUnavailableException(vaga.getCodigo(), vaga.getStatus());
            }
            vaga.setStatus(StatusVaga.OCUPADA);
        }

        OperacaoCarga operacao = OperacaoCarga.builder()
                .terminal(terminal)
                .doca(doca)
                .vaga(vaga)
                .veiculo(veiculo)
                .usuario(usuarioLogado())
                .tipo(request.tipo())
                .status(StatusOperacao.AGENDADA)
                .descricaoCarga(request.descricaoCarga())
                .quantidadeVolume(request.quantidadeVolume())
                .pesoEstimado(request.pesoEstimado())
                .observacao(request.observacao())
                .agendadaEm(LocalDateTime.now())
                .build();

        OperacaoCarga salvo = operacaoRepository.save(operacao);
        return OperacaoResponse.from(salvo, List.of());
    }

    @Transactional
    public OperacaoResponse iniciar(Long id) {
        OperacaoCarga op = loadEntity(id);
        if (op.getStatus() != StatusOperacao.AGENDADA) {
            throw new InvalidStateTransitionException("Operacao", id, op.getStatus(), "iniciar");
        }
        if (op.getDoca() == null && op.getVaga() == null) {
            throw new BusinessException("MISSING_LOCATION",
                    "A operacao %d nao possui doca ou vaga atribuida para iniciar.".formatted(id));
        }
        op.setStatus(StatusOperacao.EM_ANDAMENTO);
        op.setIniciadaEm(LocalDateTime.now());
        return OperacaoResponse.from(op, documentos(id));
    }

    @Transactional
    public OperacaoResponse finalizar(Long id) {
        OperacaoCarga op = loadEntity(id);
        if (op.getStatus() != StatusOperacao.EM_ANDAMENTO) {
            throw new InvalidStateTransitionException("Operacao", id, op.getStatus(), "finalizar");
        }
        op.setStatus(StatusOperacao.FINALIZADA);
        op.setFinalizadaEm(LocalDateTime.now());
        return OperacaoResponse.from(op, documentos(id));
    }

    @Transactional
    public OperacaoResponse checkout(Long id) {
        OperacaoCarga op = loadEntity(id);
        if (op.getStatus() != StatusOperacao.FINALIZADA) {
            throw new OperacaoNotFinishedException(id, op.getStatus());
        }
        liberarRecursos(op);
        return OperacaoResponse.from(op, documentos(id));
    }

    @Transactional
    public OperacaoResponse cancelar(Long id) {
        OperacaoCarga op = loadEntity(id);
        if (op.getStatus() == StatusOperacao.FINALIZADA) {
            throw new InvalidStateTransitionException("Operacao", id, op.getStatus(), "cancelar");
        }
        if (op.getStatus() == StatusOperacao.CANCELADA) {
            return OperacaoResponse.from(op, documentos(id));
        }
        liberarRecursos(op);
        op.setStatus(StatusOperacao.CANCELADA);
        return OperacaoResponse.from(op, documentos(id));
    }

    private void liberarRecursos(OperacaoCarga op) {
        if (op.getDoca() != null && op.getDoca().getStatus() == StatusDoca.OCUPADA) {
            op.getDoca().setStatus(StatusDoca.DISPONIVEL);
        }
        if (op.getVaga() != null && op.getVaga().getStatus() == StatusVaga.OCUPADA) {
            op.getVaga().setStatus(StatusVaga.LIVRE);
        }
    }

    private List<DocumentoCargaResponse> documentos(Long operacaoId) {
        return documentoRepository.findByOperacaoIdOrderById(operacaoId).stream()
                .map(DocumentoCargaResponse::from)
                .toList();
    }

    public OperacaoCarga loadEntity(Long id) {
        return operacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operacao", id));
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
