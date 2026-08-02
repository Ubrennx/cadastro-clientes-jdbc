package com.projetoclientes.cadastroclientesjdbc.service;

import com.projetoclientes.cadastroclientesjdbc.entities.ItemCompra;
import com.projetoclientes.cadastroclientesjdbc.exceptions.DatabaseException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EntityNotFoundException;
import com.projetoclientes.cadastroclientesjdbc.repository.ItemCompraRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;

    public ItemCompra insert(ItemCompra itemCompra) {
        return itemCompraRepository.save(itemCompra);
    }

    public List<ItemCompra> findAll() {
        return itemCompraRepository.findAll();
    }

    public ItemCompra findById(Long compraId, Long produtoId) {
        return itemCompraRepository.findById(compraId, produtoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ItemCompra nao encontrado com id: " + compraId + "/" + produtoId));
    }

    public ItemCompra update(Long compraId, Long produtoId, ItemCompra itemCompra) {
        ItemCompra entidade = itemCompraRepository.findById(compraId, produtoId)
                .orElseThrow(() -> new EntityNotFoundException("ItemCompra nao encontrado com id: " + compraId));

        updateData(entidade, itemCompra);
        return itemCompraRepository.update(entidade);
    }

    public void delete(Long compraId, Long produtoId) {
        try {
            itemCompraRepository.deleteById(compraId, produtoId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(ItemCompra entidade, ItemCompra itemCompra) {
        entidade.setQuantidade(itemCompra.getQuantidade());
        entidade.setPrecoUnitario(itemCompra.getPrecoUnitario());
    }
}
