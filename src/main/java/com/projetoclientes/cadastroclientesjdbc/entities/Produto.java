package com.projetoclientes.cadastroclientesjdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Produto {
    private Long id;
    private String codigoDeBarras;
    private String nome;
    private Double preco;
    private Integer qtdeEmEstoque;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
}
