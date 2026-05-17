package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.NivelGravidade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_incidente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoIncidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_incidente")
    private Long id;

    @Column(name = "nm_tipo_incidente", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "ds_tipo_incidente", columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_gravidade", nullable = false, length = 30)
    private NivelGravidade nivelGravidade = NivelGravidade.BAIXO;
}
