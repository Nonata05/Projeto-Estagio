package projeto.springframework.projetoestagio.service;

import org.springframework.security.core.Authentication;
import projeto.springframework.projetoestagio.controller.dto.user.*;
import projeto.springframework.projetoestagio.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    //Criar usuario
    UserPerfilDTO criarUser(UserDTO userDTO);

    //Buscar Usuario por id
    Optional<UserAdminDTO> buscarUsuarioPorId(Long id);

    //Listar Usuarios
    List<UserAdminDTO> listarUsuarios();

    UserAdminDTO atualizarUser(Long id, UserAtualizacaoPapelDTO atualizacaoDTO);

    void atualizarMeuNome(String email, String novoNome);

    //Deletar Usuario
    void deletar(Long id);

    UserPerfilDTO buscarPorEmail(String email);

    //mudar senha
    void mudarSenha( Long id, UserAtualizarSenhaDTO dto, Authentication auth);

    void mudarMinhaSenha(UserAtualizarSenhaDTO dto, Authentication auth);

    public Optional<User> buscarEntidadePorEmail(String email);

}
