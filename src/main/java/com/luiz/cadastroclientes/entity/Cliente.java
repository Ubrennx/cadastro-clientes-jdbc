package com.luiz.cadastroclientes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cliente {
    private Long id;
    private String nome;
    private Integer idade;
    private String email;
    private String senha;

}
