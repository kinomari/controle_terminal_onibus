package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nm_usuario", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_email", unique = true, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 40)
    private PerfilUsuario perfil;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "dt_ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true;
        }
    }
}
