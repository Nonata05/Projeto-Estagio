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

public class CursoDTO {

    @NotBlank(message = "Titulo é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "categoria é obrigatória")
    private Categoria categoria;

    @NotBlank(message = "Link do curso é obrigatorio")
    private String linkCurso;

    private Boolean ativo = true;


    public CursoDTO(String titulo, String descricao, Categoria categoria, String linkCurso) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.linkCurso = linkCurso;
        this.ativo = true;
    }

}
