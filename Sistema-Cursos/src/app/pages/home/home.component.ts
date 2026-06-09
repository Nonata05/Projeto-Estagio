import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { CursoService } from '../../core/services/curso.service';

export interface CursoRespostaUserDTO {
  id: number;
  titulo: string;
  descricao: string;
  linkCurso: string;
  categoria: 'PROGRAMACAO' | 'DADOS_LOGICA' | 'DESIGN';
}

interface Feature {
  icon: string;
  titulo: string;
  descricao: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private router = inject(Router);
  private userService = inject(UserService);
  private cursoService = inject(CursoService);
  private cdr = inject(ChangeDetectorRef); 

  isDarkMode = true;
  cursosDestaque: CursoRespostaUserDTO[] = [];

  stats = [
    { quantidade: '0', rotulo: 'Cursos no catálogo' },
    { quantidade: '+30', rotulo: 'Plataformas parceiras' },
    { quantidade: '100%', rotulo: 'Gratuito e Direto' }
  ];

  features: Feature[] = [
    {
      icon: 'grid_view',
      titulo: 'Organização dos cursos',
      descricao: 'Tudo categorizado para facilitar sua jornada de aprendizado.'
    },
    {
      icon: 'bolt',
      titulo: 'Busca rápida',
      descricao: 'Encontre exatamente o que você precisa em segundos.'
    },
    {
      icon: 'layers',
      titulo: 'Diversas áreas',
      descricao: 'De tecnologia a negócios, marketing, design e mais.'
    },
    {
      icon: 'link',
      titulo: 'Acesso simplificado',
      descricao: 'Um clique te leva direto ao conteúdo da plataforma parceira.'
    },
    {
      icon: 'all_inclusive',
      titulo: 'Aprendizado contínuo',
      descricao: 'Novos cursos adicionados ao catálogo todas as semanas.'
    }
  ];

  ngOnInit() {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;
    this.carregarCursosDoBanco();
  }

  carregarCursosDoBanco() {
    this.cursoService.listarTodos().subscribe({
      next: (dados: any[]) => {
        if (!dados) return;

        // Filtro ultra-seguro para pegar apenas cursos ativos
        const cursosAtivos = dados.filter(curso => curso.ativo !== false);

        this.cursosDestaque = cursosAtivos.map(curso => {
          let categoriaExtraida = 'PROGRAMACAO';

          // EXTRAÇÃO SEGURA: Verifica se a categoria é um objeto ou string antes de tratar
          if (curso.categoria) {
            if (typeof curso.categoria === 'object') {
              categoriaExtraida = curso.categoria.cod || 'PROGRAMACAO';
            } else if (typeof curso.categoria === 'string') {
              categoriaExtraida = curso.categoria;
            }
          }

          return {
            id: Number(curso.id),
            titulo: curso.titulo || 'Curso sem título',
            descricao: curso.descricao || 'Sem descrição disponível.',
            linkCurso: curso.linkCurso || curso.LinkCurso || '#',        
            categoria: categoriaExtraida.toUpperCase() as 'PROGRAMACAO' | 'DADOS_LOGICA' | 'DESIGN'
          };
        });

       
        this.stats[0].quantidade = `${this.cursosDestaque.length}`;
        
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao buscar cursos ativos para a Home:', err);
      }
    });
  }

  aoAcessarCurso(event: Event, curso: CursoRespostaUserDTO) {
    event.preventDefault();

    // Validação segura de sessão do usuário antes de abrir o link externo
    if (this.userService.usuarioLogado) {
      window.open(curso.linkCurso, '_blank');
    } else {
      alert('Atenção: Você precisa estar logado para acessar o conteúdo dos cursos!');
      this.router.navigate(['/login']);
    }
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  // Tratamento de segurança adicional para evitar que objetos ou nulos quebrem o CSS
  getThemeClass(categoria: any): string {
    if (!categoria) return 'programacao';
    
    // Se por acaso vier como objeto, extrai o código de forma segura
    const cod = typeof categoria === 'object' ? categoria?.cod : categoria;
    if (!cod || typeof cod !== 'string') return 'programacao';

    switch (cod.toUpperCase()) {
      case 'PROGRAMACAO': return 'programacao';
      case 'DADOS_LOGICA': return 'dados';
      case 'DESIGN': return 'design';
      default: return 'programacao';
    }
  }
}
