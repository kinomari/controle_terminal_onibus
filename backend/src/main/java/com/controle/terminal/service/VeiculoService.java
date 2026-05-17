package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Veiculo;
import com.controle.terminal.dto.request.VeiculoRequest;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.dto.response.VeiculoResponse;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public PageResponse<VeiculoResponse> list(String placa, Pageable pageable) {
        Page<Veiculo> page = (placa == null || placa.isBlank())
                ? veiculoRepository.findAll(pageable)
                : veiculoRepository.findByPlacaContainingIgnoreCase(placa.trim(), pageable);
        return PageResponse.of(page, VeiculoResponse::from);
    }

    @Transactional(readOnly = true)
    public VeiculoResponse findById(Long id) {
        return VeiculoResponse.from(loadEntity(id));
    }

    @Transactional
    public VeiculoResponse create(VeiculoRequest request) {
        String placaNorm = request.placa().toUpperCase();
        if (veiculoRepository.existsByPlacaIgnoreCase(placaNorm)) {
            throw new DuplicateResourceException("Veiculo", "placa", placaNorm);
        }
        Veiculo entity = Veiculo.builder()
                .placa(placaNorm)
                .tipo(request.tipo())
                .empresaResponsavel(request.empresaResponsavel())
                .tipoEmpresa(request.tipoEmpresa())
                .modelo(request.modelo())
                .build();
        return VeiculoResponse.from(veiculoRepository.save(entity));
    }

    @Transactional
    public VeiculoResponse update(Long id, VeiculoRequest request) {
        Veiculo entity = loadEntity(id);
        String placaNorm = request.placa().toUpperCase();
        if (!entity.getPlaca().equalsIgnoreCase(placaNorm)
                && veiculoRepository.existsByPlacaIgnoreCase(placaNorm)) {
            throw new DuplicateResourceException("Veiculo", "placa", placaNorm);
        }
        entity.setPlaca(placaNorm);
        entity.setTipo(request.tipo());
        entity.setEmpresaResponsavel(request.empresaResponsavel());
        entity.setTipoEmpresa(request.tipoEmpresa());
        entity.setModelo(request.modelo());
        return VeiculoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Veiculo entity = loadEntity(id);
        veiculoRepository.delete(entity);
    }

    public Veiculo loadEntity(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo", id));
    }

    public Veiculo findOrCreateByPlaca(VeiculoRequest request) {
        return veiculoRepository.findByPlacaIgnoreCase(request.placa()).orElseGet(() -> {
            Veiculo novo = Veiculo.builder()
                    .placa(request.placa().toUpperCase())
                    .tipo(request.tipo())
                    .empresaResponsavel(request.empresaResponsavel())
                    .tipoEmpresa(request.tipoEmpresa())
                    .modelo(request.modelo())
                    .build();
            return veiculoRepository.save(novo);
        });
    }
}
