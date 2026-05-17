package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.StatusDoca;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doca")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_terminal", nullable = false)
    private Terminal terminal;

    @Column(name = "cd_doca", nullable = false, length = 20)
    private String codigo;

    @Column(name = "ds_localizacao", length = 150)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_doca", nullable = false, length = 30)
    private StatusDoca status = StatusDoca.DISPONIVEL;
}
