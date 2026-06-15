package projeto.springframework.projetoestagio.domain.enun;


import lombok.Getter;


@Getter
public enum Papel {
    ROLE_ADMIN("Administrador"),
    ROLE_ALUNO("Aluno");

    private final String descricao;

    Papel(String descricao){
        this.descricao = descricao;
    }

}
