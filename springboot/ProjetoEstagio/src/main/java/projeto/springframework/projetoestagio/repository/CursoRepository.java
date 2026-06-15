package projeto.springframework.projetoestagio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.springframework.projetoestagio.domain.entity.Curso;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByAtivoTrue();
}
