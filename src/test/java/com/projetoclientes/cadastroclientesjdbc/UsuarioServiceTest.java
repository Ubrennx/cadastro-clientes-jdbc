package com.projetoclientes.cadastroclientesjdbc;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
import com.projetoclientes.cadastroclientesjdbc.exceptions.DatabaseException;
import com.projetoclientes.cadastroclientesjdbc.exceptions.EntityNotFoundException;
import com.projetoclientes.cadastroclientesjdbc.repository.UsuarioRepository;
import com.projetoclientes.cadastroclientesjdbc.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Brenno", 0, "teste@gmail.com", "senha123", UsuarioRole.ADMIN);
    }

    @Test
    @DisplayName("insert deve salvar usuário com senha criptografada")
    void insertDeveSalvarUsuarioComSenhaCriptografada() {
        when(usuarioRepository.save(usuario)).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");

        Usuario salvo = usuarioService.insert(usuario);

        assertEquals("senhaCriptografada", salvo.getSenha());
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("insert deve lancar DatabaseException quando o email ja existir")
    void insertDeveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(DatabaseException.class, () -> usuarioService.insert(usuario));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("findById deve retornar o usuario quando existir")
    void findByIdDeveRetornarUsuarioQuandoExistir() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.findById(1L);

        assertEquals(usuario, encontrado);
    }

    @Test
    @DisplayName("findById deve lancar EntityNotFoundException quando nao existir")
    void findByIdDeveLancarExcecaoQuandoNaoExistir() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.findById(99L));
    }

    @Test
    @DisplayName("findByEmail deve retornar o usuario quando existir")
    void findByEmailDeveRetornarUsuarioQuandoExistir() {
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.findByEmail("joao@email.com");

        assertEquals(usuario, encontrado);
    }

    @Test
    @DisplayName("findByEmail deve lancar EntityNotFoundException quando nao existir")
    void findByEmailDeveLancarExcecaoQuandoNaoExistir() {
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.findByEmail("naoexiste@email.com"));
    }

    @Test
    @DisplayName("findAll deve retornar a lista de usuarios")
    void findAllDeveRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.findAll();

        assertThat(resultado).containsExactly(usuario);
    }

    @Test
    @DisplayName("update deve atualizar os dados e recriptografar a senha")
    void updateDeveAtualizarDadosERecriptografarSenha() {

        Usuario entidade = new Usuario(1L,"Antigo",20,"antigo@email.com","senhaAntiga",UsuarioRole.USUARIO);

        Usuario dadosNovos = new Usuario(null,"Novo Nome",25,"novo@email.com","novaSenha",UsuarioRole.USUARIO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entidade));

        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaCriptografada");

        when(usuarioRepository.update(entidade)).thenReturn(entidade);

        Usuario resultado = usuarioService.update(1L, dadosNovos);

        assertEquals("Novo Nome", resultado.getNome());
        assertEquals(25, resultado.getIdade());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("novaSenhaCriptografada", resultado.getSenha());

        verify(usuarioRepository, times(1)).update(entidade);
    }

    @Test
    @DisplayName("update deve lancar EntityNotFoundException quando o usuario nao existir")
    void updateDeveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findById(99L))
                .thenThrow(new EntityNotFoundException("nao encontrado"));

        assertThrows(EntityNotFoundException.class, () -> usuarioService.update(99L, usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete deve remover usuário quando existir")
    void deleteDeveRemoverUsuario() {
        when(usuarioRepository.deleteById(1L)).thenReturn(1);

        usuarioService.delete(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete deve lancar DatabaseException quando houver violacao de integridade")
    void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        doThrow(new DataIntegrityViolationException("violacao"))
                .when(usuarioRepository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> usuarioService.delete(1L));
    }
}
