package projeto.springframework.projetoestagio.controller.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projeto.springframework.projetoestagio.domain.enun.Papel;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {


    private Long id;
    private String nome;
    private String email;
    private Papel papel;
    private LocalDate dataCriacao;
}
