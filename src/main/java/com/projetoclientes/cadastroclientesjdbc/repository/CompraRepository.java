package com.projetoclientes.cadastroclientesjdbc.repository;

import com.projetoclientes.cadastroclientesjdbc.entities.Compra;
import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
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
public class CompraRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // join com usuarios para popular o "dono" da compra, que antes vinha do @ManyToOne
    private static final String SELECT_BASE =
            "SELECT c.id AS id, c.data_compra AS data_compra, c.valor_total AS valor_total, " +
            "u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email " +
            "FROM compra c JOIN usuarios u ON u.id = c.cliente_id ";

    private static final RowMapper<Compra> ROW_MAPPER = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("usuario_id"));
        usuario.setNome(rs.getString("usuario_nome"));
        usuario.setEmail(rs.getString("usuario_email"));

        Compra compra = new Compra();
        compra.setId(rs.getLong("id"));

        Timestamp dataCompra = rs.getTimestamp("data_compra");
        compra.setDataCompra(dataCompra != null ? dataCompra.toLocalDateTime() : null);

        compra.setValorTotal(rs.getObject("valor_total", Double.class));
        compra.setUsuario(usuario);
        return compra;
    };

    public Compra save(Compra compra) {
        String sql = "INSERT INTO compra (data_compra, cliente_id, valor_total) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setTimestamp(1, compra.getDataCompra() != null ? Timestamp.valueOf(compra.getDataCompra()) : null);
            ps.setLong(2, compra.getUsuario().getId());
            ps.setDouble(3, compra.getValorTotal());
            return ps;
        }, keyHolder);

        compra.setId(keyHolder.getKey().longValue());
        return compra;
    }

    public Optional<Compra> findById(Long id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, id).stream().findFirst();
    }

    public List<Compra> findAll() {
        return jdbcTemplate.query(SELECT_BASE, ROW_MAPPER);
    }

    public List<Compra> findByUsuarioId(Long usuarioId) {
        String sql = SELECT_BASE + "WHERE c.cliente_id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, usuarioId);
    }

    public Compra update(Compra compra) {
        String sql = "UPDATE compra SET data_compra = ?, cliente_id = ?, valor_total = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                compra.getDataCompra() != null ? Timestamp.valueOf(compra.getDataCompra()) : null,
                compra.getUsuario().getId(),
                compra.getValorTotal(),
                compra.getId());
        return compra;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM compra WHERE id = ?", id);
    }
}
