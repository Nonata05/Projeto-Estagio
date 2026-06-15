package projeto.springframework.projetoestagio.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    private Curso criarCursoAtivo() {
        Curso curso = new Curso();
        curso.setTitulo("Spring Boot");
        curso.setDescricao("Curso completo");
        curso.setLinkCurso("http://curso.com");
        curso.setAtivo(true);
        curso.setStatus(CursoStatus.NAO_INICIADO);
        return curso;
    }

    private Curso criarCursoInativo() {
        Curso curso = new Curso();
        curso.setTitulo("Java Avançado");
        curso.setDescricao("Curso avançado");
        curso.setLinkCurso("http://java.com");
        curso.setAtivo(false);
        curso.setStatus(CursoStatus.NAO_INICIADO);
        return curso;
    }

    @Test
    @DisplayName("Deve salvar curso com sucesso")
    void deveSalvarCurso() {

        Curso curso = criarCursoAtivo();

        Curso cursoSalvo = cursoRepository.save(curso);

        assertThat(cursoSalvo.getId()).isNotNull();
        assertThat(cursoSalvo.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve aplicar valores padrão no @PrePersist")
    void deveAplicarValoresPadraoNoPrePersist() {

        Curso curso = new Curso();
        curso.setTitulo("Docker");
        curso.setLinkCurso("http://docker.com");

        Curso salvo = cursoRepository.save(curso);

        assertThat(salvo.getDataCriacao()).isNotNull();
        assertThat(salvo.getStatus()).isEqualTo(CursoStatus.NAO_INICIADO);
        assertThat(salvo.isAtivo()).isTrue(); // valor padrão do atributo
    }

    @Test
    @DisplayName("Deve retornar apenas cursos ativos")
    void deveRetornarApenasCursosAtivos() {

        Curso ativo = criarCursoAtivo();
        Curso inativo = criarCursoInativo();

        cursoRepository.save(ativo);
        cursoRepository.save(inativo);

        List<Curso> cursosAtivos = cursoRepository.findByAtivoTrue();

        assertThat(cursosAtivos).hasSize(1);
        assertThat(cursosAtivos.get(0).isAtivo()).isTrue();
        assertThat(cursosAtivos.get(0).getTitulo()).isEqualTo("Spring Boot");
    }
    @Test
    @DisplayName("Não deve retornar cursos quando nenhum estiver ativo")
    void naoDeveRetornarCursosQuandoNenhumAtivo() {

        Curso inativo = criarCursoInativo();
        cursoRepository.save(inativo);

        List<Curso> cursosAtivos = cursoRepository.findByAtivoTrue();

        assertThat(cursosAtivos).isEmpty();
    }

}
