package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "documento_carga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operacao", nullable = false)
    private OperacaoCarga operacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_documento", nullable = false, length = 50)
    private TipoDocumento tipo;

    @Column(name = "nr_documento", nullable = false, length = 80)
    private String numero;

    @Column(name = "dt_emissao")
    private LocalDate emitidoEm;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;
}
