package projeto.springframework.projetoestagio.controller.dto.curso;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Categoria;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoRespostaDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String LinkCurso;
    private boolean ativo = true;
    private Categoria categoria;
    private LocalDate dataCriacao;
}
