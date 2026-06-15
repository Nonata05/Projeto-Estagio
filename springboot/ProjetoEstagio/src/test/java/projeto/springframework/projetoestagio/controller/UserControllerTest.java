package projeto.springframework.projetoestagio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import projeto.springframework.projetoestagio.controller.dto.user.UserPerfilDTO;
import projeto.springframework.projetoestagio.security.config.SecurityConfig;
import projeto.springframework.projetoestagio.service.CursoService;
import projeto.springframework.projetoestagio.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    //Cadastro é publico
    @Test
    void devePermitirCriacaoUsuarioSemAutenticacao() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "nome": "Teste",
                  "email": "teste@email.com",
                  "senha": "123456"
                }
            """))
                .andExpect(status().isCreated());
    }

    //Testar rota ADMIN sem login
    @Test
    void deveRetornar401AoListarUsuariosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    //Testar rota ADMIN com usuário comum
    @Test
    void deveRetornar403QuandoUsuarioNaoForAdmin() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    //Testar rota ADMIN com ADMIN
    @Test
    void devePermitirListarUsuariosQuandoForAdmin() throws Exception {

        when(userService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/usuarios")
                        .with(user("admin@email.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    //Testar /usuarios/me autenticado
    @Test
    void deveBuscarUsuarioLogado() throws Exception {

        when(userService.buscarPorEmail("user@email.com"))
                .thenReturn(new UserPerfilDTO(1L, "Teste", "user@email.com"));

        mockMvc.perform(get("/usuarios/me")
                        .with(user("user@email.com").roles("USER")))
                .andExpect(status().isOk());
    }

}
