import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // Adicionado ChangeDetectorRef
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service'; 
import { MenuLateralComponent } from '../../../componentes/menu-lateral/menu-lateral.component'; 

export interface UserAdminDTO {
  id: number;
  nome: string | null;
  email: string;
  papel: string;
  dataCriacao?: string;
}

@Component({
  selector: 'app-gerenciar-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuLateralComponent],
  templateUrl: './gerenciar-usuarios.component.html',
  styleUrls: ['./gerenciar-usuarios.component.css'],
  providers: [DatePipe]
})
export class GerenciarUsuariosComponent implements OnInit {
  usuarios: UserAdminDTO[] = [];
  usuariosFiltrados: UserAdminDTO[] = [];
  
  buscaTexto: string = '';
  perfilSelecionado: string = 'TODOS';

  mostrarToast: boolean = false;
  mensagemToast: string = '';

  
  constructor(
    private userService: UserService, 
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.userService.listarUsuarios().subscribe({
      next: (dados: any) => {
        console.log('Dados recebidos:', dados);

        if (!dados) {
          this.usuarios = [];
        } else if (Array.isArray(dados)) {
          this.usuarios = dados;
        } else if (dados.content && Array.isArray(dados.content)) {
          this.usuarios = dados.content;
        } else if (dados.data && Array.isArray(dados.data)) {
          this.usuarios = dados.data;
        } else {
          this.usuarios = [dados];
        }

        // Clona e filtra
        this.usuariosFiltrados = [...this.usuarios];
        this.filtrarUsuarios();

        // === SOLUÇÃO DO PROBLEMA ===
        // Força o Angular a renderizar os dados na tela imediatamente
        this.cdr.detectChanges(); 
      },
      error: (err: any) => {
        console.error('Erro ao buscar usuários:', err);
      }
    });
  }

  alterarPapel(id: number, novoPapelSemPrefixo: 'ADMIN' | 'ALUNO'): void {
    const papelCompleto = `ROLE_${novoPapelSemPrefixo}`;
    const atualizacaoDTO = { papel: papelCompleto };

    this.userService.atualizarPapel(id, atualizacaoDTO).subscribe({
      next: () => {
        const usuario = this.usuarios.find(u => u.id === id);
        if (usuario) {
          usuario.papel = papelCompleto;
        }
        this.filtrarUsuarios();
        this.exibirToast('Perfil do usuário atualizado com sucesso!');
        this.cdr.detectChanges(); // Força atualização visual
      },
      error: (err: any) => {
        console.error('Erro ao atualizar papel:', err);
        alert('Erro ao salvar alteração de perfil no banco.');
      }
    });
  }

  deletarUsuario(id: number): void {
    if (confirm('Tem certeza de que deseja excluir este usuário do banco de dados definitivamente?')) {
      this.userService.deletarUsuario(id).subscribe({
        next: () => {
          this.usuarios = this.usuarios.filter(u => u.id !== id);
          this.filtrarUsuarios();
          this.exibirToast('Usuário excluído com sucesso!');
          this.cdr.detectChanges(); // Força atualização visual
        },
        error: (err: any) => {
          console.error('Erro ao deletar usuário:', err);
          alert('Não foi possível excluir o usuário.');
        }
      });
    }
  }

  exibirToast(mensagem: string): void {
    this.mensagemToast = mensagem;
    this.mostrarToast = true;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.mostrarToast = false;
      this.cdr.detectChanges();
    }, 4000);
  }

  filtrarUsuarios(): void {
    if (!this.usuarios || this.usuarios.length === 0) {
      this.usuariosFiltrados = [];
      return;
    }

    this.usuariosFiltrados = this.usuarios.filter(usuario => {
      const nomeUsuario = (usuario.nome || '').toLowerCase();
      const emailUsuario = (usuario.email || '').toLowerCase();
      const busca = this.buscaTexto.toLowerCase().trim();

      const bateTexto = busca === '' || nomeUsuario.includes(busca) || emailUsuario.includes(busca);
      
      const papelUsuario = (usuario.papel || '').toUpperCase();
      let batePerfil = false;

      if (this.perfilSelecionado === 'TODOS') {
        batePerfil = true;
      } else if (this.perfilSelecionado === 'ADMIN') {
        batePerfil = papelUsuario.includes('ADMIN');
      } else if (this.perfilSelecionado === 'ALUNO') {
        batePerfil = papelUsuario.includes('ALUNO') || papelUsuario.includes('USER');
      }

      return bateTexto && batePerfil;
    });

    // Garante que a filtragem por digitação também renderize instantaneamente
    this.cdr.detectChanges(); 
  }

  isUsuarioAdmin(usuario: UserAdminDTO): boolean {
    const papel = (usuario.papel || '').toUpperCase();
    return papel.includes('ADMIN');
  }

  obterIniciais(nome: string | null, email: string): string {
    if (!nome || nome.trim() === '') {
      return email ? email[0].toUpperCase() : 'U';
    }
    const partes = nome.trim().split(' ');
    if (partes.length > 1) {
      return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
    }
    return partes[0][0].toUpperCase();
  }

  getAvatarClass(nome: string | null): string {
    const base = nome || 'U';
    const charCode = base.charCodeAt(0) || 0;
    const classes = ['bg-purple', 'bg-blue', 'bg-green', 'bg-orange'];
    return classes[charCode % classes.length];
  }
}
