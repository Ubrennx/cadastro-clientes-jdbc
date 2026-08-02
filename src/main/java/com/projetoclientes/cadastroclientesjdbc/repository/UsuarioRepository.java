package com.projetoclientes.cadastroclientesjdbc.repository;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
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
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Usuario> ROW_MAPPER = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setIdade(rs.getObject("idade", Integer.class));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        String role = rs.getString("role");
        usuario.setRole(role != null ? UsuarioRole.valueOf(role) : null);
        return usuario;
    };

    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, idade, email, senha, role) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, usuario.getNome());
            if (usuario.getIdade() != null) {
                ps.setInt(2, usuario.getIdade());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getSenha());
            ps.setString(5, usuario.getRole() != null ? usuario.getRole().name() : null);
            return ps;
        }, keyHolder);

        usuario.setId(keyHolder.getKey().longValue());
        return usuario;
    }

    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, id).stream().findFirst();
    }

    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, email).stream().findFirst();
    }

    public List<Usuario> findAll() {
        return jdbcTemplate.query("SELECT * FROM usuarios", ROW_MAPPER);
    }

    public Usuario update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, idade = ?, email = ?, senha = ?, role = ? WHERE id = ?";
        jdbcTemplate.update(sql, usuario.getNome(), usuario.getIdade(), usuario.getEmail(),
                usuario.getSenha(), usuario.getRole() != null ? usuario.getRole().name() : null, usuario.getId());
        return usuario;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM usuarios WHERE id = ?", id);
    }
}
