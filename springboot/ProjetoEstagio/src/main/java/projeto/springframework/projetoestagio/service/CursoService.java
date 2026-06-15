package projeto.springframework.projetoestagio.service;

import projeto.springframework.projetoestagio.controller.dto.curso.CursoAtualizacaoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaUserDTO;


import java.util.List;
import java.util.Optional;

public interface CursoService {

    CursoRespostaDTO criarCurso(CursoDTO cursoDTO);

    //Buscar curso por id
    Object buscarCursoPorId(Long id, boolean isAdmin);

    //Listar curso
    List<CursoRespostaDTO> listarCursos();

    //Listar cursos ativos
   List<CursoRespostaUserDTO> listarCursosAtivos();

    //Atualizar
    CursoRespostaDTO atualizar(Long id, CursoAtualizacaoDTO cursoAtualizado);


    //Desativar curso
    void desativar(Long id);
}
