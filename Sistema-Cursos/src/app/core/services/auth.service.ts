import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private javaLoginUrl = 'http://localhost:8080/auth'; 

  constructor(private http: HttpClient) {}

  // NOVO MÉTODO: Envia os dados para salvar no banco de dados
  cadastrar(dadosCadastro: any): Observable<any> {
    // Envia para http://localhost:8080/auth/register
    // Se no seu Spring Boot o endpoint for "/cadastro", mude para: `${this.javaLoginUrl}/cadastro`
    return this.http.post<any>(`${this.javaLoginUrl}/register`, dadosCadastro);
  }

  login(dadosLogin: any): Observable<any> {
    return this.http.post<any>(`${this.javaLoginUrl}/login`, dadosLogin).pipe(
      tap(response => {
        // LOGs de diagnóstico
        if (response && response.token) {
          localStorage.setItem('token', response.token); // Salva o token no navegador
        
          if (response.email) {
            localStorage.setItem('user', response.email);
          }
          // Mapeia o email e papel vindos do banco de dados (Spring Boot)
          if (response.papel) {
            let papelFormatado = response.papel.toUpperCase();
            if (!papelFormatado.startsWith('ROLE_')) {
              papelFormatado = `ROLE_${papelFormatado}`; // Transforma 'ADMIN' em 'ROLE_ADMIN'
            }
            localStorage.setItem('role', papelFormatado);
          }
        }
      })
    );
  }

  // Pega o token salvo
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Verifica se o usuário está logado
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Pega o papel formatado do LocalStorage
  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  // Deslogar
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
  }
}
