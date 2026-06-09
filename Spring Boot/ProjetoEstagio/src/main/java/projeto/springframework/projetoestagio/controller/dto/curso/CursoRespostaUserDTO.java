package projeto.springframework.projetoestagio.controller.dto.curso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Categoria;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoRespostaUserDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Categoria categoria;
    private String LinkCurso;
}
