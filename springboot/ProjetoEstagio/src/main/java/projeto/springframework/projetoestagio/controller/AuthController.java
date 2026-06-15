package projeto.springframework.projetoestagio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projeto.springframework.projetoestagio.controller.dto.user.AuthResponse;
import projeto.springframework.projetoestagio.controller.dto.user.LoginDTO;
import projeto.springframework.projetoestagio.controller.dto.user.UserDTO;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.enun.Papel;
import projeto.springframework.projetoestagio.service.UserService;
import projeto.springframework.projetoestagio.security.jwt.JwtService;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody UserDTO dto) {
        System.out.println("=== TENTATIVA DE CADASTRO ===");
        System.out.println("Nome: " + dto.getNome());
        System.out.println("Email: " + dto.getEmail());

        try {

            var novoUsuario = userService.criarUser(dto);

            System.out.println("Usuário cadastrado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);

        } catch (projeto.springframework.projetoestagio.exception.RegraNegocioException e) {
            // Captura o erro caso o e-mail já esteja cadastrado
            System.out.println("ERRO ao cadastrar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> validarLogin(@RequestBody LoginDTO dto) {
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Email recebido do Angular: " + dto.getEmail());
        System.out.println("Senha recebida do Angular: " + dto.getSenha());

        var userOpt = userService.buscarEntidadePorEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            System.out.println("ERRO: Usuário não foi encontrado para o e-mail: " + dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userOpt.get();
        System.out.println(" Usuário encontrado no banco!");
        System.out.println("Senha criptografada no banco (Hash): " + user.getSenha());

        boolean senhaBate = passwordEncoder.matches(dto.getSenha(), user.getSenha());
        System.out.println("A senha bate com o Hash? " + (senhaBate ? "SIM " : "NÃO "));

        if (!senhaBate) {
            System.out.println("ERRO: Senha incorreta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.gerarToken(user);
        System.out.println(" Login realizado com sucesso! Token gerado.");

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        user.getEmail(),
                        user.getPapel().name()
                )
        );
    }

}
