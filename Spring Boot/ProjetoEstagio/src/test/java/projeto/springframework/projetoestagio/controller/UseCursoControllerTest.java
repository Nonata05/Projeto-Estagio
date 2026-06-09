package projeto.springframework.projetoestagio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaRespostaDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaStatusDTO;
import projeto.springframework.projetoestagio.security.config.SecurityConfig;
import projeto.springframework.projetoestagio.service.UserCursoService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static projeto.springframework.projetoestagio.domain.enun.CursoStatus.EM_ANDAMENTO;


@WebMvcTest(UserCursoController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UseCursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCursoService userCursoService;

    @Autowired
    private ObjectMapper objectMapper;

    //Listar matrículas como ADMIN
    @Test
    void deveListarTodasMatriculasQuandoAdmin() throws Exception {

        when(userCursoService.listarUC()).thenReturn(List.of());

        mockMvc.perform(get("/matriculas")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
    // Listar matrículas como USER
    @Test
    void deveListarMatriculasDoUsuarioQuandoNaoAdmin() throws Exception {

        when(userCursoService.listarPorUsuario("user@email.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/matriculas")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isOk());
    }

    //Buscar matrícula por ID existente
    @Test
    void deveBuscarMatriculaPorIdQuandoExistir() throws Exception {

        MatriculaRespostaDTO dto = new MatriculaRespostaDTO();
        dto.setId(1L);

        when(userCursoService.buscarUCId(1L))
                .thenReturn(java.util.Optional.of(dto));

        mockMvc.perform(get("/matriculas/1")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    //Buscar matrícula por ID não existente
    @Test
    void deveRetornarNotFoundQuandoMatriculaNaoExistir() throws Exception {

        when(userCursoService.buscarUCId(99L))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/matriculas/99")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    //Listar matrículas por usuário
    @Test
    void deveListarMatriculasPorUsuario() throws Exception {

        when(userCursoService.listarMatriculasPorUsuario(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/matriculas/usuarios/1/matriculas")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    //Criar matricula
    @Test
    void deveCriarMatricula() throws Exception {

        MatriculaDTO dto = new MatriculaDTO();
        dto.setCursoId(1L);

        MatriculaRespostaDTO resposta = new MatriculaRespostaDTO();
        resposta.setId(10L);

        when(userCursoService.criarUC("user@email.com", 1L))
                .thenReturn(resposta);

        mockMvc.perform(post("/matriculas")
                        .with(user("user@email.com").roles("USER"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    // Atualizar status
    @Test
    void deveAtualizarMatricula() throws Exception {

        MatriculaStatusDTO dto = new MatriculaStatusDTO();
        dto.setStatus(EM_ANDAMENTO);

        MatriculaRespostaDTO resposta = new MatriculaRespostaDTO();
        resposta.setId(1L);

        when(userCursoService.atualizarUC(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(resposta);

        mockMvc.perform(put("/matriculas/1")
                        .with(user("admin@email.com").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    //Deletar matricula
    @Test
    void deveDeletarMatricula() throws Exception {

        mockMvc.perform(delete("/matriculas/1")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

}
