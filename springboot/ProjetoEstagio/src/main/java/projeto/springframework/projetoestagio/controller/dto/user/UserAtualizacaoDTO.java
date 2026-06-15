package projeto.springframework.projetoestagio.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAtualizacaoDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
}
