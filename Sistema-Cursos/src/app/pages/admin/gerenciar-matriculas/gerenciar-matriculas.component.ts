import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; 
import { MenuLateralComponent } from '../../../componentes/menu-lateral/menu-lateral.component';

// Interface ajustada para bater exatamente com o seu HTML e receber dados convertidos do Java
export interface MatriculaRespostaDTO {
  id: number;
  usuarioNome: string;
  usuarioEmail: string;
  cursoNome: string;
  dataMatricula: string;
  status: 'NAO_INICIADO' | 'EM_ANDAMENTO' | 'FINALIZADO';
}

// Interface estruturada para exibição agrupada por Usuário
export interface UsuarioGrupo {
  nome: string;
  email: string;
  totalCursos: number;
  qtdNaoIniciado: number;
  qtdEmAndamento: number;
  qtdFinalizado: number;
  matriculas: MatriculaRespostaDTO[];
  isExpandido?: boolean;
}

@Component({
  selector: 'app-gerenciar-matriculas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MenuLateralComponent],
  templateUrl: './gerenciar-matriculas.component.html',
  styleUrl: './gerenciar-matriculas.component.css'
})
export class GerenciarMatriculasComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private http = inject(HttpClient); 

  private apiUrl = 'http://localhost:8080/matriculas'; 

  // Armazenará a lista de matrículas vinda do banco de dados
  matriculasCruas: any[] = [];

  usuariosAgrupados: UsuarioGrupo[] = [];
  usuariosFiltrados: UsuarioGrupo[] = [];

  // Métricas Globais
  totalAlunos: number = 0;
  totalMatriculas: number = 0;

  searchTerm: string = '';
  isDarkMode: boolean = true;
  isSidebarCollapsed: boolean = false;

  ngOnInit(): void {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
    
    this.carregarDados();
  }

  // Busca as matrículas diretamente do seu banco via GET /matriculas
  carregarDados(): void {
    console.log('[Matrículas] Carregando matrículas do banco...');
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (dados) => {
        console.log('[Matrículas] Dados brutos recebidos do Java:', dados);
        this.matriculasCruas = dados || [];
        this.processarEAgruparUsuarios();
      },
      error: (err) => {
        console.error('[Matrículas] Erro ao carregar matrículas do backend:', err);
      }
    });
  }

  // Agrupa as matrículas por aluno e faz o de-para dos nomes de variáveis (Java -> HTML)
  processarEAgruparUsuarios(): void {
    const mapa = new Map<string, UsuarioGrupo>();

    this.matriculasCruas.forEach(m => {
      // Como o DTO do Java não traz e-mail, geramos um fictício com base no nome para o design continuar perfeito
      const nomeLimpo = m.nomeUsuario ? m.nomeUsuario.trim() : 'Aluno';
      const emailChave = m.nomeUsuario 
        ? m.nomeUsuario.toLowerCase().trim().replace(/\s+/g, '.') + '@email.com' 
        : 'aluno@email.com';

      if (!mapa.has(emailChave)) {
        mapa.set(emailChave, {
          nome: nomeLimpo,
          email: emailChave,
          totalCursos: 0,
          qtdNaoIniciado: 0,
          qtdEmAndamento: 0,
          qtdFinalizado: 0,
          matriculas: [],
          isExpandido: false
        });
      }

      const userGroup = mapa.get(emailChave)!;

      // Traduz os campos do Java (nomeCurso, dataInicio) para os nomes que seu HTML espera (cursoNome, dataMatricula)
      const matriculaMapeada: MatriculaRespostaDTO = {
        id: m.id,
        usuarioNome: nomeLimpo,
        usuarioEmail: emailChave,
        cursoNome: m.nomeCurso || 'Curso sem Nome',
        dataMatricula: m.dataInicio || new Date().toISOString(),
        status: m.status
      };

      userGroup.matriculas.push(matriculaMapeada);
      userGroup.totalCursos++;

      if (m.status === 'NAO_INICIADO') userGroup.qtdNaoIniciado++;
      else if (m.status === 'EM_ANDAMENTO') userGroup.qtdEmAndamento++;
      else if (m.status === 'FINALIZADO') userGroup.qtdFinalizado++;
    });

    this.usuariosAgrupados = Array.from(mapa.values());
    this.totalAlunos = this.usuariosAgrupados.length;
    this.totalMatriculas = this.matriculasCruas.length;
    
    this.filtrarUsuarios();
  }

  filtrarUsuarios(): void {
    const termo = this.searchTerm.toLowerCase().trim();
    if (!termo) {
      this.usuariosFiltrados = [...this.usuariosAgrupados];
    } else {
      this.usuariosFiltrados = this.usuariosAgrupados.filter(u => 
        u.nome.toLowerCase().includes(termo) || 
        u.email.toLowerCase().includes(termo) ||
        u.matriculas.some(m => m.cursoNome.toLowerCase().includes(termo))
      );
    }
    this.cdr.detectChanges(); // Força a tela a renderizar os dados e as mudanças imediatamente
  }

  toggleExpandir(usuario: UsuarioGrupo): void {
    usuario.isExpandido = !usuario.isExpandido;
  }

  // Executa PUT /matriculas/{id} atualizando o status no banco
  alterarStatus(matriculaId: number, novoStatus: 'NAO_INICIADO' | 'EM_ANDAMENTO' | 'FINALIZADO'): void {
    console.log(`[Matrículas] Atualizando matrícula ID ${matriculaId} para status: ${novoStatus}`);
    
    const body = { status: novoStatus };

    this.http.put(`${this.apiUrl}/${matriculaId}`, body).subscribe({
      next: (resposta) => {
        console.log('[Matrículas] Status atualizado com sucesso no backend!', resposta);
        this.carregarDados(); // Recarrega do banco para atualizar os cards de contagem e a tela
      },
      error: (err) => {
        console.error('[Matrículas] Erro ao atualizar status no backend:', err);
        alert('Não foi possível atualizar o status da matrícula.');
      }
    });
  }

  // Executa DELETE /matriculas/{id} excluindo a matrícula do aluno no banco
  removerCursoDoAluno(matriculaId: number): void {
    if (confirm('Deseja realmente remover esta matrícula do aluno?')) {
      console.log(`[Matrículas] Deletando matrícula ID ${matriculaId}...`);
      
      this.http.delete(`${this.apiUrl}/${matriculaId}`).subscribe({
        next: () => {
          console.log('[Matrículas] Matrícula removida com sucesso!');
          this.carregarDados(); // Recarrega do banco para atualizar a listagem e as contagens
        },
        error: (err) => {
          console.error('[Matrículas] Erro ao remover matrícula do backend:', err);
          alert('Erro ao excluir matrícula do banco de dados.');
        }
      });
    }
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  getIniciais(nome: string): string {
    const partes = nome.trim().split(' ');
    if (partes.length > 1) {
      return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
    }
    return partes[0][0].toUpperCase();
  }

  getLabelStatus(status: string): string {
    switch (status) {
      case 'NAO_INICIADO': return 'Não Iniciado';
      case 'EM_ANDAMENTO': return 'Em Andamento';
      case 'FINALIZADO': return 'Finalizado';
      default: return status;
    }
  }
}
