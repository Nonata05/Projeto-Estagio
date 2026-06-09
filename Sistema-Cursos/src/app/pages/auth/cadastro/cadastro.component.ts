import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service'; 

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css']
})
export class CadastroComponent implements OnInit {
  cadastroForm: FormGroup;
  errorMessage: string = '';
  isDarkMode: boolean = true; 
  isLoading: boolean = false; 

  showSenha = false;
  showConfirmarSenha = false;

  // Injetando o seu AuthService aqui
  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private authService: AuthService 
  ) {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(4)]],
      confirmarSenha: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit() {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('senha')?.value === g.get('confirmarSenha')?.value
      ? null : { 'mismatch': true };
  }

  onSubmit() {
    if (this.cadastroForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

     
      const { nome, email, senha } = this.cadastroForm.value;
      const payload = { nome, email, senha };

      console.log('Enviando dados para o backend:', payload);

      // Chamamos o método cadastrar() do AuthService
      this.authService.cadastrar(payload).subscribe({
        next: (resposta) => {
          this.isLoading = false;
          console.log('Usuário cadastrado com sucesso!', resposta);
          alert('Conta criada com sucesso! Agora você já pode fazer login.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Erro ao realizar cadastro no Spring Boot:', err);

          // Trata o erro amigavelmente
          if (err.status === 409 || err.status === 400) {
            this.errorMessage = 'Este e-mail já está cadastrado ou os dados são inválidos.';
          } else {
            this.errorMessage = 'Erro ao conectar ao servidor. Certifique-se de que o backend está rodando.';
          }
        }
      });
    } else {
      this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
    }
  }
}
