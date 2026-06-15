package projeto.springframework.projetoestagio.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Papel;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    private LocalDate dataCriacao;

    //ligação entre usuario e usecurso
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCurso> userCursos;

    @PrePersist
    public void prePersist(){
        if (this.dataCriacao == null){
            this.dataCriacao=LocalDate.now();
        }

    }
}
