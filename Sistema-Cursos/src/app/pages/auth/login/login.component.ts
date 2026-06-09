import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; 
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service'; 
import { AuthService } from '../../../core/services/auth.service'; 

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit { 
  private fb = inject(FormBuilder);
  private authService = inject(AuthService); 
  private userService = inject(UserService); 
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  loginForm: FormGroup;
  errorMessage: string = '';
  loading: boolean = false;
  
  showSenha = false;
  isDarkMode: boolean = false; 

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(4)]] 
    });
  }

  ngOnInit(): void {
    const temaSalvo = localStorage.getItem('theme') || localStorage.getItem('darkMode');
    const temClasseDark = document.body.classList.contains('dark-theme') || 
                          document.body.classList.contains('dark') ||
                          document.documentElement.classList.contains('dark');

    this.isDarkMode = !!(temaSalvo === 'dark' || temaSalvo === 'true' || temClasseDark);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { email, senha } = this.loginForm.value;

    this.authService.login({ email, senha }).subscribe({
      next: (response: any) => {
        
        // ===================================================================
        // PASSO DE SEGURANÇA: Limpa qualquer resquício de sessão anterior
        // (Isso zera o usuarioSubject na memória e remove tokens antigos)
        // ===================================================================
        this.userService.limparCache();

        let token = null;
        let role = null;

        // --- EXTRATOR INTELIGENTE DE TOKEN ---
        if (typeof response === 'string') {
          try {
            const parsed = JSON.parse(response);
            token = parsed.token;
            role = parsed.role;
          } catch (e) {
            token = response;
          }
        } else if (response && typeof response === 'object') {
          token = response.token;
          role = response.role;
        }

        // --- SALVA AS INFORMAÇÕES FRESCAS NO STORAGE ---
        if (token) {
          localStorage.setItem('token', token);
          console.log('Novo Token salvo com sucesso no LocalStorage!');
        } else {
          console.error('Aviso: Token não pôde ser extraído da resposta.');
        }

        localStorage.setItem('user', email);

        // --- OBTÉM OS DADOS DO NOVO USUÁRIO LOGADO (Força ida ao Backend) ---
        this.userService.obterUsuarioLogado().subscribe({
          next: (usuario) => {
            this.loading = false;
            
            const papelLogado = usuario?.papel || role;
            console.log('Papel identificado para redirecionamento:', papelLogado);

            // Força a detecção de mudança antes de navegar
            this.cdr.detectChanges();

            if (papelLogado === 'ROLE_ADMIN') {
              console.log('Encaminhando Admin para /admin/cursos');
              this.router.navigate(['/admin/cursos']); 
            } else if (papelLogado === 'ROLE_USER' || papelLogado === 'ROLE_ALUNO') {
              console.log('Encaminhando Aluno para /aluno/meus-cursos');
              this.router.navigate(['/aluno/meus-cursos']);
            } else {
              console.warn('Papel não identificado ou inválido. Redirecionando para a Home:', papelLogado);
              this.router.navigate(['/']);
            }
          },
          error: (err) => {
            this.loading = false;
            this.errorMessage = 'Erro ao carregar os dados de perfil do Spring Boot.';
            console.error('Erro na requisição /usuarios/me:', err);
            
            // Força atualização da tela para mostrar o erro imediatamente
            this.cdr.detectChanges(); 
          }
        });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'E-mail ou senha incorretos.';
        console.error('Erro ao fazer login:', err);

        // === FORÇA O ANGULAR A ATUALIZAR A TELA IMEDIATAMENTE ===
        this.cdr.detectChanges(); 
      }
    });
  }
}
