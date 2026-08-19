package com.projetoclientes.cadastroclientesjdbc.repository;

import com.projetoclientes.cadastroclientesjdbc.entities.Produto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProdutoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Produto> ROW_MAPPER = (rs, rowNum) -> {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setCodigoDeBarras(rs.getString("codigo_de_barras"));
        produto.setNome(rs.getString("nome"));
        produto.setPreco(rs.getObject("preco", Double.class));
        produto.setQtdeEmEstoque(rs.getObject("quantidade_em_estoque", Integer.class));

        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        produto.setDataCriacao(dataCriacao != null ? dataCriacao.toLocalDateTime() : null);

        Timestamp dataAtualizacao = rs.getTimestamp("data_ultima_atualizacao");
        produto.setDataUltimaAtualizacao(dataAtualizacao != null ? dataAtualizacao.toLocalDateTime() : null);

        return produto;
    };

    public Produto save(Produto produto) {
        String sql = "INSERT INTO produto (codigo_de_barras, nome, preco, quantidade_em_estoque, " +
                "data_criacao, data_ultima_atualizacao) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, produto.getCodigoDeBarras());
            ps.setString(2, produto.getNome());
            ps.setDouble(3, produto.getPreco());
            ps.setInt(4, produto.getQtdeEmEstoque());
            ps.setTimestamp(5, produto.getDataCriacao() != null ? Timestamp.valueOf(produto.getDataCriacao()) : null);
            ps.setTimestamp(6, produto.getDataUltimaAtualizacao() != null ? Timestamp.valueOf(produto.getDataUltimaAtualizacao()) : null);
            return ps;
        }, keyHolder);

        produto.setId(keyHolder.getKey().longValue());
        return produto;
    }

    public Optional<Produto> findById(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, id).stream().findFirst();
    }

    public Optional<Produto> findByCodigoDeBarras(String codigoDeBarras) {
        String sql = "SELECT * FROM produto WHERE codigo_de_barras = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, codigoDeBarras).stream().findFirst();
    }

    public List<Produto> findAll() {
        return jdbcTemplate.query("SELECT * FROM produto", ROW_MAPPER);
    }

    public Produto update(Produto produto) {
        String sql = "UPDATE produto SET codigo_de_barras = ?, nome = ?, preco = ?, quantidade_em_estoque = ?, " +
                "data_criacao = ?, data_ultima_atualizacao = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                produto.getCodigoDeBarras(),
                produto.getNome(),
                produto.getPreco(),
                produto.getQtdeEmEstoque(),
                produto.getDataCriacao() != null ? Timestamp.valueOf(produto.getDataCriacao()) : null,
                produto.getDataUltimaAtualizacao() != null ? Timestamp.valueOf(produto.getDataUltimaAtualizacao()) : null,
                produto.getId());
        return produto;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM produto WHERE id = ?", id);
    }
}
