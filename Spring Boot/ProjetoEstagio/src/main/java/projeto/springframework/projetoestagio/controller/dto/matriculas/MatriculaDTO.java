package projeto.springframework.projetoestagio.controller.dto.matriculas;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatriculaDTO {

    @NotNull(message = "CursoId é obrigatório")
    @Positive(message = "CursoId deve ser maior que zero")
    private Long cursoId;

}
