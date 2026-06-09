package projeto.springframework.projetoestagio.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.entity.UserCurso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;
import projeto.springframework.projetoestagio.domain.enun.Papel;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UseCursoRepositoryTest {

    @Autowired
    private UserCursoRepository userCursoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CursoRepository cursoRepository;

    private User criarUser() {
        User user = new User();
        user.setNome("João");
        user.setEmail("joao@email.com");
        user.setSenha("123456");
        user.setPapel(Papel.ROLE_ALUNO);
        return userRepository.save(user);
    }

    private Curso criarCurso() {
        Curso curso = new Curso();
        curso.setTitulo("Spring Boot");
        curso.setLinkCurso("http://curso.com");
        curso.setStatus(CursoStatus.NAO_INICIADO);
        return cursoRepository.save(curso);
    }

    private UserCurso criarMatricula(User user, Curso curso) {
        UserCurso userCurso = new UserCurso();
        userCurso.setUser(user);
        userCurso.setCurso(curso);
        userCurso.setStatus(CursoStatus.NAO_INICIADO);
        return userCursoRepository.save(userCurso);
    }

    @Test
    @DisplayName("Deve salvar matrícula com sucesso")
    void deveSalvarMatricula() {

        User user = criarUser();
        Curso curso = criarCurso();

        UserCurso matricula = criarMatricula(user, curso);

        assertThat(matricula.getId()).isNotNull();
        assertThat(matricula.getUser()).isEqualTo(user);
        assertThat(matricula.getCurso()).isEqualTo(curso);
    }

    @Test
    @DisplayName("Deve buscar matrícula por user e curso")
    void deveBuscarPorUserECurso() {

        User user = criarUser();
        Curso curso = criarCurso();
        criarMatricula(user, curso);

        Optional<UserCurso> resultado =
                userCursoRepository.findByUserAndCurso(user, curso);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUser()).isEqualTo(user);
        assertThat(resultado.get().getCurso()).isEqualTo(curso);
    }

    @Test
    @DisplayName("Deve retornar vazio quando matrícula não existir")
    void deveRetornarVazioQuandoNaoExistir() {

        User user = criarUser();
        Curso curso = criarCurso();

        Optional<UserCurso> resultado =
                userCursoRepository.findByUserAndCurso(user, curso);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar todas as matrículas de um usuário")
    void deveBuscarPorUser() {

        User user = criarUser();
        Curso curso1 = criarCurso();
        Curso curso2 = criarCurso();

        criarMatricula(user, curso1);
        criarMatricula(user, curso2);

        List<UserCurso> matriculas =
                userCursoRepository.findByUser(user);

        assertThat(matriculas).hasSize(2);
    }



}
