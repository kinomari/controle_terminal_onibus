package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Usuario;
import com.controle.terminal.dto.request.UsuarioRequest;
import com.controle.terminal.dto.response.PageResponse;
import com.controle.terminal.dto.response.UsuarioResponse;
import com.controle.terminal.exception.BusinessException;
import com.controle.terminal.exception.DuplicateResourceException;
import com.controle.terminal.exception.ResourceNotFoundException;
import com.controle.terminal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> list(Pageable pageable) {
        return PageResponse.of(usuarioRepository.findAll(pageable), UsuarioResponse::from);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return UsuarioResponse.from(loadEntity(id));
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (request.senha() == null || request.senha().isBlank()) {
            throw new BusinessException("MISSING_PASSWORD", "A senha e obrigatoria ao criar um novo usuario.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Usuario", "email", request.email());
        }
        Usuario entity = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .perfil(request.perfil())
                .ativo(request.ativo() == null ? Boolean.TRUE : request.ativo())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .build();
        return UsuarioResponse.from(usuarioRepository.save(entity));
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario entity = loadEntity(id);
        if (!entity.getEmail().equalsIgnoreCase(request.email())
                && usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Usuario", "email", request.email());
        }
        entity.setNome(request.nome());
        entity.setEmail(request.email());
        entity.setPerfil(request.perfil());
        if (request.ativo() != null) {
            entity.setAtivo(request.ativo());
        }
        if (request.senha() != null && !request.senha().isBlank()) {
            entity.setSenhaHash(passwordEncoder.encode(request.senha()));
        }
        return UsuarioResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Usuario entity = loadEntity(id);
        usuarioRepository.delete(entity);
    }

    public Usuario loadEntity(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
}
