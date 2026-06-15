package projeto.springframework.projetoestagio.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoAtualizacaoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoDTO;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;
import projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException;
import projeto.springframework.projetoestagio.repository.CursoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CursoServiceImpTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoServiceImp cursoService;


    @Test
    void deveCriarCursoComSucesso() {

        CursoDTO dto = new CursoDTO();
        dto.setTitulo("Java");
        dto.setDescricao("Curso Java");
        dto.setLinkCurso("link");

        Curso salvo = new Curso();
        salvo.setId(1L);
        salvo.setTitulo("Java");
        salvo.setDescricao("Curso Java");
        salvo.setLinkCurso("link");
        salvo.setAtivo(true);
        salvo.setStatus(CursoStatus.NAO_INICIADO);

        when(cursoRepository.save(Mockito.any(Curso.class)))
                .thenReturn(salvo);

        var resultado = cursoService.criarCurso(dto);

        assertNotNull(resultado);
        assertEquals("Java", resultado.getTitulo());
        verify(cursoRepository).save(Mockito.any(Curso.class));
    }


    @Test
    void adminDeveBuscarCursoPorId() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Java");
        curso.setAtivo(false);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        var resultado = cursoService.buscarCursoPorId(1L, true);

        assertTrue(resultado instanceof projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaDTO);
    }


    @Test
    void alunoDeveBuscarCursoAtivo() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Java");
        curso.setAtivo(true);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        var resultado = cursoService.buscarCursoPorId(1L, false);

        assertTrue(resultado instanceof Optional);
    }


    @Test
    void alunoNaoDeveBuscarCursoInativo() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setAtivo(false);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        assertThrows(RecursoNaoEncontradoException.class,
                () -> cursoService.buscarCursoPorId(1L, false));
    }


    @Test
    void deveLancarExcecaoQuandoCursoNaoExiste() {

        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> cursoService.buscarCursoPorId(1L, true));
    }


    @Test
    void deveListarCursos() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Java");

        when(cursoRepository.findAll()).thenReturn(List.of(curso));

        var lista = cursoService.listarCursos();

        assertEquals(1, lista.size());
        assertEquals("Java", lista.get(0).getTitulo());
    }


    @Test
    void deveListarCursosAtivos() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Java");
        curso.setAtivo(true);

        when(cursoRepository.findByAtivoTrue()).thenReturn(List.of(curso));

        var lista = cursoService.listarCursosAtivos();

        assertEquals(1, lista.size());
    }


    @Test
    void deveAtualizarCurso() {

        Curso curso = new Curso();
        curso.setId(1L);

        CursoAtualizacaoDTO dto = new CursoAtualizacaoDTO();
        dto.setTitulo("Novo");
        dto.setDescricao("Desc");
        dto.setLinkCurso("Link");

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(curso)).thenReturn(curso);

        var resultado = cursoService.atualizar(1L, dto);

        assertEquals("Novo", resultado.getTitulo());
        verify(cursoRepository).save(curso);
    }


    @Test
    void deveLancarExcecaoAoAtualizarCursoInexistente() {

        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        CursoAtualizacaoDTO dto = new CursoAtualizacaoDTO();

        assertThrows(RecursoNaoEncontradoException.class,
                () -> cursoService.atualizar(1L, dto));
    }


    @Test
    void deveDesativarCurso() {

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setAtivo(true);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        cursoService.desativar(1L);

        assertFalse(curso.isAtivo());
        verify(cursoRepository).save(curso);
    }








}
