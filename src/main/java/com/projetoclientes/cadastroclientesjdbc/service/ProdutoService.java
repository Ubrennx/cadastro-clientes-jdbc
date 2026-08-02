package com.projetoclientes.cadastroclientesjdbc.service;

import com.projetoclientes.cadastroclientesjdbc.entities.Produto;
import com.projetoclientes.cadastroclientesjdbc.exceptions.DatabaseException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EntityNotFoundException;
import com.projetoclientes.cadastroclientesjdbc.repository.ProdutoRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public Produto insert(Produto produto) {
        if (produtoRepository.findByCodigoDeBarras(produto.getCodigoDeBarras()).isPresent()) {
            throw new DatabaseException("Já existe um produto com esse código de barras");
        }

        produto.setDataCriacao(LocalDateTime.now());
        produto.setDataUltimaAtualizacao(LocalDateTime.now());
        return produtoRepository.save(produto);
    }

    public Produto findById(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.orElseThrow(() -> new EntityNotFoundException(
                "entidade não encontrada com id: " + id));
    }

    public Produto findByCodigoDeBarras(String codigoDeBarras) {
        return produtoRepository.findByCodigoDeBarras(codigoDeBarras)
                .orElseThrow(() -> new EntityNotFoundException(
                        "entidade não encontrada com o código de barras" + codigoDeBarras));
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public void updateData(Produto entidade, Produto produto) {
        entidade.setCodigoDeBarras(produto.getCodigoDeBarras());
        entidade.setNome(produto.getNome());
        entidade.setPreco(produto.getPreco());
        entidade.setQtdeEmEstoque(produto.getQtdeEmEstoque());
        entidade.setDataUltimaAtualizacao(LocalDateTime.now());
    }

    public Produto update(Long id, Produto produto) {
        Produto entidade = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade não encontrada com id: " + id));

        updateData(entidade, produto);
        return produtoRepository.update(entidade);
    }

    public void delete(Long id) {
        try {
            produtoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}
