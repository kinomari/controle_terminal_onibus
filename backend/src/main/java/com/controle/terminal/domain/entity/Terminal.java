package com.controle.terminal.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terminal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_terminal")
    private Long id;

    @Column(name = "nm_terminal", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_endereco", length = 200)
    private String endereco;

    @Column(name = "nm_cidade", nullable = false, length = 80)
    private String cidade;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
}
