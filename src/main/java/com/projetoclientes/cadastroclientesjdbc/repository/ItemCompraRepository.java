package com.projetoclientes.cadastroclientesjdbc.repository;

import com.projetoclientes.cadastroclientesjdbc.entities.Compra;
import com.projetoclientes.cadastroclientesjdbc.entities.ItemCompra;
import com.projetoclientes.cadastroclientesjdbc.entities.Produto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemCompraRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemCompraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // join com produto para popular o item completo (nome, preco etc), que antes vinha do @ManyToOne
    private static final String SELECT_BASE =
            "SELECT ic.compra_id AS compra_id, ic.produto_id AS produto_id, " +
            "ic.quantidade AS quantidade, ic.preco_unitario AS preco_unitario, " +
            "p.id AS p_id, p.codigo_de_barras AS p_codigo_de_barras, p.nome AS p_nome, " +
            "p.preco AS p_preco, p.quatidade_em_estoque AS p_quatidade_em_estoque, " +
            "p.data_criacao AS p_data_criacao, p.data_ultima_atualizacao AS p_data_ultima_atualizacao " +
            "FROM item_compra ic JOIN produto p ON p.id = ic.produto_id ";

    private static final RowMapper<ItemCompra> ROW_MAPPER = (rs, rowNum) -> {
        Produto produto = new Produto();
        produto.setId(rs.getLong("p_id"));
        produto.setCodigoDeBarras(rs.getString("p_codigo_de_barras"));
        produto.setNome(rs.getString("p_nome"));
        produto.setPreco(rs.getObject("p_preco", Double.class));
        produto.setQtdeEmEstoque(rs.getObject("p_quatidade_em_estoque", Integer.class));

        Timestamp dataCriacao = rs.getTimestamp("p_data_criacao");
        produto.setDataCriacao(dataCriacao != null ? dataCriacao.toLocalDateTime() : null);

        Timestamp dataAtualizacao = rs.getTimestamp("p_data_ultima_atualizacao");
        produto.setDataUltimaAtualizacao(dataAtualizacao != null ? dataAtualizacao.toLocalDateTime() : null);

        Compra compra = new Compra();
        compra.setId(rs.getLong("compra_id"));

        ItemCompra item = new ItemCompra();
        item.setCompra(compra);
        item.setProduto(produto);
        item.setQuantidade(rs.getObject("quantidade", Integer.class));
        item.setPrecoUnitario(rs.getObject("preco_unitario", Double.class));
        return item;
    };

    public ItemCompra save(ItemCompra itemCompra) {
        String sql = "INSERT INTO item_compra (compra_id, produto_id, quantidade, preco_unitario) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (compra_id, produto_id) DO UPDATE SET " +
                "quantidade = EXCLUDED.quantidade, preco_unitario = EXCLUDED.preco_unitario";

        jdbcTemplate.update(sql,
                itemCompra.getCompra().getId(),
                itemCompra.getProduto().getId(),
                itemCompra.getQuantidade(),
                itemCompra.getPrecoUnitario());

        return itemCompra;
    }

    public List<ItemCompra> findAll() {
        return jdbcTemplate.query(SELECT_BASE, ROW_MAPPER);
    }

    public List<ItemCompra> findByCompraId(Long compraId) {
        String sql = SELECT_BASE + "WHERE ic.compra_id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, compraId);
    }

    public Optional<ItemCompra> findById(Long compraId, Long produtoId) {
        String sql = SELECT_BASE + "WHERE ic.compra_id = ? AND ic.produto_id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, compraId, produtoId).stream().findFirst();
    }

    public ItemCompra update(ItemCompra itemCompra) {
        String sql = "UPDATE item_compra SET quantidade = ?, preco_unitario = ? WHERE compra_id = ? AND produto_id = ?";
        jdbcTemplate.update(sql,
                itemCompra.getQuantidade(),
                itemCompra.getPrecoUnitario(),
                itemCompra.getCompra().getId(),
                itemCompra.getProduto().getId());
        return itemCompra;
    }

    public void deleteById(Long compraId, Long produtoId) {
        jdbcTemplate.update("DELETE FROM item_compra WHERE compra_id = ? AND produto_id = ?", compraId, produtoId);
    }

    public void deleteByCompraId(Long compraId) {
        jdbcTemplate.update("DELETE FROM item_compra WHERE compra_id = ?", compraId);
    }
}
