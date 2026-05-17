package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.StatusIncidente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_incidente", nullable = false)
    private TipoIncidente tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_terminal", nullable = false)
    private Terminal terminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doca")
    private Doca doca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estacionamento")
    private Estacionamento estacionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vaga")
    private VagaEstacionamento vaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operacao")
    private OperacaoCarga operacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro")
    private Usuario usuarioRegistro;

    @Column(name = "dt_incidente", nullable = false)
    private LocalDateTime ocorridoEm;

    @Column(name = "ds_incidente", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_incidente", nullable = false, length = 30)
    private StatusIncidente status = StatusIncidente.ABERTO;

    @Column(name = "acao_tomada", columnDefinition = "TEXT")
    private String acaoTomada;

    @Column(name = "dt_encerramento")
    private LocalDateTime encerradoEm;
}
