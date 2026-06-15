package projeto.springframework.projetoestagio.controller.dto.matriculas;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatriculaStatusDTO {
    @NotNull(message = "Status é obrigatório")
    private CursoStatus status;
}
