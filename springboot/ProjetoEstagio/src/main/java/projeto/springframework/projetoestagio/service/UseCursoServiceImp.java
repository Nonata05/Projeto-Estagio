package projeto.springframework.projetoestagio.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaStatusDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaRespostaDTO;
import projeto.springframework.projetoestagio.domain.entity.Curso;
import projeto.springframework.projetoestagio.domain.entity.User;
import projeto.springframework.projetoestagio.domain.entity.UserCurso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;
import projeto.springframework.projetoestagio.exception.RecursoNaoEncontradoException;
import projeto.springframework.projetoestagio.exception.RegraNegocioException;
import projeto.springframework.projetoestagio.repository.CursoRepository;
import projeto.springframework.projetoestagio.repository.UserCursoRepository;
import projeto.springframework.projetoestagio.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UseCursoServiceImp implements UserCursoService {

    private final UserCursoRepository userCursoRepository;

    private final UserRepository userRepository;

    private final CursoRepository cursoRepository;

    //Criar userCurso
    @Override
    public MatriculaRespostaDTO criarUC(String email, Long cursoId){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));

        if(!curso.isAtivo()){
            throw new RegraNegocioException("Não é possivel se matricular em um curso inativo");
        }
        boolean existe = userCursoRepository
                .findByUserAndCurso(user, curso)
                .isPresent();

        if (existe) {
            throw new RegraNegocioException("Usuário já matriculado nesse curso");
        }

        UserCurso uc = new UserCurso();
        uc.setUser(user);
        uc.setCurso(curso);
        uc.setStatus(CursoStatus.NAO_INICIADO);
        uc.setDataInicio(null);

        UserCurso salvo = userCursoRepository.save(uc);

        return converterParaDTO(salvo);
    }

    //Listar Matriculas
    @Override
    public List<MatriculaRespostaDTO> listarUC(){
        return userCursoRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    //Listar por usuario

    public List<MatriculaRespostaDTO> listarPorUsuario(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return userCursoRepository.findByUser(user)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }
    //Listar matricula por id
    @Override
    public Optional<MatriculaRespostaDTO> buscarUCId(Long id){
        return userCursoRepository.findById(id)
                .map(this::converterParaDTO);

    }

    @Override
    public List<MatriculaRespostaDTO> listarMatriculasPorUsuario(
            Long userId,
            Authentication auth) {

        User usuarioSolicitado = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String emailLogado = auth.getName();

        // Se não for admin, só pode acessar o próprio ID
        if (!isAdmin && !usuarioSolicitado.getEmail().equals(emailLogado)) {
            throw new RegraNegocioException(
                    "Você não tem permissão para visualizar essas matrículas");
        }

        return userCursoRepository.findByUser(usuarioSolicitado)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }



    private void validarTransicaoStatus(UserCurso uc, CursoStatus novoStatus){
        CursoStatus atual = uc.getStatus();
        //  Não pode auterar se tiver finalizado
        if (!atual.podeTransicionarPara(novoStatus)) {
            throw new RegraNegocioException( "Transição inválida de " + atual + " para " + novoStatus);
        }

    }

    //Aplicar status

    private void aplicarAtualizacaoStatus(UserCurso uc, CursoStatus novoStatus){



        // Se está mudando para EM_ANDAMENTO
        if (novoStatus == CursoStatus.EM_ANDAMENTO && uc.getDataInicio() == null) {
            uc.setDataInicio(LocalDate.now());
        }

        // Se está mudando para FINALIZADO
        if (novoStatus == CursoStatus.FINALIZADO) {
            uc.setDataFinal(LocalDate.now());
        }

        uc.setStatus(novoStatus);
    }

    //Atualizar matricula
    @Override
    public MatriculaRespostaDTO atualizarUC(Long id,
                                            MatriculaStatusDTO dto,
                                            Authentication auth){

        UserCurso uc = userCursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula não encontrada"));


        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !uc.getUser().getEmail().equals(auth.getName())) {
            throw new RegraNegocioException("Você não pode alterar matrícula de outro usuário");
        }

        validarTransicaoStatus(uc, dto.getStatus());

        aplicarAtualizacaoStatus(uc, dto.getStatus());

        return converterParaDTO(uc);
    }

    @Override
    public void deletarUC(Long id, Authentication auth){
        UserCurso uc = userCursoRepository.findById(id)
        .orElseThrow(()->  new RecursoNaoEncontradoException("Matricula não encontrada"));

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String emailLogado = auth.getName();

        // Se não for admin, só pode deletar a própria matrícula
        if (!isAdmin && !uc.getUser().getEmail().equals(emailLogado)) {
            throw new RegraNegocioException(
                    "Você não pode deletar matrícula de outro usuário");
        }

        if (uc.getStatus() == CursoStatus.FINALIZADO) {
            throw new RegraNegocioException(
                    "Não é possível desmatricular um curso já finalizado");
        }



        userCursoRepository.delete(uc);
    }


    private MatriculaRespostaDTO converterParaDTO(UserCurso uc){
        return new MatriculaRespostaDTO(
                uc.getId(),
                uc.getUser().getNome(),
                uc.getCurso().getId(),
                uc.getCurso().getTitulo(),
                uc.getStatus(),
                uc.getDataInicio(),
                uc.getDataFinal()
        );
    }

}
