package projeto.springframework.projetoestagio.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projeto.springframework.projetoestagio.controller.dto.user.*;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.enun.Papel;
import projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException;
import projeto.springframework.projetoestagio.exception.RegraNegocioException;
import projeto.springframework.projetoestagio.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    //Criar Usuario
    @Override
    public UserPerfilDTO criarUser(UserDTO userDTO){

        User user = new User();

        user.setNome(userDTO.getNome());

        String emailNormalizado = userDTO.getEmail().toLowerCase();

        if(userRepository.existsByEmail(emailNormalizado)){
            throw new RegraNegocioException("Email já cadastrado");
        }
        user.setEmail(emailNormalizado);

        user.setSenha(passwordEncoder.encode(userDTO.getSenha()));

        user.setPapel(Papel.ROLE_ALUNO);
        user.setDataCriacao(java.time.LocalDate.now());

        User salvo = userRepository.save(user);

        return converterUsuarioParaDTO(salvo);
    }

    //Buscar usuario por id
    @Override
    public Optional<UserAdminDTO> buscarUsuarioPorId(Long id){
        return userRepository.findById(id)
                .map(this::converterParaDTO);
    }

    //Buscar Usuario por email
    public UserPerfilDTO buscarPorEmail(String email) {
       User user = userRepository.findByEmail(email.toLowerCase())
               .orElseThrow(()-> new RecursoNaoEncontradoException("Usuario não encontrado"));

       return new UserPerfilDTO(
               user.getId(),
               user.getNome(),
               user.getEmail()

       );
    }


    //Listar usuarios
    @Override
    public List<UserAdminDTO> listarUsuarios(){
        return userRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    //ADMIN Atualizar usuario
    @Override
    public UserAdminDTO atualizarUser(Long id, UserAtualizacaoPapelDTO atualizacaoDTO){
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if(existente.getPapel() == Papel.ROLE_ADMIN &&
                atualizacaoDTO.getPapel() == Papel.ROLE_ALUNO){
            throw new RegraNegocioException("ADMIN não pode ser rebaixado");
        }
        existente.setPapel(atualizacaoDTO.getPapel());

       User salvo = userRepository.save(existente);
        return converterParaDTO(salvo);
    }

    //Atualiza usuario
    @Override
    @Transactional
    public void atualizarMeuNome(String email, String novoNome) {

        User usuario = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setNome(novoNome);
        userRepository.save(usuario);
    }

    //deletar usuario
    @Override
    public void deletar(Long id){
       if(!userRepository.existsById(id)){
            throw new RecursoNaoEncontradoException("Usuario não encontrado");

       }

       userRepository.deleteById(id);
    }

    //ADMIN Mudar a senha de usuario
    @Override
    public void mudarSenha(Long id, UserAtualizarSenhaDTO dto, Authentication auth){

        User userLogado = userRepository.findByEmail(auth.getName().toLowerCase())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado"));

        User userExistente = userRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Usuario não encontrado"));


        //Regra de segurança
        if(userLogado.getPapel() != Papel.ROLE_ADMIN && !userLogado.getId().equals(userExistente.getId())){

            throw new RegraNegocioException("Você não tem permissão para alterar a senha deste usuário.");
        }
        //  Se não for admin, validar senha atual
        if(userLogado.getPapel() != Papel.ROLE_ADMIN){

            if(!passwordEncoder.matches(dto.getSenhaAtual(), userExistente.getSenha())){
                throw new RegraNegocioException("Senha atual incorreta");
            }
        }

        //atualizar a senha
        userExistente.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        userRepository.save(userExistente);

    }

    //Atualizar senha
    public void mudarMinhaSenha(UserAtualizarSenhaDTO dto, Authentication auth){

        User userLogado = userRepository.findByEmail(auth.getName().toLowerCase())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário logado não encontrado"));

        // Valida senha atual
        if(!passwordEncoder.matches(dto.getSenhaAtual(), userLogado.getSenha())){
            throw new RegraNegocioException("Senha atual incorreta");
        }
        userLogado.setSenha(passwordEncoder.encode(dto.getNovaSenha()));

        userRepository.save(userLogado);
    }

    private UserAdminDTO converterParaDTO(User user){
        return new UserAdminDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPapel(),
                user.getDataCriacao()

        );
    }

    private UserPerfilDTO converterUsuarioParaDTO(User user){
        return new UserPerfilDTO(
                user.getId(),
                user.getNome(),
                user.getEmail()

        );
    }

    public Optional<User> buscarEntidadePorEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }
}
