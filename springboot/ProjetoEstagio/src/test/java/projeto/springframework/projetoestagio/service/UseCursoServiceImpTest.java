package projeto.springframework.projetoestagio.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.entity.UserCurso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;
import projeto.springframework.projetoestagio.exception.RegraNegocioException;
import projeto.springframework.projetoestagio.repository.CursoRepository;
import projeto.springframework.projetoestagio.repository.UserCursoRepository;
import projeto.springframework.projetoestagio.repository.UserRepository;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UseCursoServiceImpTest {

    @Mock
    private UserCursoRepository userCursoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private UseCursoServiceImp service;

    private Authentication mockAuth(String email, String role) {
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn(email);

        Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        doReturn(authorities).when(auth).getAuthorities();

        return auth;
    }



    @Test
    void deveCriarMatricula() {

        User user = new User();
        user.setEmail("user@email.com");

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setAtivo(true);

        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(Optional.of(user));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        when(userCursoRepository.findByUserAndCurso(user, curso))
                .thenReturn(Optional.empty());

        when(userCursoRepository.save(any(UserCurso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.criarUC("user@email.com", 1L);

        assertEquals(CursoStatus.NAO_INICIADO, resultado.getStatus());
        verify(userCursoRepository).save(any(UserCurso.class));
    }


    @Test
    void naoDevePermitirCursoInativo() {

        User user = new User();
        user.setEmail("user@email.com");

        Curso curso = new Curso();
        curso.setAtivo(false);

        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(Optional.of(user));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        assertThrows(RegraNegocioException.class,
                () -> service.criarUC("user@email.com", 1L));
    }


    @Test
    void naoDevePermitirMatriculaDuplicada() {

        User user = new User();
        Curso curso = new Curso();
        curso.setAtivo(true);

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(cursoRepository.findById(any()))
                .thenReturn(Optional.of(curso));

        when(userCursoRepository.findByUserAndCurso(user, curso))
                .thenReturn(Optional.of(new UserCurso()));

        assertThrows(RegraNegocioException.class,
                () -> service.criarUC("email", 1L));
    }


    @Test
    void userNaoPodeListarOutroUsuario() {

        User user = new User();
        user.setId(1L);
        user.setEmail("outro@email.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Authentication auth = mockAuth("logado@email.com", "ROLE_USER");

        assertThrows(RegraNegocioException.class,
                () -> service.listarMatriculasPorUsuario(1L, auth));
    }


    @Test
    void adminPodeListarQualquerUsuario() {

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userCursoRepository.findByUser(user))
                .thenReturn(List.of());

        Authentication auth = mockAuth("admin@email.com", "ROLE_ADMIN");

        var resultado = service.listarMatriculasPorUsuario(1L, auth);

        assertNotNull(resultado);
    }


    @Test
    void naoDeveDeletarFinalizado() {

        UserCurso uc = new UserCurso();
        uc.setStatus(CursoStatus.FINALIZADO);

        User user = new User();
        user.setEmail("user@email.com");
        uc.setUser(user);

        when(userCursoRepository.findById(1L))
                .thenReturn(Optional.of(uc));

        Authentication auth = mockAuth("user@email.com", "ROLE_USER");

        assertThrows(RegraNegocioException.class,
                () -> service.deletarUC(1L, auth));
    }

}
