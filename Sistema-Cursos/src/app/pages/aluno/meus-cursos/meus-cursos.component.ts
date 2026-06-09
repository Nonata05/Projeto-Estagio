import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { MenuLateralComponent } from '../../../componentes/menu-lateral/menu-lateral.component';

export interface CategoriaObj {
  cod: string;
  displayName: string;
  corHex: string;
}

export interface CursoMatriculado {
  id: number;          // ID do Curso
  matriculaId: number; // ID da Matrícula (UserCurso)
  titulo: string;
  descricao: string;
  linkCurso: string; 
  categoria?: CategoriaObj;
  status: 'NAO_INICIADO' | 'EM_ANDAMENTO' | 'FINALIZADO';
}

@Component({
  selector: 'app-meus-cursos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MenuLateralComponent],
  templateUrl: './meus-cursos.component.html',
  styleUrl: './meus-cursos.component.css'
})
export class MeusCursosComponent implements OnInit {
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080';

  meusCursos: CursoMatriculado[] = [];
  cursosFiltrados: CursoMatriculado[] = [];
  
  searchTerm: string = '';
  statusSelecionado: string = 'Todos'; 
  categoriaSelecionada: string = 'Todos'; 
  loading: boolean = false;
  isDarkMode: boolean = true;
  isSidebarCollapsed: boolean = false;

  ngOnInit(): void {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
    
    this.carregarMeusCursos();
  }

  private obterHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  carregarMeusCursos(): void {
    this.loading = true;
    const headers = this.obterHeaders();

    forkJoin({
      cursos: this.http.get<any[]>(`${this.API_URL}/cursos`, { headers }),
      matriculas: this.http.get<any[]>(`${this.API_URL}/matriculas`, { headers })
    }).subscribe({
      next: ({ cursos, matriculas }) => {
        this.meusCursos = matriculas.map(mat => {
          const detalheCurso = cursos.find(c => c.id === mat.cursoId);
          return {
            id: mat.cursoId,
            matriculaId: mat.id,
            titulo: detalheCurso?.titulo || detalheCurso?.nome || mat.nomeCurso,
            descricao: detalheCurso?.descricao || '',
            linkCurso: detalheCurso?.linkCurso || detalheCurso?.link || '',
            categoria: detalheCurso?.categoria,
            status: mat.status
          };
        });

        this.filtrarMeusCursos();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar dados do usuário:', err);
        this.loading = false;
      }
    });
  }

  
  atualizarStatus(curso: CursoMatriculado, novoStatus: any): void {
    const statusAnterior = curso.status; 
    
    curso.status = novoStatus;

    const headers = this.obterHeaders().set('Content-Type', 'application/json');
    const body = { status: novoStatus };

    
    this.http.put(`${this.API_URL}/matriculas/${curso.matriculaId}`, body, { headers }).subscribe({
      next: (respostaAtualizada: any) => {
       
        curso.status = respostaAtualizada.status;
        this.filtrarMeusCursos();
      },
      error: (err) => {
        console.error('Erro ao atualizar status no servidor:', err);
        alert('Não foi possível salvar a atualização do progresso.');
        
        
        curso.status = statusAnterior;
        
        this.filtrarMeusCursos();
        this.cdr.detectChanges(); 
      }
    });
  }

  estudarCurso(curso: CursoMatriculado): void {
    if (curso.linkCurso) {
      window.open(curso.linkCurso, '_blank');
    }
  }

  filtrarMeusCursos(): void {
    const termo = this.searchTerm.toLowerCase().trim();
    
    this.cursosFiltrados = this.meusCursos.filter((curso) => {
      const correspondeTermo = !termo || 
        curso.titulo.toLowerCase().includes(termo) ||
        curso.descricao.toLowerCase().includes(termo);
        
      const correspondeCategoria = this.categoriaSelecionada === 'Todos' || 
        curso.categoria?.cod === this.categoriaSelecionada;

      const correspondeStatus = this.statusSelecionado === 'Todos' || 
        curso.status === this.statusSelecionado;

      return correspondeTermo && correspondeCategoria && correspondeStatus;
    });
    this.cdr.detectChanges();
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'NAO_INICIADO': return 'Não Iniciado';
      case 'EM_ANDAMENTO': return 'Em andamento';
      case 'FINALIZADO': return 'Finalizado';
      default: return 'Desconhecido';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'NAO_INICIADO': return 'status-nao-iniciado';
      case 'EM_ANDAMENTO': return 'status-em-andamento';
      case 'FINALIZADO': return 'status-finalizado';
      default: return '';
    }
  }

  getCategoriaLabel(categoria?: CategoriaObj): string {
    return categoria?.displayName || 'Geral';
  }

  getCategoriaColor(categoria?: CategoriaObj): string {
    return categoria?.corHex || '#6B7280';
  }

  getGradienteCard(categoria?: CategoriaObj): string {
    const cor = this.getCategoriaColor(categoria);
    return `linear-gradient(135deg, ${cor} 0%, #110e21 100%)`;
  }
}
