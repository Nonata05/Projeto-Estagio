import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private userService = inject(UserService);

  isDarkMode: boolean = true; 
  loading: boolean = true; 
  abaAtiva: 'info' | 'senha' = 'info'; 

  usuario: any = null; 

  // Formulário de Edição de Nome
  nomeForm!: FormGroup;
  isEditandoNome: boolean = false;
  nomeLoading: boolean = false;
  nomeSuccessMessage: string | null = null;
  nomeErrorMessage: string | null = null;

  // Formulário de Alteração de Senha
  senhaForm!: FormGroup;
  senhaLoading: boolean = false;
  senhaSuccessMessage: string | null = null;
  senhaErrorMessage: string | null = null;

  constructor() {
    this.criarFormularios();
  }

  ngOnInit(): void {
    const temaSalvo = localStorage.getItem('theme');
    this.isDarkMode = temaSalvo ? temaSalvo === 'dark' : true;

    this.carregarUsuario();
  }

  carregarUsuario(): void {
    this.loading = true;
    this.userService.obterUsuarioLogado().subscribe({
      next: (dados) => {
        this.usuario = dados;
        this.nomeForm.patchValue({ nome: dados.nome });
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dados do usuário:', err);
        this.loading = false;
        this.router.navigate(['/login']);
      }
    });
  }

  criarFormularios(): void {
    // Form de Nome
    this.nomeForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]]
    });

    // Form de Senha
    this.senhaForm = this.fb.group({
      senhaAtual: ['', [Validators.required]],
      novaSenha: ['', [Validators.required, Validators.minLength(4)]],
      confirmarSenha: ['', [Validators.required]]
    }, { validator: this.compararSenhas });
  }

  compararSenhas(group: FormGroup) {
    const nova = group.get('novaSenha')?.value;
    const confirmar = group.get('confirmarSenha')?.value;
    return nova === confirmar ? null : { mismatch: true };
  }

  // === AÇÃO 1: ATUALIZAR NOME (PATCH /usuarios/me) ===
  salvarNome() {
    if (this.nomeForm.invalid) return;

    this.nomeLoading = true; 
    this.nomeSuccessMessage = null;
    this.nomeErrorMessage = null;

    const novoNome = this.nomeForm.value.nome;

    // Passamos o 'novoNome' diretamente como string para corrigir o erro do TypeScript
    this.userService.atualizarNome(novoNome).subscribe({
      next: () => {
        this.nomeLoading = false;
        this.isEditandoNome = false; // Fecha o campo de edição automaticamente
        
        // Atualiza o nome no objeto local do usuário. 
        // Isso faz o Angular atualizar o menu, iniciais e a tela na hora!
        if (this.usuario) {
          this.usuario.nome = novoNome;
        }

        // Exibe a mensagem de sucesso na tela
        this.nomeSuccessMessage = 'Nome atualizado com sucesso!';

        // Reseta o estado de validação do formulário
        this.nomeForm.markAsPristine();
        this.nomeForm.markAsUntouched();

          setTimeout(() => {
          this.nomeSuccessMessage = null;
        }, 3000);
      },
      error: (err) => {
        this.nomeLoading = false;
        this.nomeErrorMessage = err.error?.message || 'Erro ao atualizar o nome. Tente novamente.';
        console.error(err);
      }
    });
  }
  

  // === ALTERAR SENHA (PATCH /usuarios/me/senha) ===
  alterarSenha(): void {
    if (this.senhaForm.invalid) return;

    this.senhaLoading = true;
    this.senhaSuccessMessage = null;
    this.senhaErrorMessage = null;

    const dadosSenha = {
      senhaAtual: this.senhaForm.value.senhaAtual,
      novaSenha: this.senhaForm.value.novaSenha,
      confirmaSenha: this.senhaForm.value.confirmarSenha 
    };

    this.userService.alterarSenha(dadosSenha).subscribe({
      next: () => {
        this.senhaLoading = false;
        this.senhaSuccessMessage = 'Senha atualizada com sucesso!';
        this.senhaForm.reset();
        setTimeout(() => {
          this.voltarParaInfo();
        }, 2000);
      },
      error: (err) => {
        this.senhaLoading = false;
        this.senhaErrorMessage = err.error?.message || 'Senha atual incorreta ou erro no servidor.';
        console.error(err);
      }
    });
  }

  // Métodos Auxiliares
  isAdmin(): boolean {
    if (!this.usuario) return false;
    const papel = this.usuario.papel || this.usuario.role;
    return papel === 'ROLE_ADMIN' || papel === 'ADMIN';
  }

  getIniciais(): string {
    const nome = this.usuario?.nome;
    if (!nome || nome.trim() === '') {
      const email = this.usuario?.email || '';
      return email ? email.charAt(0).toUpperCase() : '?';
    }
    
    const nomes = nome.trim().split(/\s+/);
    if (nomes.length > 1) {
      return (nomes[0].charAt(0) + nomes[nomes.length - 1].charAt(0)).toUpperCase();
    }
    return nomes[0].charAt(0).toUpperCase();
  }

  getNomeExibicao(): string {
    if (this.usuario?.nome && this.usuario.nome.trim() !== '') {
      return this.usuario.nome;
    }
    return this.usuario?.email ? this.usuario.email.split('@')[0] : 'Usuário';
  }

  toggleTema(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  voltarParaDashboard(): void {
    if (this.isAdmin()) {
      this.router.navigate(['/admin/cursos']); 
    } else {
      this.router.navigate(['/aluno/meus-cursos']);
    }
  }

  irParaMudarSenha(): void {
    this.senhaForm.reset();
    this.senhaSuccessMessage = null;
    this.senhaErrorMessage = null;
    this.abaAtiva = 'senha';
  }

  voltarParaInfo(): void {
    this.abaAtiva = 'info';
  }

  ativarEdicaoNome(): void {
    this.isEditandoNome = true;
    this.nomeForm.patchValue({ nome: this.usuario.nome });
  }

  cancelarEdicaoNome(): void {
    this.isEditandoNome = false;
    this.nomeErrorMessage = null;
  }
}
