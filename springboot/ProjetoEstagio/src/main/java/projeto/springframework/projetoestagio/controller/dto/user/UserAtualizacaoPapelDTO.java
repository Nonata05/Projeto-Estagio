package projeto.springframework.projetoestagio.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import projeto.springframework.projetoestagio.domain.enun.Papel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAtualizacaoPapelDTO {


    @NotNull(message = "Papel é obrigatório")
    private Papel papel;

}
