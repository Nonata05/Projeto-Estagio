package projeto.springframework.projetoestagio.service;

import org.springframework.security.core.Authentication;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaRespostaDTO;
import projeto.springframework.projetoestagio.controller.dto.matriculas.MatriculaStatusDTO;
import projeto.springframework.projetoestagio.domain.entity.UserCurso;
import projeto.springframework.projetoestagio.domain.enun.CursoStatus;

import java.util.List;
import java.util.Optional;

public interface UserCursoService {


    //Criar usuario
    MatriculaRespostaDTO criarUC(String email, Long cursoId);

    List<MatriculaRespostaDTO> listarUC();

    List<MatriculaRespostaDTO> listarPorUsuario(String email);

    List<MatriculaRespostaDTO> listarMatriculasPorUsuario(Long userId,
                                                          Authentication auth);

    Optional<MatriculaRespostaDTO> buscarUCId(Long id);

    MatriculaRespostaDTO atualizarUC(Long id, MatriculaStatusDTO dto, Authentication auth);

    void deletarUC(Long id, Authentication auth);
}
