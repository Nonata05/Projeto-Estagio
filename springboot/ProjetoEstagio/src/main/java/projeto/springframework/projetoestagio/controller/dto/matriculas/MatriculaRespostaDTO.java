package projeto.springframework.projetoestagio.controller.dto.matriculas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatriculaRespostaDTO {
    private Long id;
    private String nomeUsuario;
    private Long cursoId;
    private String nomeCurso;
    private CursoStatus status;
    private LocalDate dataInicio;
    private  LocalDate dataFinal;
}
