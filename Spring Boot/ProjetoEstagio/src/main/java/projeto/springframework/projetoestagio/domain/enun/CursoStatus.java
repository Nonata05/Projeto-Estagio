package projeto.springframework.projetoestagio.domain.enun;
import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@Getter


public enum CursoStatus {
    NAO_INICIADO("Não Iniciado"),
    EM_ANDAMENTO ("Em andamento"),
    FINALIZADO ("Finalizado");

    private final String descricao;

    CursoStatus(String descricao){
        this.descricao = descricao;
    }

    public boolean podeTransicionarPara(CursoStatus novoStatus){

        if(this == FINALIZADO){
            return false;
        }

        if(this == EM_ANDAMENTO && novoStatus == NAO_INICIADO){
            return false;
        }

        if(this == NAO_INICIADO && novoStatus == FINALIZADO){
            return false;
        }
        return true;
    }

}
