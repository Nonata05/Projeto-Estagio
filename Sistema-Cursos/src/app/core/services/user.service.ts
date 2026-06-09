import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  
  // URL da sua API Java (ajuste se necessário)
  private readonly API_URL = 'http://localhost:8080'; 

  // Subject que guarda o usuário em cache na memória do Angular
  private usuarioSubject = new BehaviorSubject<any>(null);
  public usuario$ = this.usuarioSubject.asObservable();

  constructor() {
    const usuarioLocal = localStorage.getItem('usuario');
    if (usuarioLocal) {
      try {
        this.usuarioSubject.next(JSON.parse(usuarioLocal));
      } catch (e) {
        this.limparCache();
      }
    }
  }

  get usuarioLogado(): any {
    return this.usuarioSubject.value;
  }

  estaLogado(): boolean {
    return !!this.usuarioSubject.value || !!localStorage.getItem('token');
  }

  temPapel(papelRequerido: string): boolean {
    const usuario = this.usuarioLogado;
    if (!usuario) return false;
    
    const papelUsuario = usuario.papel || usuario.role;
    return papelUsuario === papelRequerido;
  }

  logout(): void {
    this.limparCache();
  }

  /**
   * Busca dados do usuário. Evita requisições repetidas se já estiver em memória.
   */
  obterUsuarioLogado(): Observable<any> {
    if (this.usuarioSubject.value) {
      return of(this.usuarioSubject.value);
    }

    return this.http.get<any>(`${this.API_URL}/usuarios/me`).pipe(
      tap(usuario => {
        this.atualizarUsuarioNaMemoria(usuario);
      }),
      catchError(err => {
        this.limparCache();
        throw err;
      })
    );
  }

  // ==========================================
  //     INTEGRAÇÃO COM O BACKEND (NOVO)
  // ==========================================

  /**
   * Atualiza o nome do usuário logado
   * @path PATCH /usuarios/me
   */
  atualizarNome(novoNome: string): Observable<void> {
    const payload = { nome: novoNome };
    return this.http.patch<void>(`${this.API_URL}/usuarios/me`, payload).pipe(
      tap(() => {
        // Se deu certo no back, atualiza o nome localmente em tempo real
        const usuarioAtual = this.usuarioSubject.value;
        if (usuarioAtual) {
          const usuarioAtualizado = { ...usuarioAtual, nome: novoNome };
          this.atualizarUsuarioNaMemoria(usuarioAtualizado);
        }
      })
    );
  }

  /**
   * Altera a senha do usuário logado
   * @path PATCH /usuarios/me/senha
   */
  alterarSenha(dadosSenha: { senhaAtual: string; novaSenha: string; confirmaSenha: string }): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/usuarios/me/senha`, dadosSenha);
  }


  // ==========================================
  //      MÉTODOS ADMINISTRATIVOS (PAINEL)
  // ==========================================

  /**
   * Lista todos os usuários cadastrados (Requer perfil ADMIN)
   * @path GET /usuarios
   */
  listarUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/usuarios`);
  }

  /**
   * Busca um usuário pelo ID específico (Requer perfil ADMIN)
   * @path GET /usuarios/{id}
   */
  buscarUsuarioPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/usuarios/${id}`);
  }

  /**
   * Atualiza o papel/perfil de um usuário (Requer perfil ADMIN)
   * @path PUT /usuarios/{id}
   */
  atualizarPapel(id: number, atualizacaoDTO: { papel: string }): Observable<any> {
    return this.http.put<any>(`${this.API_URL}/usuarios/${id}`, atualizacaoDTO);
  }

  /**
   * Deleta permanentemente um usuário (Requer perfil ADMIN)
   * @path DELETE /usuarios/{id}
   */
  deletarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/usuarios/${id}`);
  }


  // ==========================================
  //        MÉTODOS DE CONTROLE DE CACHE
  // ==========================================

  atualizarUsuarioNaMemoria(usuario: any): void {
    this.usuarioSubject.next(usuario);
    localStorage.setItem('usuario', JSON.stringify(usuario));
  }

  limparCache(): void {
    this.usuarioSubject.next(null);
    localStorage.removeItem('usuario');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}
