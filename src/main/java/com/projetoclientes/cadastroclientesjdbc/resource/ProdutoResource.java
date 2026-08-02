package com.projetoclientes.cadastroclientesjdbc.resource;

import com.projetoclientes.cadastroclientesjdbc.entities.Produto;
import com.projetoclientes.cadastroclientesjdbc.service.ProdutoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@AllArgsConstructor
public class ProdutoResource {
    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> insert(@RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.insert(produto));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> findAll() {
        return ResponseEntity.ok().body(produtoService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(produtoService.findById(id));
    }

    @GetMapping(value = "/buscar")
    public ResponseEntity<Produto> findByCodigoDeBarras(@RequestParam("codigo-de-barras") String codigoDeBarras) {
        return ResponseEntity.ok().body(produtoService.findByCodigoDeBarras(codigoDeBarras));
    }

    @PutMapping("/{id}")
    public Produto update(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.update(id, produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
