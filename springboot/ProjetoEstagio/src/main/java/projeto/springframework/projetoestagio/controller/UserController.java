package projeto.springframework.projetoestagio.controller;


import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projeto.springframework.projetoestagio.controller.dto.user.*;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException;
import projeto.springframework.projetoestagio.security.jwt.JwtService;
import projeto.springframework.projetoestagio.service.UserService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@Slf4j
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS
})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //Listar Usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserAdminDTO> listarUsuarios() {
        log.info("Listando usuários");
        return userService.listarUsuarios();
    }

    //Buscar usuario com id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminDTO> buscarUsuariosPorId(@PathVariable Long id) {
        log.info("Chamou usuario por id");

        return userService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Buscar usuario Logado
    @GetMapping("/me")
    public ResponseEntity<?> buscarUsuarioLogado(@AuthenticationPrincipal String email) {
        // Se o filtro não autenticou, o e-mail será null
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Busca o usuário usando o e-mail (que veio diretamente do Token)
        User user = userService.buscarEntidadePorEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "nome", user.getNome(),
                "email", user.getEmail(),
                "papel", user.getPapel().name() // Retornará "ROLE_ADMIN" ou "ROLE_USER"
        ));
    }




    //Criar usuario
        @PostMapping
        public ResponseEntity<UserPerfilDTO> criarUsuario (@Valid @RequestBody UserDTO userDTO){
            UserPerfilDTO novoUsuario = userService.criarUser(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        }

        //Atualizar papel do usuario
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<UserAdminDTO> atualizarUsuario (@PathVariable Long id,
                @Valid @RequestBody UserAtualizacaoPapelDTO atualizacaoDTO){

            return ResponseEntity.ok(userService.atualizarUser(id, atualizacaoDTO));
        }

        //Atualizar Usuario
        @PatchMapping("/me")
        public ResponseEntity<Void> atualizarMeuNome (
                @Valid @RequestBody UserAtualizacaoDTO use,
                Authentication authentication){

            userService.atualizarMeuNome(authentication.getName(), use.getNome());

            return ResponseEntity.noContent().build();
        }

        //deletar usuario
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletarUsuario (@PathVariable Long id){
            userService.deletar(id);

            return ResponseEntity.noContent().build();
        }

        //ADMIN atualizar senhados usuarios
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id:\\d+}/senha")
        public ResponseEntity<Void> mudarSenha (@PathVariable Long id, @Valid @RequestBody UserAtualizarSenhaDTO dto,
                Authentication auth){

            userService.mudarSenha(id, dto, auth);

            return ResponseEntity.noContent().build();
        }

        //Usuario atualiza sua propria senha
        @PatchMapping("/me/senha")
        public ResponseEntity<Void> mudarMinhaSenha (
                @Valid @RequestBody UserAtualizarSenhaDTO dto,
                Authentication auth){

            userService.mudarMinhaSenha(dto, auth);

            return ResponseEntity.noContent().build();
        }


}
