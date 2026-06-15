package projeto.springframework.projetoestagio.controller.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPerfilDTO {
    private Long id;
    private String nome;
    private String email;

}
