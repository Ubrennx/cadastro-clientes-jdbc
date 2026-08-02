package com.projetoclientes.cadastroclientesjdbc.dto.response;

import com.projetoclientes.cadastroclientesjdbc.entities.ItemCompra;

public record ItemCompraResponseDTO(
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        Double precoUnitario,
        Double subTotal
) {
    public ItemCompraResponseDTO(ItemCompra item) {
        this(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubTotal()
        );
    }
}
