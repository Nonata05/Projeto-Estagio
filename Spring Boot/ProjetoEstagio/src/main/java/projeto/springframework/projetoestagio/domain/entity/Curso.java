package projeto.springframework.projetoestagio.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Categoria;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;


import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "cursos")
@Getter
@Setter
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @Column(nullable = false)
    private String linkCurso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;


    private boolean ativo = true;

    private LocalDate dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CursoStatus status;

    //ligação entre curso e usecurso
    @OneToMany(mappedBy = "curso",cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserCurso> matriculas;

    @PrePersist
    public void prePersist(){
        if(this.dataCriacao == null){
            this.dataCriacao=LocalDate.now();
        }
        if(this.status == null){
            this.status = CursoStatus.NAO_INICIADO;
        }

        if(this.categoria == null){
            this.categoria = Categoria.PROGRAMACAO;
        }

    }

}
