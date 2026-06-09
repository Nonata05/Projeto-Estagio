package projeto.springframework.projetoestagio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoAtualizacaoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaUserDTO;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;
import projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException;
import projeto.springframework.projetoestagio.repository.CursoRepository;
import projeto.springframework.projetoestagio.domain.enun.Categoria;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CursoServiceImp implements CursoService{
    private final CursoRepository cursoRepository;

    //Criar curso
    @Override
    public CursoRespostaDTO criarCurso(CursoDTO cursoDTO){
       Curso curso = new Curso();

       curso.setTitulo(cursoDTO.getTitulo());
       curso.setDescricao(cursoDTO.getDescricao());
       curso.setLinkCurso(cursoDTO.getLinkCurso());
       curso.setCategoria(cursoDTO.getCategoria());
       curso.setAtivo(true);

       //Forcando status
       curso.setStatus(CursoStatus.NAO_INICIADO);

       Curso salvo = cursoRepository.save(curso);
       return converterParaDTO(salvo);
    }

    //Buscar Curso por id

    @Override
    public Object buscarCursoPorId(Long id, boolean isAdmin){
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Curso não encontrado"));

        if(!isAdmin && !curso.isAtivo()){
            throw new RecursoNaoEncontradoException("Curso não encontrado");
        }
        if(isAdmin){
            return converterParaDTO(curso);
        }
        return Optional.of(converterParaUserDTO(curso));
    }

    //Listar cursos admin
    @Override
    public List<CursoRespostaDTO> listarCursos(){
        return cursoRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    //Listar cursos alunos
    @Override
    public List<CursoRespostaUserDTO> listarCursosAtivos(){
        return cursoRepository.findByAtivoTrue()
                .stream()
                .map(this::converterParaUserDTO)
                .toList();
    }

    //atualizar curso
    @Override
    public CursoRespostaDTO atualizar(Long id, CursoAtualizacaoDTO cursoAtualizado) {

        Curso existente= cursoRepository.findById(id)
                .orElseThrow(()->new RecursoNaoEncontradoException("Curso não encontrado"));

        existente.setTitulo(cursoAtualizado.getTitulo());
        existente.setDescricao(cursoAtualizado.getDescricao());
        existente.setCategoria(cursoAtualizado.getCategoria());
        existente.setAtivo(cursoAtualizado.getAtivo());
        existente.setLinkCurso(cursoAtualizado.getLinkCurso());



        Curso salvo = cursoRepository.save(existente);
        return converterParaDTO(salvo) ;
    }



    //desativar curso
    @Override
    public void desativar(Long id){
        Curso curso= cursoRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Curso não encontrado"));
        curso.setAtivo(false);
        cursoRepository.save(curso);
    }

    private CursoRespostaDTO converterParaDTO(Curso curso){
        return new CursoRespostaDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescricao(),
                curso.getLinkCurso(),
                curso.isAtivo(),
                curso.getCategoria(),
                curso.getDataCriacao()

        );
    }

    private CursoRespostaUserDTO converterParaUserDTO(Curso curso) {
        return new CursoRespostaUserDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescricao(),
                curso.getCategoria(),
                curso.getLinkCurso()
        );
    }
}
