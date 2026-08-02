package com.projetoclientes.cadastroclientesjdbc.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Compra {
    private Long id;
    private LocalDateTime dataCompra;
    private Usuario usuario;
    private List<ItemCompra> itens = new ArrayList<>();
    private Double valorTotal;

    public Compra(Long id, LocalDateTime dataCompra, Usuario usuario, Double valorTotal) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.usuario = usuario;
        this.valorTotal = valorTotal;
    }

    public Double getValorTotal() {
        double soma = 0.0;

        if (itens != null) {
            for (ItemCompra item : itens) {
                soma += item.getSubTotal();
            }
        }
        return soma;
    }
}
