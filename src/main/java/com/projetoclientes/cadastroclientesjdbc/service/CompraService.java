package com.projetoclientes.cadastroclientesjdbc.service;

import com.projetoclientes.cadastroclientesjdbc.dto.response.CompraResponseDTO;
import com.projetoclientes.cadastroclientesjdbc.entities.Compra;
import com.projetoclientes.cadastroclientesjdbc.entities.ItemCompra;
import com.projetoclientes.cadastroclientesjdbc.entities.Produto;
import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.exceptions.DatabaseException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EntityNotFoundException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EstoqueInsuficienteException;
import com.projetoclientes.cadastroclientesjdbc.repository.CompraRepository;
import com.projetoclientes.cadastroclientesjdbc.repository.ItemCompraRepository;
import com.projetoclientes.cadastroclientesjdbc.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final ItemCompraRepository itemCompraRepository;
    private final ProdutoService produtoService;
    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    // sem cascade automatico do JPA, a transacao garante que compra + itens + baixa de estoque
    // sejam gravados (ou revertidos) juntos
    @Transactional
    public Compra insert(Compra compra) {
        compra.setDataCompra(LocalDateTime.now());

        Optional<Usuario> usuario = usuarioRepository.findById(compra.getUsuario().getId());
        compra.setUsuario(usuario.get());

        List<ItemCompra> itensProcessados = new ArrayList<>();
        double valorTotal = 0.0;

        for (ItemCompra item : compra.getItens()) {
            Produto produto = produtoService.findById(item.getProduto().getId());

            if (produto.getQtdeEmEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQtdeEmEstoque(produto.getQtdeEmEstoque() - item.getQuantidade());
            produtoService.update(produto.getId(), produto);

            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());

            valorTotal += produto.getPreco() * item.getQuantidade();
            itensProcessados.add(item);
        }

        compra.setItens(itensProcessados);
        compra.setValorTotal(valorTotal);

        Compra salva = compraRepository.save(compra);

        for (ItemCompra item : itensProcessados) {
            item.setCompra(salva);
            itemCompraRepository.save(item);
        }

        salva.setItens(itensProcessados);

        notificacaoService.notificarCompra(new CompraResponseDTO(salva));
        return salva;
    }

    public List<Compra> findAll() {
        List<Compra> compras = compraRepository.findAll();
        compras.forEach(this::carregarItens);
        return compras;
    }

    public List<CompraResponseDTO> findAllDTO() {
        return findAll().stream()
                .map(CompraResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<CompraResponseDTO> findByUsuarioDTO(Long usuarioId) {
        List<Compra> compras = compraRepository.findByUsuarioId(usuarioId);
        compras.forEach(this::carregarItens);
        return compras.stream()
                .map(CompraResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Compra findById(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra nao encontrada com id: " + id));
        carregarItens(compra);
        return compra;
    }

    public CompraResponseDTO findByIdDTO(Long id) {
        return new CompraResponseDTO(findById(id));
    }

    @Transactional
    public Compra update(Long id, Compra compra) {
        Compra entidade = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra nao encontrada com id: " + id));
        carregarItens(entidade);

        updateData(entidade, compra);
        Compra atualizada = compraRepository.update(entidade);
        atualizada.setItens(entidade.getItens());
        return atualizada;
    }

    public void delete(Long id) {
        try {
            itemCompraRepository.deleteByCompraId(id);
            compraRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // sem lazy-loading do JPA, os itens de cada compra precisam ser buscados manualmente
    private void carregarItens(Compra compra) {
        compra.setItens(itemCompraRepository.findByCompraId(compra.getId()));
    }

    private void updateData(Compra entidade, Compra compra) {
        entidade.setUsuario(compra.getUsuario());

        if (compra.getItens() != null) {
            itemCompraRepository.deleteByCompraId(entidade.getId());

            List<ItemCompra> novosItens = new ArrayList<>();
            double valorTotal = 0.0;

            for (ItemCompra item : compra.getItens()) {
                item.setCompra(entidade);

                Produto produto = produtoService.findById(item.getProduto().getId());
                item.setProduto(produto);
                item.setPrecoUnitario(produto.getPreco());

                valorTotal += produto.getPreco() * item.getQuantidade();

                itemCompraRepository.save(item);
                novosItens.add(item);
            }

            entidade.setItens(novosItens);
            entidade.setValorTotal(valorTotal);
        } else {
            entidade.setValorTotal(compra.getValorTotal());
        }
    }
}
