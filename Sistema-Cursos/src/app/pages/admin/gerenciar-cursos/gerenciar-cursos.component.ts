import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MenuLateralComponent } from '../../../componentes/menu-lateral/menu-lateral.component';
import { CursoService } from '../../../core/services/curso.service';

export interface CursoAdmin {
  id: number;
  titulo: string;
  descricao?: string;
  linkCurso: string;
  categoria: 'PROGRAMACAO' | 'DADOS_LOGICA' | 'DESIGN';
  ativo: boolean; 
}

@Component({
  selector: 'app-gerenciar-cursos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MenuLateralComponent],
  templateUrl: './gerenciar-cursos.component.html',
  styleUrl: './gerenciar-cursos.component.css'
})
export class GerenciarCursosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private cursoService = inject(CursoService);

  cursos: CursoAdmin[] = [];
  cursosFiltrados: CursoAdmin[] = [];
  cursoForm!: FormGroup;

  searchTerm: string = '';
  categoriaSelecionada: string = 'Todos';
  isDarkMode: boolean = true;
  isSidebarCollapsed: boolean = false;
  
  isModalOpen: boolean = false;
  modoEdicao: boolean = false;
  cursoEmEdicaoId: number | null = null;

  mostrarToast: boolean = false;
  mensagemToast: string = '';
  toastTipo: 'sucesso' | 'erro' = 'sucesso';

  ngOnInit(): void {
    const theme = localStorage.getItem('theme');
    this.isDarkMode = theme ? theme === 'dark' : true;

    this.inicializarFormulario();
    this.carregarCursosDoBanco();
  }

  inicializarFormulario(): void {
    this.cursoForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(5)]],
      descricao: ['', [Validators.required, Validators.minLength(15)]],
      linkCurso: ['', [Validators.required, Validators.pattern('https?://.+')]],
      categoria: ['PROGRAMACAO', [Validators.required]],
      ativo: [true] 
    });
  }

  carregarCursosDoBanco(): void {
    this.cursoService.listarTodos().subscribe({
      next: (dados: any[]) => {
        this.cursos = dados.map(curso => ({
          id: Number(curso.id),
          titulo: curso.titulo,
          descricao: curso.descricao || '',
          linkCurso: curso.linkCurso || curso.LinkCurso || '',
          categoria: curso.categoria || 'PROGRAMACAO',
          ativo: curso.ativo !== false // Se for null ou undefined no banco, trata como true
        })) as CursoAdmin[];

        this.filtrarCursos();
      },
      error: (err: any) => {
        console.error('Erro ao buscar cursos do banco:', err);
        this.exibirToast('Erro ao carregar cursos do banco de dados.', 'erro');
      }
    });
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  filtrarCursos(): void {
    const termo = this.searchTerm.toLowerCase().trim();
    this.cursosFiltrados = this.cursos.filter(curso => {
      const titulo = curso.titulo ? curso.titulo.toLowerCase() : '';
      const descricao = curso.descricao ? curso.descricao.toLowerCase() : '';

      const correspondeTermo = !termo || 
        titulo.includes(termo) || 
        descricao.includes(termo) ||
        String(curso.id).includes(termo);

      const correspondeCategoria = this.categoriaSelecionada === 'Todos' || 
        curso.categoria === this.categoriaSelecionada;

      return correspondeTermo && correspondeCategoria;
    });
    this.cdr.detectChanges();
  }

  abrirModalNovo(): void {
    this.modoEdicao = false;
    this.cursoEmEdicaoId = null;
    this.cursoForm.reset({ categoria: 'PROGRAMACAO', ativo: true });
    this.isModalOpen = true;
  }

  abrirModalEditar(curso: CursoAdmin): void {
    this.modoEdicao = true;
    this.cursoEmEdicaoId = curso.id;
    this.cursoForm.patchValue({
      titulo: curso.titulo,
      descricao: curso.descricao || '',
      linkCurso: curso.linkCurso,
      categoria: curso.categoria,
      ativo: curso.ativo
    });
    this.isModalOpen = true;
  }

  fecharModal(): void {
    this.isModalOpen = false;
    this.cursoForm.reset();
  }

  salvarCurso(): void {
    if (this.cursoForm.invalid) {
      console.warn('Formulário inválido! Verifique os erros nos campos:', this.obterCamposComErro());
      this.exibirToast('Preencha todos os campos corretamente antes de salvar.', 'erro');
      return;
    }

    const dadosCurso: any = {
      titulo: this.cursoForm.value.titulo,
      descricao: this.cursoForm.value.descricao,
      linkCurso: this.cursoForm.value.linkCurso,
      categoria: this.cursoForm.value.categoria,
      ativo: this.cursoForm.value.ativo 
    };

    if (this.modoEdicao && this.cursoEmEdicaoId !== null) {
      this.cursoService.atualizarCurso(this.cursoEmEdicaoId, dadosCurso).subscribe({
        next: (cursoAtualizado: any) => {
          const index = this.cursos.findIndex(c => c.id === this.cursoEmEdicaoId);
          if (index !== -1) {
            this.cursos[index] = { 
              ...cursoAtualizado, 
              id: Number(cursoAtualizado.id),
              linkCurso: cursoAtualizado.linkCurso || cursoAtualizado.LinkCurso || '',
              ativo: cursoAtualizado.ativo !== false
            } as CursoAdmin;
          }
          this.filtrarCursos();
          this.fecharModal();
          this.exibirToast('Curso atualizado com sucesso!', 'sucesso');
        },
        error: (err: any) => {
          console.error('Erro ao atualizar curso:', err);
          this.exibirToast('Erro ao atualizar dados do curso no servidor.', 'erro');
        }
      });
    } else {
      this.cursoService.criarCurso(dadosCurso).subscribe({
        next: (novoCurso: any) => {
          const cursoMapeado: CursoAdmin = {
            ...novoCurso,
            id: Number(novoCurso.id),
            linkCurso: novoCurso.linkCurso || novoCurso.LinkCurso || '',
            ativo: novoCurso.ativo !== false
          } as CursoAdmin;

          this.cursos.push(cursoMapeado);
          this.filtrarCursos();
          this.fecharModal();
          this.exibirToast('Curso cadastrado com sucesso!', 'sucesso');
        },
        error: (err: any) => {
          console.error('Erro ao criar curso:', err);
          this.exibirToast('Erro ao salvar o novo curso no servidor.', 'erro');
        }
      });
    }
  }

  // Permite ativar/desativar o curso de forma rápida clicando no toggle switch direto na tabela!
  toggleStatusCurso(curso: CursoAdmin): void {
    const novoStatus = !curso.ativo;
    
    // Payload mantendo dados originais e alterando somente o status de ativação
    const dadosCurso: any = {
      titulo: curso.titulo,
      descricao: curso.descricao,
      linkCurso: curso.linkCurso,
      categoria: curso.categoria,
      ativo: novoStatus
    };

    this.cursoService.atualizarCurso(curso.id, dadosCurso).subscribe({
      next: (cursoAtualizado: any) => {
        curso.ativo = cursoAtualizado.ativo !== false;
        this.filtrarCursos();
        this.exibirToast(`Curso ${novoStatus ? 'ativado' : 'desativado'} com sucesso!`, 'sucesso');
      },
      error: (err: any) => {
        console.error('Erro ao alterar status de atividade do curso:', err);
        this.exibirToast('Erro ao alterar o status do curso.', 'erro');
      }
    });
  }

  private obterCamposComErro(): string[] {
    const erros: string[] = [];
    Object.keys(this.cursoForm.controls).forEach(key => {
      const controlErrors = this.cursoForm.get(key)?.errors;
      if (controlErrors != null) {
        erros.push(`${key}: ${JSON.stringify(controlErrors)}`);
      }
    });
    return erros;
  }

  excluirCurso(id: number): void {
    if (confirm('Tem certeza de que deseja desativar/excluir este curso definitivamente?')) {
      this.cursoService.excluirCurso(id).subscribe({
        next: () => {
          this.cursos = this.cursos.filter(c => c.id !== id);
          this.filtrarCursos();
          this.exibirToast('Curso excluído/desativado com sucesso!', 'sucesso');
        },
        error: (err: any) => {
          console.error('Erro ao excluir curso:', err);
          this.exibirToast('Não foi possível excluir o curso.', 'erro');
        }
      });
    }
  }

  exibirToast(mensagem: string, tipo: 'sucesso' | 'erro' = 'sucesso'): void {
    this.mensagemToast = mensagem;
    this.toastTipo = tipo;
    this.mostrarToast = true;

    setTimeout(() => {
      this.mostrarToast = false;
    }, 4000);
  }

  getCategoriaLabel(categoria: any): string {
    const catStr = categoria && typeof categoria === 'object' ? (categoria.nome || categoria.codigo) : categoria;
    switch (catStr) {
      case 'PROGRAMACAO': return 'Programação';
      case 'DADOS_LOGICA': return 'Dados & Lógica';
      case 'DESIGN': return 'Design';
      default: return 'Geral';
    }
  }

  getCategoriaBadgeClass(categoria: any): string {
    if (!categoria) return 'badge-geral';
    
    const catStr = typeof categoria === 'object' ? (categoria.nome || categoria.codigo || JSON.stringify(categoria)) : String(categoria);
    
    const safeClass = catStr
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9]/g, '-');

    return `badge-${safeClass}`;
  }
}
