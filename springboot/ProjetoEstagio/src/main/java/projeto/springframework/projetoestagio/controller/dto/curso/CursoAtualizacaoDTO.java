package projeto.springframework.projetoestagio.controller.dto.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Categoria;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoAtualizacaoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "categoria é obrigatória")
    private Categoria categoria;

    @NotBlank(message = "Link do curso é obrigatorio")
    private String linkCurso;

    // ADICIONE ESTE CAMPO:
    @NotNull(message = "O status de atividade é obrigatório")
    private Boolean ativo;


}
