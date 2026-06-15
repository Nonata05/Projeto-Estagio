package projeto.springframework.projetoestagio.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoAtualizacaoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaDTO;
import projeto.springframework.projetoestagio.domain.enun.Categoria;


import projeto.springframework.projetoestagio.service.CursoService;

import java.util.List;


@RestController
@RequestMapping("/cursos")
public class CursoController {


    private final CursoService cursoService;

    public CursoController(CursoService cursoService){ this.cursoService = cursoService; }

    //Listar Curso
    @GetMapping
    public List<?> listarCurso(){

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                        .stream()
                                .anyMatch(a->a.getAuthority()
                                        .equals("ROLE_ADMIN"));

        System.out.println("Chamou uma lista de cursos");

        if(isAdmin){
            return cursoService.listarCursos();
        }

        return cursoService.listarCursosAtivos();
    }
    //Listar Curso por id

    @GetMapping("/{id}")
    public ResponseEntity<?> listarCursoPorId(@PathVariable Long id){
        System.out.println("Listar curso por id");

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(cursoService.buscarCursoPorId(id, isAdmin));
    }

    //Criar curso
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CursoRespostaDTO> criarCurso(@Valid @RequestBody CursoDTO cursoDTO){
        CursoRespostaDTO novoCurso = cursoService.criarCurso(cursoDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoCurso);
    }

    //Atualizar curso
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CursoRespostaDTO> atualizarCurso(@PathVariable Long id,
                                               @Valid @RequestBody CursoAtualizacaoDTO cursoAtualizar){

        return ResponseEntity.ok(cursoService.atualizar(id, cursoAtualizar));
    }



    //deletar curso
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCurso(@PathVariable Long id){
        System.out.println("Deletando curso id: " + id);
      cursoService.desativar(id);
      return ResponseEntity.noContent().build();

    }


}
