package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.StatusVaga;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vaga_estacionamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VagaEstacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vaga")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estacionamento", nullable = false)
    private Estacionamento estacionamento;

    @Column(name = "cd_vaga", nullable = false, length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_vaga", nullable = false, length = 30)
    private StatusVaga status = StatusVaga.LIVRE;
}
