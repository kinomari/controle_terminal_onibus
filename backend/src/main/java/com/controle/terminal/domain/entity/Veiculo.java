package com.controle.terminal.domain.entity;

import com.controle.terminal.domain.enums.TipoEmpresa;
import com.controle.terminal.domain.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veiculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Long id;

    @Column(name = "nr_placa", nullable = false, unique = true, length = 10)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_veiculo", nullable = false, length = 40)
    private TipoVeiculo tipo;

    @Column(name = "nm_empresa_responsavel", nullable = false, length = 150)
    private String empresaResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_empresa_responsavel", nullable = false, length = 30)
    private TipoEmpresa tipoEmpresa;

    @Column(name = "ds_modelo", length = 80)
    private String modelo;
}
