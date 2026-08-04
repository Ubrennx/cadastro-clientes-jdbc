package com.projetoclientes.cadastroclientesjdbc;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
import com.projetoclientes.cadastroclientesjdbc.exceptions.DatabaseException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EntityNotFoundException;
import com.projetoclientes.cadastroclientesjdbc.repository.UsuarioRepository;
import com.projetoclientes.cadastroclientesjdbc.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UsuarioServiceTest.TestConfig.class, UsuarioRepository.class, UsuarioService.class})
@Sql(scripts = "classpath:db/migration/V1_create_usuarios_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE FROM usuarios", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("insert deve salvar usuário com senha criptografada")
    void insertDeveSalvarUsuarioComSenhaCriptografada() {
        Usuario usuario = novoUsuario("Joao Silva", 25, "joao@teste.com", "senha123", UsuarioRole.USUARIO);

        Usuario salvo = usuarioService.insert(usuario);

        assertThat(salvo.getId()).isNotNull();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, "joao@teste.com");
        assertEquals(1, count);

        String senhaSalva = jdbcTemplate.queryForObject(
                "SELECT senha FROM usuarios WHERE email = ?", String.class, "joao@teste.com");
        assertThat(senhaSalva).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", senhaSalva)).isTrue();
    }

    @Test
    @DisplayName("insert deve lancar DatabaseException quando o email ja existir")
    void insertDeveLancarDatabaseExceptionQuandoEmailJaExistir() {
        usuarioService.insert(novoUsuario("Joao Silva", 25, "duplicado@teste.com", "senha123", UsuarioRole.USUARIO));

        Usuario usuarioComEmailRepetido = novoUsuario(
                "Outro Nome", 30, "duplicado@teste.com", "outrasenha", UsuarioRole.USUARIO);

        assertThatThrownBy(() -> usuarioService.insert(usuarioComEmailRepetido))
                .isInstanceOf(DatabaseException.class);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, "duplicado@teste.com");
        assertEquals(1, count);
    }

    @Test
    @DisplayName("findById deve retornar o usuario quando existir")
    void findByIdDeveRetornarUsuarioQuandoExistir() {
        Usuario salvo = usuarioService.insert(
                novoUsuario("Maria Souza", 40, "maria@teste.com", "senha123", UsuarioRole.ADMIN));

        Usuario encontrado = usuarioService.findById(salvo.getId());

        assertThat(encontrado.getId()).isEqualTo(salvo.getId());
        assertThat(encontrado.getNome()).isEqualTo("Maria Souza");
        assertThat(encontrado.getEmail()).isEqualTo("maria@teste.com");
        assertThat(encontrado.getRole()).isEqualTo(UsuarioRole.ADMIN);
    }

    @Test
    @DisplayName("findById deve lancar EntityNotFoundException quando nao existir")
    void findByIdDeveLancarEntityNotFoundExceptionQuandoNaoExistir() {
        assertThatThrownBy(() -> usuarioService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("findByEmail deve retornar o usuario quando existir")
    void findByEmailDeveRetornarUsuarioQuandoExistir() {
        usuarioService.insert(novoUsuario("Carlos Lima", 22, "carlos@teste.com", "senha123", UsuarioRole.USUARIO));

        Usuario encontrado = usuarioService.findByEmail("carlos@teste.com");

        assertThat(encontrado.getEmail()).isEqualTo("carlos@teste.com");
        assertThat(encontrado.getNome()).isEqualTo("Carlos Lima");
    }

    @Test
    @DisplayName("findByEmail deve lancar EntityNotFoundException quando nao existir")
    void findByEmailDeveLancarEntityNotFoundExceptionQuandoNaoExistir() {
        assertThatThrownBy(() -> usuarioService.findByEmail("naoexiste@teste.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @TestConfiguration
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}