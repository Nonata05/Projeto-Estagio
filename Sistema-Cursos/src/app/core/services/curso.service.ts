// CRUD de Cursos
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CursoRequestDTO, CursoRespostaDTO } from '../models/app.models'; // Ajuste o caminho se necessário

@Injectable({
  providedIn: 'root'
})
export class CursoService {
  private apiUrl = 'http://localhost:8080/cursos'; // Endpoint do CursoController
  private matriculaUrl = 'http://localhost:8080/matriculas'; // Endpoint para matrículas

  constructor(private http: HttpClient) {}

  // ==========================================
  // ROTAS PÚBLICAS OU DISPONÍVEIS PARA ALUNO & ADMIN
  // ==========================================

  // GET /cursos - Listar todos os cursos
  listarTodos(): Observable<CursoRespostaDTO[]> {
    return this.http.get<CursoRespostaDTO[]>(this.apiUrl);
  }

  // GET /cursos/{id} - Detalhes de um curso específico
  buscarPorId(id: number): Observable<CursoRespostaDTO> {
    return this.http.get<CursoRespostaDTO>(`${this.apiUrl}/${id}`);
  }

  // ==========================================
  //  ROTAS EXCLUSIVAS DE ADMIN (CRUD)
  // ==========================================

  // POST /cursos - Criar um novo curso
  criarCurso(curso: CursoRequestDTO): Observable<CursoRespostaDTO> {
    return this.http.post<CursoRespostaDTO>(this.apiUrl, curso);
  }

  // PUT /cursos/{id} - Atualizar dados do curso
  atualizarCurso(id: number, curso: CursoRequestDTO): Observable<CursoRespostaDTO> {
    return this.http.put<CursoRespostaDTO>(`${this.apiUrl}/${id}`, curso);
  }

  // DELETE /cursos/{id} - Excluir curso
  excluirCurso(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // PATCH /cursos/{id}/status - Ativar/Desativar curso (Controle do Admin)
  alternarStatusCurso(id: number, ativo: boolean): Observable<CursoRespostaDTO> {
    return this.http.patch<CursoRespostaDTO>(`${this.apiUrl}/${id}/status`, null, {
      params: { ativo: ativo.toString() }
    });
  }

  // ==========================================
  //  NOVAS ROTAS: FLUXO DO ALUNO (MATRÍCULAS)
  // ==========================================

  // POST /matriculas/inscrever/{cursoId} - Matricular o aluno logado no curso
  matricularNoCurso(cursoId: number): Observable<any> {
    return this.http.post(`${this.matriculaUrl}/inscrever/${cursoId}`, {});
  }

  // GET /matriculas/meus-cursos - Listar apenas os cursos que o aluno logado está matriculado
  listarMeusCursos(): Observable<CursoRespostaDTO[]> {
    return this.http.get<CursoRespostaDTO[]>(`${this.matriculaUrl}/meus-cursos`);
  }
}
