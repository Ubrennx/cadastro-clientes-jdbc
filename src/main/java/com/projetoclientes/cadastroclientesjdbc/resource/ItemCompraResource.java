package com.projetoclientes.cadastroclientesjdbc.resource;

import com.projetoclientes.cadastroclientesjdbc.entities.ItemCompra;
import com.projetoclientes.cadastroclientesjdbc.service.ItemCompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-compra")
@AllArgsConstructor
public class ItemCompraResource {

    private final ItemCompraService itemCompraService;

    @PostMapping
    public ResponseEntity<ItemCompra> insert(@RequestBody ItemCompra itemCompra) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCompraService.insert(itemCompra));
    }

    @GetMapping
    public ResponseEntity<List<ItemCompra>> findAll() {
        return ResponseEntity.ok(itemCompraService.findAll());
    }

    @GetMapping("/{compraId}/{produtoId}")
    public ResponseEntity<ItemCompra> findById(@PathVariable Long compraId, @PathVariable Long produtoId) {
        return ResponseEntity.ok(itemCompraService.findById(compraId, produtoId));
    }

    @PutMapping("/{compraId}/{produtoId}")
    public ResponseEntity<ItemCompra> update(@PathVariable Long compraId, @PathVariable Long produtoId, @RequestBody ItemCompra itemCompra) {
        return ResponseEntity.ok(itemCompraService.update(compraId, produtoId, itemCompra));
    }

    @DeleteMapping("/{compraId}/{produtoId}")
    public ResponseEntity<Void> delete(@PathVariable Long compraId, @PathVariable Long produtoId) {
        itemCompraService.delete(compraId, produtoId);
        return ResponseEntity.noContent().build();
    }
}
