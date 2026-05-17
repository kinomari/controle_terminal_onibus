package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.StatusOperacao;
import com.controle.terminal.domain.enums.TipoOperacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operacao_carga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_terminal", nullable = false)
    private Terminal terminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doca")
    private Doca doca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vaga")
    private VagaEstacionamento vaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veiculo")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_operacao", nullable = false, length = 30)
    private TipoOperacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_operacao", nullable = false, length = 30)
    private StatusOperacao status = StatusOperacao.AGENDADA;

    @Column(name = "ds_carga", length = 200)
    private String descricaoCarga;

    @Column(name = "qtd_volume")
    private Integer quantidadeVolume;

    @Column(name = "peso_estimado", precision = 10, scale = 2)
    private BigDecimal pesoEstimado;

    @Column(name = "dt_agendada")
    private LocalDateTime agendadaEm;

    @Column(name = "dt_inicio")
    private LocalDateTime iniciadaEm;

    @Column(name = "dt_fim")
    private LocalDateTime finalizadaEm;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;
}
