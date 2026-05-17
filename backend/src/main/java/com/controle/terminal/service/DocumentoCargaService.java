package com.controle.terminal.service;

import com.controle.terminal.domain.entity.DocumentoCarga;
import com.controle.terminal.domain.entity.OperacaoCarga;
import com.controle.terminal.dto.request.DocumentoCargaRequest;
import com.controle.terminal.dto.response.DocumentoCargaResponse;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.DocumentoCargaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoCargaService {

    private final DocumentoCargaRepository documentoRepository;
    private final OperacaoService operacaoService;

    @Transactional(readOnly = true)
    public List<DocumentoCargaResponse> listByOperacao(Long operacaoId) {
        operacaoService.loadEntity(operacaoId);
        return documentoRepository.findByOperacaoIdOrderById(operacaoId).stream()
                .map(DocumentoCargaResponse::from)
                .toList();
    }

    @Transactional
    public DocumentoCargaResponse create(Long operacaoId, DocumentoCargaRequest request) {
        OperacaoCarga operacao = operacaoService.loadEntity(operacaoId);
        DocumentoCarga entity = DocumentoCarga.builder()
                .operacao(operacao)
                .tipo(request.tipo())
                .numero(request.numero())
                .emitidoEm(request.emitidoEm())
                .observacao(request.observacao())
                .build();
        return DocumentoCargaResponse.from(documentoRepository.save(entity));
    }

    @Transactional
    public DocumentoCargaResponse update(Long documentoId, DocumentoCargaRequest request) {
        DocumentoCarga entity = loadEntity(documentoId);
        entity.setTipo(request.tipo());
        entity.setNumero(request.numero());
        entity.setEmitidoEm(request.emitidoEm());
        entity.setObservacao(request.observacao());
        return DocumentoCargaResponse.from(entity);
    }

    @Transactional
    public void delete(Long documentoId) {
        DocumentoCarga entity = loadEntity(documentoId);
        documentoRepository.delete(entity);
    }

    private DocumentoCarga loadEntity(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", id));
    }
}
