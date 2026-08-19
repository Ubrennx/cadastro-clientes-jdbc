package com.projetoclientes.cadastroclientesjdbc;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
import com.projetoclientes.cadastroclientesjdbc.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {UsuarioRepositoryTest.TestConfig.class, UsuarioRepository.class})
@Sql(scripts = "classpath:db/migration/V1__create_usuarios_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE FROM usuarios", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Usuario novoUsuario(String nome, Integer idade, String email, String senha, UsuarioRole role) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setIdade(idade);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setRole(role);
        return usuario;
    }

    @Test
    @DisplayName("save deve persistir o usuario e gerar um id")
    void saveDevePersistirUsuarioEGerarId() {
        Usuario usuario = novoUsuario("Joao Silva", 25, "joao@teste.com", "senha123", UsuarioRole.USUARIO);

        Usuario salvo = usuarioRepository.save(usuario);

        assertThat(salvo.getId()).isNotNull();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, "joao@teste.com");
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("findById deve retornar o usuario quando existir")
    void findByIdDeveRetornarUsuarioQuandoExistir() {
        Usuario salvo = usuarioRepository.save(
                novoUsuario("Maria Souza", 40, "maria@teste.com", "senha123", UsuarioRole.ADMIN));

        Optional<Usuario> encontrado = usuarioRepository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertThat(encontrado.get().getNome()).isEqualTo("Maria Souza");
        assertThat(encontrado.get().getEmail()).isEqualTo("maria@teste.com");
        assertThat(encontrado.get().getRole()).isEqualTo(UsuarioRole.ADMIN);
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando nao existir")
    void findByIdDeveRetornarOptionalVazioQuandoNaoExistir() {
        Optional<Usuario> encontrado = usuarioRepository.findById(999L);

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("findByEmail deve retornar o usuario quando existir")
    void findByEmailDeveRetornarUsuarioQuandoExistir() {
        usuarioRepository.save(novoUsuario("Carlos Lima", 22, "carlos@teste.com", "senha123", UsuarioRole.USUARIO));

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("carlos@teste.com");

        assertTrue(encontrado.isPresent());
        assertThat(encontrado.get().getNome()).isEqualTo("Carlos Lima");
    }

    @Test
    @DisplayName("findByEmail deve retornar Optional vazio quando nao existir")
    void findByEmailDeveRetornarOptionalVazioQuandoNaoExistir() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("naoexiste@teste.com");

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("findAll deve retornar todos os usuarios")
    void findAllDeveRetornarTodosOsUsuarios() {
        usuarioRepository.save(novoUsuario("Usuario 1", 20, "u1@teste.com", "senha123", UsuarioRole.USUARIO));
        usuarioRepository.save(novoUsuario("Usuario 2", 21, "u2@teste.com", "senha123", UsuarioRole.USUARIO));

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(Usuario::getEmail)
                .containsExactlyInAnyOrder("u1@teste.com", "u2@teste.com");
    }

    @Test
    @DisplayName("update deve atualizar os dados do usuario")
    void updateDeveAtualizarDadosDoUsuario() {
        Usuario salvo = usuarioRepository.save(
                novoUsuario("Nome Antigo", 25, "atualizar@teste.com", "senhaAntiga", UsuarioRole.USUARIO));

        salvo.setNome("Nome Novo");
        salvo.setIdade(30);
        salvo.setEmail("novo@teste.com");
        salvo.setSenha("senhaNova");

        Usuario atualizado = usuarioRepository.update(salvo);

        assertThat(atualizado.getNome()).isEqualTo("Nome Novo");
        assertThat(atualizado.getIdade()).isEqualTo(30);
        assertThat(atualizado.getEmail()).isEqualTo("novo@teste.com");

        String senhaSalva = jdbcTemplate.queryForObject(
                "SELECT senha FROM usuarios WHERE id = ?", String.class, salvo.getId());
        assertThat(senhaSalva).isEqualTo("senhaNova");
    }

    @Test
    @DisplayName("deleteById deve remover o usuario e retornar 1 quando existir")
    void deleteByIdDeveRemoverUsuarioQuandoExistir() {
        Usuario salvo = usuarioRepository.save(
                novoUsuario("Ana Lima", 28, "ana@teste.com", "senha123", UsuarioRole.USUARIO));

        int linhasAfetadas = usuarioRepository.deleteById(salvo.getId());

        assertThat(linhasAfetadas).isEqualTo(1);
        assertThat(usuarioRepository.findById(salvo.getId())).isEmpty();
    }

    @Test
    @DisplayName("deleteById deve retornar 0 quando o usuario nao existir")
    void deleteByIdDeveRetornarZeroQuandoUsuarioNaoExistir() {
        int linhasAfetadas = usuarioRepository.deleteById(999L);

        assertThat(linhasAfetadas).isEqualTo(0);
    }

    @TestConfiguration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}