package projeto.springframework.projetoestagio.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import projeto.springframework.projetoestagio.controller.dto.user.UserDTO;
import projeto.springframework.projetoestagio.controller.dto.user.UserPerfilDTO;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.enun.Papel;
import projeto.springframework.projetoestagio.exception.RegraNegocioException;
import projeto.springframework.projetoestagio.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private org.springframework.security.core.Authentication authentication;

    @InjectMocks
    private UserServiceImp userService;



@Test
void deveCriarUsuarioComSucesso() {

    UserDTO dto = new UserDTO();
    dto.setNome("João");
    dto.setEmail("joao@email.com");
    dto.setSenha("123456");

    when(userRepository.existsByEmail(dto.getEmail()))
            .thenReturn(false);

    when(passwordEncoder.encode("123456"))
            .thenReturn("senhaCriptografada");

    User userSalvo = new User();
    userSalvo.setId(1L);
    userSalvo.setNome("João");
    userSalvo.setEmail("joao@email.com");
    userSalvo.setSenha("senhaCriptografada");
    userSalvo.setPapel(Papel.ROLE_ALUNO);

    when(userRepository.save(Mockito.any(User.class)))
            .thenReturn(userSalvo);

    UserPerfilDTO resultado = userService.criarUser(dto);

    assertNotNull(resultado);
    assertEquals("João", resultado.getNome());
    assertEquals("joao@email.com", resultado.getEmail());

    verify(userRepository).save(Mockito.any(User.class));
}
@Test
    void deveLancarExcecaoQuandoEmailJaExiste() {

        UserDTO dto = new UserDTO();
        dto.setNome("João");
        dto.setEmail("joao@email.com");
        dto.setSenha("123456");

        when(userRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> {
            userService.criarUser(dto);
        });

        verify(userRepository, never()).save(any());
    }


    @Test
    void deveBuscarUsuarioPorId() {

        User user = new User();
        user.setId(1L);
        user.setNome("João");
        user.setEmail("joao@email.com");
        user.setPapel(Papel.ROLE_ALUNO);

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        var resultado = userService.buscarUsuarioPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João", resultado.get().getNome());
    }

    @Test
    void deveRetornarOptionalVazioQuandoNaoEncontrarPorId() {

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        var resultado = userService.buscarUsuarioPorId(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBuscarPorEmail() {

        User user = new User();
        user.setId(1L);
        user.setNome("Maria");
        user.setEmail("maria@email.com");

        when(userRepository.findByEmail("maria@email.com"))
                .thenReturn(java.util.Optional.of(user));

        var resultado = userService.buscarPorEmail("maria@email.com");

        assertEquals("Maria", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoEncontrado() {

        when(userRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException.class,
                () -> userService.buscarPorEmail("naoexiste@email.com"));
    }

    @Test
    void deveListarUsuarios() {

        User user1 = new User();
        user1.setId(1L);
        user1.setNome("João");
        user1.setEmail("joao@email.com");
        user1.setPapel(Papel.ROLE_ALUNO);

        when(userRepository.findAll())
                .thenReturn(java.util.List.of(user1));

        var resultado = userService.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("João", resultado.get(0).getNome());
    }

    @Test
    void deveDeletarUsuario() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        userService.deletar(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {

        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException.class,
                () -> userService.deletar(1L));

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deveAtualizarPapelUsuario() {

        User user = new User();
        user.setId(1L);
        user.setPapel(Papel.ROLE_ALUNO);

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizacaoPapelDTO();
        dto.setPapel(Papel.ROLE_ADMIN);

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);

        var resultado = userService.atualizarUser(1L, dto);

        assertEquals(Papel.ROLE_ADMIN, resultado.getPapel());
    }

    @Test
    void naoDevePermitirRebaixarAdmin() {

        User user = new User();
        user.setId(1L);
        user.setPapel(Papel.ROLE_ADMIN);

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizacaoPapelDTO();
        dto.setPapel(Papel.ROLE_ALUNO);

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        assertThrows(RegraNegocioException.class,
                () -> userService.atualizarUser(1L, dto));
    }

    @Test
    void adminPodeAlterarSenhaDeQualquerUsuario() {

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@email.com");
        admin.setPapel(Papel.ROLE_ADMIN);

        User usuario = new User();
        usuario.setId(2L);
        usuario.setSenha("senhaAntiga");

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();
        dto.setNovaSenha("novaSenha");

        when(authentication.getName()).thenReturn("admin@email.com");
        when(userRepository.findByEmail("admin@email.com"))
                .thenReturn(java.util.Optional.of(admin));
        when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha"))
                .thenReturn("senhaCriptografada");

        userService.mudarSenha(2L, dto, authentication);

        verify(userRepository).save(usuario);
    }

    @Test
    void usuarioNaoPodeAlterarSenhaDeOutroUsuario() {

        User logado = new User();
        logado.setId(1L);
        logado.setEmail("user@email.com");
        logado.setPapel(Papel.ROLE_ALUNO);

        User outro = new User();
        outro.setId(2L);

        when(authentication.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(logado));
        when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.of(outro));

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();

        assertThrows(RegraNegocioException.class,
                () -> userService.mudarSenha(2L, dto, authentication));

        verify(userRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {

        User usuario = new User();
        usuario.setId(1L);
        usuario.setEmail("user@email.com");
        usuario.setPapel(Papel.ROLE_ALUNO);
        usuario.setSenha("senhaCriptografada");

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();
        dto.setSenhaAtual("errada");
        dto.setNovaSenha("novaSenha");

        when(authentication.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(usuario));
        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("errada", "senhaCriptografada"))
                .thenReturn(false);

        assertThrows(RegraNegocioException.class,
                () -> userService.mudarSenha(1L, dto, authentication));
    }

    @Test
    void usuarioDeveAlterarPropriaSenha() {

        User usuario = new User();
        usuario.setId(1L);
        usuario.setEmail("user@email.com");
        usuario.setPapel(Papel.ROLE_ALUNO);
        usuario.setSenha("senhaCriptografada");

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();
        dto.setSenhaAtual("senhaAntiga");
        dto.setNovaSenha("novaSenha");

        when(authentication.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(usuario));
        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("senhaAntiga", "senhaCriptografada"))
                .thenReturn(true);
        when(passwordEncoder.encode("novaSenha"))
                .thenReturn("novaSenhaCriptografada");

        userService.mudarSenha(1L, dto, authentication);

        verify(userRepository).save(usuario);
    }

    @Test
    void deveMudarMinhaSenha() {

        User usuario = new User();
        usuario.setEmail("user@email.com");
        usuario.setSenha("senhaCriptografada");

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();
        dto.setSenhaAtual("senhaAntiga");
        dto.setNovaSenha("novaSenha");

        when(authentication.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("senhaAntiga", "senhaCriptografada"))
                .thenReturn(true);
        when(passwordEncoder.encode("novaSenha"))
                .thenReturn("novaCriptografada");

        userService.mudarMinhaSenha(dto, authentication);

        verify(userRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoMinhaSenhaAtualIncorreta() {

        User usuario = new User();
        usuario.setEmail("user@email.com");
        usuario.setSenha("senhaCriptografada");

        var dto = new projeto.springframework.projetoestagio.controller.dto.user.UserAtualizarSenhaDTO();
        dto.setSenhaAtual("errada");

        when(authentication.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("errada", "senhaCriptografada"))
                .thenReturn(false);

        assertThrows(RegraNegocioException.class,
                () -> userService.mudarMinhaSenha(dto, authentication));
    }

    @Test
    void deveAtualizarMeuNome() {

        User usuario = new User();
        usuario.setEmail("user@email.com");
        usuario.setNome("Nome Antigo");

        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(java.util.Optional.of(usuario));

        userService.atualizarMeuNome("user@email.com", "Nome Novo");

        verify(userRepository).save(usuario);
        assertEquals("Nome Novo", usuario.getNome());
    }

}
