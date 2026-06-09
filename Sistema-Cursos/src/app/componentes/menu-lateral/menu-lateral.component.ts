import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-menu-lateral',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './menu-lateral.component.html',
  styleUrls: ['./menu-lateral.component.css']
})
export class MenuLateralComponent {
  private router = inject(Router);
  public userService = inject(UserService);

  @Input() isCollapsed: boolean = false;

  // Busca dinamicamente os dados do usuário do backend
  get usuarioLogado() {
    return this.userService.usuarioLogado;
  }

  // Ambos (Admin e Aluno) vão para a mesma rota de dashboard compartilhada!
  get rotaDashboard(): string {
    return '/dashboard'; 
  }

  // Verifica se o usuário atual é Administrador do sistema
  get isAdmin(): boolean {
    const papel = this.usuarioLogado?.papel as any;
    return papel === 'ROLE_ADMIN' || papel === 'ADMIN';
  }

  // Verifica se o usuário atual é um Aluno
  get isAluno(): boolean {
    const papel = this.usuarioLogado?.papel as any;
    return papel === 'ROLE_ALUNO' || papel === 'ROLE_USER';
  }

  // Pega a primeira letra do nome de forma ultra segura
  get primeiraLetraNome(): string {
    const nome = this.usuarioLogado?.nome;
    return nome ? nome.charAt(0).toUpperCase() : 'U';
  }

  // Realiza o logout direto, limpando a sessão e redirecionando para a Landing Page (Home)
  logout() {
     const desejaSair = confirm('Deseja realmente sair do sistema?');
    
    if (desejaSair) {
      this.userService.logout();
      this.router.navigate(['/']); // Redireciona diretamente para a Home
    }
  }
}
