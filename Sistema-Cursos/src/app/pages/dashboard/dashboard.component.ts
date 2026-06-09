import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; 
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { MenuLateralComponent } from '../../componentes/menu-lateral/menu-lateral.component';
import { UserService } from '../../core/services/user.service'; 
import { CursoService } from '../../core/services/curso.service';

export interface CategoriaObj {
  cod: string;
  displayName: string;
  corHex: string;
}

export interface Curso {
  id: number;
  titulo: string;
  descricao: string;
  categoria: any;
  linkCurso: string;
  matriculado?: boolean; // Controla se o aluno está matriculado
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MenuLateralComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private userService = inject(UserService);
  private cursoService = inject(CursoService);
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router); 

  private readonly API_URL = 'http://localhost:8080';

  isDarkMode = true;
  isSidebarCollapsed = false;
  usuarioNome = 'Usuário';
  
  // Controle de Perfil
  isAluno = false; 

  // Filtros
  buscaTexto: string = '';
  categoriaSelecionada: string = 'Todas';

  // Métricas Dinâmicas
  totalCursos = 0;
  totalCategorias = 3;
  totalParceiros = 12;

  categorias = [
    { valor: 'Todas', label: 'Todas as categorias' },
    { valor: 'PROGRAMACAO', label: 'Programação' },
    { valor: 'DADOS_LOGICA', label: 'Dados & Lógica' },
    { valor: 'DESIGN', label: 'Design' }
  ];

  cursos: Curso[] = [];
  cursosFiltrados: Curso[] = [];
  matriculasDoAluno: number[] = []; // Guarda IDs dos cursos que o aluno está matriculado

  ngOnInit() {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
    
    this.carregarDadosIniciais();
  }


  private obterHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  carregarDadosIniciais() {
    const headers = this.obterHeaders();

    //Busca os dados do usuário logado
    this.userService.obterUsuarioLogado().subscribe({
      next: (usuario: any) => {
        console.log('=== DADOS DO USUÁRIO RETORNADOS DO BACKEND ===', usuario);

        if (usuario) {
          this.usuarioNome = usuario.nome ? usuario.nome.trim().split(' ')[0] : 'Usuário';
          
          // Verifica o papel usando as possibilidades comuns
          const perfil = usuario.perfil || usuario.role || usuario.papel || '';
          this.isAluno = perfil.toUpperCase().includes('ALUNO');

          console.log('O perfil identificado é:', perfil);
          console.log('O usuário é considerado Aluno?', this.isAluno);
        }

        //  Se for Aluno, busca também as matrículas dele para cruzamento
        const requisicaoMatriculas = this.isAluno 
          ? this.http.get<any[]>(`${this.API_URL}/matriculas`, { headers }) 
          : of([]); // Se não for aluno, retorna uma lista vazia simulada

        requisicaoMatriculas.subscribe({
          next: (matriculas) => {
            console.log('Matrículas recebidas do aluno:', matriculas);
            this.matriculasDoAluno = matriculas.map(m => m.cursoId || m.curso?.id);
            this.carregarCursosDoBanco();
          },
          error: (err) => {
            console.error('Erro ao carregar matrículas:', err);
            this.carregarCursosDoBanco();
          }
        });
      },
      error: (err) => {
        console.error('Erro ao obter usuário logado:', err);
        this.carregarCursosDoBanco();
      }
    });
  }

  carregarCursosDoBanco() {
    this.cursoService.listarTodos().subscribe({
      next: (dados: any[]) => {
        const cursosAtivos = dados.filter(curso => curso.ativo !== false);

        this.cursos = cursosAtivos.map(curso => {
          let categoriaFinal: any = 'PROGRAMACAO';

          if (curso.categoria) {
            if (typeof curso.categoria === 'object') {
              categoriaFinal = {
                cod: curso.categoria.cod || 'PROGRAMACAO',
                displayName: curso.categoria.displayName || 'Programação',
                corHex: curso.categoria.corHex || '#7C3AED'
              };
            } else {
              categoriaFinal = curso.categoria;
            }
          }

          const idCurso = Number(curso.id);

          return {
            id: idCurso,
            titulo: curso.titulo || curso.nome || '',
            descricao: curso.descricao || '',
            categoria: categoriaFinal,
            linkCurso: curso.linkCurso || curso.link || '',
            // Se for Aluno, verifica se o ID deste curso está na lista de matrículas dele
            matriculado: this.isAluno ? this.matriculasDoAluno.includes(idCurso) : true
          };
        });

        console.log('Cursos carregados e mapeados no Front:', this.cursos);

        this.totalCursos = this.cursos.length;
        this.filtrarCursos();
      },
      error: (err) => {
        console.error('Erro ao carregar cursos:', err);
      }
    });
  }

  // Ação ao clicar no botão de acessar ou matricular
  clicarBotaoCurso(curso: Curso): void {
    // 1. Se NÃO for aluno ou já estiver matriculado, permite o acesso
    if (!this.isAluno || curso.matriculado) {
      if (curso.linkCurso) {
        window.open(curso.linkCurso, '_blank');
      } else {
        alert('Este curso ainda não possui um link de transmissão associado.');
      }
    } else {
      // 2. Se for Aluno e NÃO estiver matriculado, bloqueia e redireciona para o catálogo de matrículas
      const desejaIrParaCatalogo = confirm(
        `Acesso bloqueado! Você precisa estar matriculado para acessar este curso.\n\nDeseja ir para o Catálogo de Cursos para se matricular agora?`
      );

      if (desejaIrParaCatalogo) {
        console.log('Redirecionando aluno para o catálogo de matrículas...');
        // CORRIGIDO: Rota alterada de '/aluno/catalogo' para '/catalogo' com base no seu arquivo de rotas
        this.router.navigate(['/catalogo']); 
      }
    }
  }

  realizarMatricula(curso: Curso): void {
    const headers = this.obterHeaders().set('Content-Type', 'application/json');
    
    this.http.post(`${this.API_URL}/matriculas?cursoId=${curso.id}`, {}, { headers }).subscribe({
      next: () => {
        alert(`Matrícula efetuada com sucesso no curso: ${curso.titulo}!`);
        curso.matriculado = true; // Libera o acesso na hora no front-end
        this.filtrarCursos();
      },
      error: (err) => {
        console.error('Erro ao realizar matrícula:', err);
        alert(err.error?.mensagem || 'Erro ao processar matrícula.');
      }
    });
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  getCategoryClass(categoria: any): string {
    const cod = typeof categoria === 'object' ? categoria?.cod : categoria;
    switch (cod) {
      case 'PROGRAMACAO': return 'programacao';
      case 'DADOS_LOGICA': return 'dados';
      case 'DESIGN': return 'design';
      default: return 'programacao';
    }
  }

  getCategoryLabel(categoria: any): string {
    if (typeof categoria === 'object' && categoria?.displayName) {
      return categoria.displayName;
    }
    switch (categoria) {
      case 'PROGRAMACAO': return 'Programação';
      case 'DADOS_LOGICA': return 'Dados & Lógica';
      case 'DESIGN': return 'Design';
      default: return 'Geral';
    }
  }

  filtrarCursos() {
    this.cursosFiltrados = this.cursos.filter(curso => {
      const tituloCurso = (curso.titulo || '').toLowerCase();
      const descricaoCurso = (curso.descricao || '').toLowerCase();
      const termoBusca = (this.buscaTexto || '').toLowerCase();

      const bateTexto = tituloCurso.includes(termoBusca) || descricaoCurso.includes(termoBusca);
      
      const codCurso = typeof curso.categoria === 'object' ? curso.categoria?.cod : curso.categoria;
      const bateCategoria = this.categoriaSelecionada === 'Todas' || codCurso === this.categoriaSelecionada;

      return bateTexto && bateCategoria;
    });

    this.cdr.detectChanges();
  }
}
