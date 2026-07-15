package com.luiz.cadastroclientes.repository;

import com.luiz.cadastroclientes.entity.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Cliente> ROW_MAPPER = (rs, rowNum) -> {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setIdade(rs.getObject("idade", Integer.class));
        cliente.setEmail(rs.getString("email"));
        cliente.setSenha(rs.getString("senha"));
        return cliente;
    };

    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, idade, email, senha) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, cliente.getNome());
            if (cliente.getIdade() != null) {
                ps.setInt(2, cliente.getIdade());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getSenha());
            return ps;
        }, keyHolder);

        cliente.setId(keyHolder.getKey().longValue());
        return cliente;
    }

    public Optional<Cliente> findById(Long id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        List<Cliente> resultado = jdbcTemplate.query(sql, ROW_MAPPER, id);
        return resultado.stream().findFirst();
    }

    public Optional<Cliente> findByEmail(String email) {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        List<Cliente> resultado = jdbcTemplate.query(sql, ROW_MAPPER, email);
        return resultado.stream().findFirst();
    }

    public List<Cliente> findAll() {
        String sql = "SELECT * FROM clientes";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public Cliente update(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, idade = ?, email = ?, senha = ? WHERE id = ?";
        jdbcTemplate.update(sql, cliente.getNome(), cliente.getIdade(), cliente.getEmail(),
                cliente.getSenha(), cliente.getId());
        return cliente;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
