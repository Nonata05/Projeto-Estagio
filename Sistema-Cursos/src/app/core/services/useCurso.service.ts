//Matrículas e progresso
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatriculaRequestDTO, MatriculaRespostaDTO, MatriculaStatusDTO } from '../models/app.models';

@Injectable({
  providedIn: 'root'
})
export class UseCursoService {
  // Ajustado para o endpoint do seu Spring Boot: /useCurso
  private apiUrl = 'http://localhost:8080/useCurso'; 

  constructor(private http: HttpClient) {}

  // ==========================================
  // ROTAS DO ALUNO (Sessão do Usuário Logado)
  // ==========================================

  // Inscrever o aluno em um curso
  matricularNoCurso(dto: MatriculaRequestDTO): Observable<MatriculaRespostaDTO> {
    return this.http.post<MatriculaRespostaDTO>(this.apiUrl, dto);
  }

  // Listar os cursos do próprio aluno logado
  listarMinhasMatriculas(): Observable<MatriculaRespostaDTO[]> {
    return this.http.get<MatriculaRespostaDTO[]>(`${this.apiUrl}/meus-cursos`);
  }

  // Atualizar o progresso/status (Ex: "CONCLUIDO")
  atualizarStatusProgresso(id: number, dto: MatriculaStatusDTO): Observable<MatriculaRespostaDTO> {
    return this.http.put<MatriculaRespostaDTO>(`${this.apiUrl}/${id}/status`, dto);
  }

  // Aluno cancela a inscrição no curso
  cancelarMinhaMatricula(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ==========================================
  // ROTAS EXCLUSIVAS DE ADMIN
  // ==========================================

  // Admin lista todas as inscrições/matrículas do sistema
  listarTodasMatriculas(): Observable<MatriculaRespostaDTO[]> {
    return this.http.get<MatriculaRespostaDTO[]>(this.apiUrl);
  }

  // Admin visualiza as inscrições de um aluno específico
  listarMatriculasDoUsuario(usuarioId: number): Observable<MatriculaRespostaDTO[]> {
    return this.http.get<MatriculaRespostaDTO[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }
}
