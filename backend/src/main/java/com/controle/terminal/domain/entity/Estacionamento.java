package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.StatusEstacionamento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estacionamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estacionamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_terminal", nullable = false)
    private Terminal terminal;

    @Column(name = "nm_estacionamento", nullable = false, length = 100)
    private String nome;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_estacionamento", nullable = false, length = 30)
    private StatusEstacionamento status = StatusEstacionamento.ATIVO;
}
