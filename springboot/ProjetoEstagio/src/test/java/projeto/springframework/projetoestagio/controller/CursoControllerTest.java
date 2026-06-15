package projeto.springframework.projetoestagio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaDTO;
import projeto.springframework.projetoestagio.controller.dto.curso.CursoRespostaUserDTO;
import projeto.springframework.projetoestagio.security.config.SecurityConfig;
import projeto.springframework.projetoestagio.service.CursoService;
import projeto.springframework.projetoestagio.domain.enun.Categoria;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static projeto.springframework.projetoestagio.domain.enun.Categoria.PROGRAMACAO;

@WebMvcTest(CursoController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursoService;

    @Autowired
    private ObjectMapper objectMapper;

    // ADMIN deve ver todos os cursos
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveListarTodosCursos() throws Exception {

        List<CursoRespostaDTO> cursos = List.of(
                new CursoRespostaDTO(
                        1L,
                        "Java",
                        "Curso completo",
                        "http://link.com",
                        true,
                        PROGRAMACAO,
                        LocalDate.now()
                )
        );

        when(cursoService.listarCursos())
                .thenReturn(cursos);

        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Java"));
    }

    // USER deve ver apenas ativos
    @Test
    @WithMockUser(roles = "USER")
    void userDeveListarCursosAtivos() throws Exception {

        List<CursoRespostaUserDTO> cursos = List.of(
                new CursoRespostaUserDTO(
                        1L,
                        "Java",
                        "Curso completo",
                        PROGRAMACAO,
                        "http://link.com"
                )
        );

        when(cursoService.listarCursosAtivos())
                .thenReturn(cursos);

        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Java"));
    }

    // ADMIN pode criar curso
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveCriarCurso() throws Exception {

        CursoDTO dto = new CursoDTO("Java", "Curso completo",PROGRAMACAO,"Link");
        CursoRespostaDTO resposta =
                new CursoRespostaDTO(
                        1L,
                        "Java",
                        "Curso completo",
                        "http://link.com",
                        true,
                        PROGRAMACAO,
                        LocalDate.now()
                );

        when(cursoService.criarCurso(any(CursoDTO.class)))
                .thenReturn(resposta);

        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Java"));
    }

    // USER não pode criar curso
    @Test
    @WithMockUser(roles = "USER")
    void userNaoPodeCriarCurso() throws Exception {

        CursoDTO dto = new CursoDTO(
                "Java",
                "Curso completo",
                PROGRAMACAO,
                "link");

        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ADMIN pode deletar
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveDeletarCurso() throws Exception {

        doNothing().when(cursoService).desativar(1L);

        mockMvc.perform(delete("/cursos/1"))
                .andExpect(status().isNoContent());
    }
}
