import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MenuLateralComponent } from '../../componentes/menu-lateral/menu-lateral.component';

// Nova interface refletindo exatamente o que vem do seu Java
export interface CategoriaObj {
  cod: string;
  displayName: string;
  corHex: string;
}

export interface CursoExibicao {
  id: number;
  titulo?: string;
  nome?: string; 
  descricao: string;
  linkCurso?: string; 
  link?: string; 
  categoria?: CategoriaObj; // Alterado de string para CategoriaObj
}

export interface MatriculaRespostaDTO {
  id: number;
  cursoId?: number;
  curso?: {
    id: number;
  };
  status?: string;
}

@Component({
  selector: 'app-catalogo-cursos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MenuLateralComponent],
  templateUrl: './catalogo-cursos.component.html', 
  styleUrls: ['./catalogo-cursos.component.css']    
})
export class CatalogoCursosComponent implements OnInit {
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080';

  cursos: CursoExibicao[] = []; 
  cursosFiltrados: CursoExibicao[] = [];
  meusCursosIds: Set<number> = new Set(); 
  
  searchTerm: string = '';
  categoriaSelecionada: string = 'Todos'; 
  loading: boolean = false; 
  isDarkMode: boolean = true; 
  isSidebarCollapsed: boolean = false; 

  ngOnInit(): void {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
    
    this.carregarDadosDoCatalogo();
  }

  private obterHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  carregarDadosDoCatalogo(): void {
    this.loading = true;
    const headers = this.obterHeaders();

    this.http.get<CursoExibicao[]>(`${this.API_URL}/cursos`, { headers }).subscribe({
      next: (dadosCursos) => {
        console.log('CURSOS RETORNADOS DO JAVA:', dadosCursos);
        this.cursos = dadosCursos || [];
        this.cursosFiltrados = [...this.cursos];
        this.buscarMatriculasDoAluno();
      },
      error: (err) => {
        console.error('Erro ao carregar catálogo de cursos:', err);
        this.loading = false;
      }
    });
  }

  buscarMatriculasDoAluno(): void {
    const headers = this.obterHeaders();

    this.http.get<MatriculaRespostaDTO[]>(`${this.API_URL}/matriculas`, { headers }).subscribe({
      next: (matriculas) => {
        this.meusCursosIds.clear();
        
        if (matriculas && Array.isArray(matriculas)) {
          matriculas.forEach(mat => {
            if (mat.cursoId) {
              this.meusCursosIds.add(mat.cursoId);
            } else if (mat.curso && mat.curso.id) {
              this.meusCursosIds.add(mat.curso.id);
            }
          });
        }

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao buscar suas matrículas:', err);
        this.loading = false; 
      }
    });
  }

  matricular(cursoId: number): void {
    if (!cursoId) return;
    const headers = this.obterHeaders();
    const body = { cursoId: cursoId };

    this.http.post(`${this.API_URL}/matriculas`, body, { headers }).subscribe({
      next: () => {
        this.meusCursosIds.add(cursoId);
        alert('Matrícula realizada com sucesso!');
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao realizar matrícula:', err);
        alert('Não foi possível realizar a sua matrícula.');
      }
    });
  }

  isMatriculado(cursoId: number): boolean {
    return this.meusCursosIds.has(cursoId);
  }

  irParaCurso(curso: CursoExibicao): void {
    const url = curso.linkCurso || curso.link;
    if (url) {
      window.open(url, '_blank');
    } else {
      this.router.navigate(['/aluno/meus-cursos']);
    }
  }

  filtrarCursos(): void {
    const termo = this.searchTerm.toLowerCase().trim();
    
    this.cursosFiltrados = this.cursos.filter((curso: CursoExibicao) => {
      const titulo = (curso.titulo || curso.nome || '').toLowerCase();
      const descricao = (curso.descricao || '').toLowerCase();

      const correspondeTermo = !termo || 
        titulo.includes(termo) ||
        descricao.includes(termo);
        
      const correspondeCategoria = this.categoriaSelecionada === 'Todos' || 
        curso.categoria?.cod === this.categoriaSelecionada; // Filtra pelo código

      return correspondeTermo && correspondeCategoria;
    });
  }

  filtrarPorCategoria(event: Event): void {
    const elemento = event.target as HTMLSelectElement;
    this.categoriaSelecionada = elemento.value;
    this.filtrarCursos();
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  // Agora estas funções usam de forma inteligente o que já vem do seu Banco de Dados!
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
