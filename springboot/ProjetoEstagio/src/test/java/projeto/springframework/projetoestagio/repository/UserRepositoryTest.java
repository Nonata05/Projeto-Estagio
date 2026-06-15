package projeto.springframework.projetoestagio.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.enun.Papel;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User criarUsuario() {
        User user = new User();
        user.setNome("João");
        user.setEmail("joao@email.com");
        user.setSenha("123456");
        user.setPapel(Papel. ROLE_ALUNO); // ajuste conforme seu enum
        return user;
    }

    @Test
    @DisplayName("Deve salvar um usuário com sucesso")
    void deveSalvarUsuario() {

        User user = criarUsuario();

        User userSalvo = userRepository.save(user);

        assertThat(userSalvo.getId()).isNotNull();
        assertThat(userSalvo.getDataCriacao()).isNotNull(); // valida @PrePersist
    }

    @Test
    @DisplayName("Deve retornar true quando email existir")
    void deveRetornarTrueQuandoEmailExistir() {

        User user = criarUsuario();
        userRepository.save(user);

        boolean existe = userRepository.existsByEmail("joao@email.com");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando email não existir")
    void deveRetornarFalseQuandoEmailNaoExistir() {

        boolean existe = userRepository.existsByEmail("naoexiste@email.com");

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void deveEncontrarUsuarioPorEmail() {

        User user = criarUsuario();
        userRepository.save(user);

        Optional<User> resultado = userRepository.findByEmail("joao@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João");
    }

    @Test
    @DisplayName("Não deve encontrar usuário com email inexistente")
    void naoDeveEncontrarUsuarioComEmailInexistente() {

        Optional<User> resultado = userRepository.findByEmail("inexistente@email.com");

        assertThat(resultado).isEmpty();
    }

}
