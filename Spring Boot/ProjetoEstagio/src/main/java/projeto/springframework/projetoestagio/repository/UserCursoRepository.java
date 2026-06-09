package projeto.springframework.projetoestagio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.entity.UserCurso;

import java.util.List;
import java.util.Optional;

public interface UserCursoRepository extends JpaRepository<UserCurso, Long> {

    Optional<UserCurso> findByUserAndCurso(User user, Curso curso);
    List<UserCurso> findByUser(User user);
}
