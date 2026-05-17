package com.controle.terminal.service;

import com.controle.terminal.domain.entity.Usuario;
import com.controle.terminal.dto.request.LoginRequest;
import com.controle.terminal.dto.response.TokenResponse;
import com.controle.terminal.exception.InvalidCredentialsException;
import com.controle.terminal.repository.UsuarioRepository;
import com.controle.terminal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new InvalidCredentialsException();
        }

        usuario.setUltimoLogin(LocalDateTime.now());

        String token = jwtService.generate(
                usuario.getEmail(),
                Map.of(
                        "uid", usuario.getId(),
                        "perfil", usuario.getPerfil().name(),
                        "nome", usuario.getNome()
                )
        );

        return new TokenResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                new TokenResponse.UsuarioLogado(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPerfil()
                )
        );
    }
}
