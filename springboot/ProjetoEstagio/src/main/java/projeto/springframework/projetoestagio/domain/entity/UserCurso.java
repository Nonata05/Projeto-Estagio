package projeto.springframework.projetoestagio.domain.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;

import java.time.LocalDate;

@Entity
@Table(name = "user_curso")
@Getter
@Setter
public class UserCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CursoStatus status;

    private LocalDate dataInicio;
    private LocalDate dataFinal;

}
