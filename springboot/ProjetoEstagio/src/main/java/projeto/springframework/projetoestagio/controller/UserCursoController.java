package projeto.springframework.projetoestagio.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaStatusDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaRespostaDTO;
import projeto.springframework.projetoestagio.service.UserCursoService;

import java.util.List;

@RestController
@RequestMapping("/matriculas")
public class UserCursoController {

    public final UserCursoService userCursoService;

    public UserCursoController(UserCursoService userCursoService){this.userCursoService = userCursoService;}

    //Listar todas as matriculas
    @GetMapping
    public ResponseEntity<List<MatriculaRespostaDTO>> listarMatricula(Authentication auth){
        System.out.println("Listar Todas as matriculas");

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));

        if(isAdmin){
            return ResponseEntity.ok(userCursoService.listarUC());
        }

        return ResponseEntity.ok(userCursoService.listarPorUsuario(auth.getName()));
    }

    //Listar matriculas por id
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaRespostaDTO> buscarMatriculaPorId(@PathVariable Long id){

        return userCursoService.buscarUCId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios/{id}/matriculas")
    public ResponseEntity<List<MatriculaRespostaDTO>>
    listarMatriculasPorUsuario(@PathVariable Long id,
                               Authentication auth){

        return ResponseEntity.ok(
                userCursoService.listarMatriculasPorUsuario(id, auth)
        );
    }


    //Criar uma matricula
    @PostMapping
    public ResponseEntity<MatriculaRespostaDTO> criarMatricula(@Valid @RequestBody MatriculaDTO matriculaDTO,
                                                               Authentication auth){
       MatriculaRespostaDTO resposta =
               userCursoService.criarUC(auth.getName(),
                       matriculaDTO.getCursoId());


        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    //Atualizar o status
    @PutMapping("/{id}")
    public ResponseEntity<MatriculaRespostaDTO> atualizarMatricula(@PathVariable Long id,
                                                                   @Valid @RequestBody MatriculaStatusDTO dto,
                                                                   Authentication auth){

        return ResponseEntity.ok(userCursoService.atualizarUC(id, dto, auth));
    }

    //deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id, Authentication auth) {

        userCursoService.deletarUC(id, auth);

        return ResponseEntity.noContent().build();
    }

}
