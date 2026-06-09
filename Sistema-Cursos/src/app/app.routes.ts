import { Routes } from '@angular/router';

// Guards de Segurança
import { roleGuard } from './core/guards/role.guard';

// Componente Pagina Inicial
import { HomeComponent } from "./pages/home/home.component";
import { DashboardComponent } from './pages/dashboard/dashboard.component';

// Componentes de Autenticação (dentro de auth/)
import { LoginComponent } from './pages/auth/login/login.component';
import { CadastroComponent } from './pages/auth/cadastro/cadastro.component';

// Componente Perfil (Compartilhado e Unificado)
import { PerfilComponent } from './pages/perfil/perfil.component';

// Componentes do Aluno
import { CatalogoCursosComponent } from './pages/catalogo-cursos/catalogo-cursos.component';
import { MeusCursosComponent } from './pages/aluno/meus-cursos/meus-cursos.component';

// Componentes do Admin 
import { GerenciarCursosComponent } from './pages/admin/gerenciar-cursos/gerenciar-cursos.component';
import { GerenciarUsuariosComponent } from './pages/admin/gerenciar-usuarios/gerenciar-usuarios.component';
import { GerenciarMatriculasComponent } from './pages/admin/gerenciar-matriculas/gerenciar-matriculas.component';

export const routes: Routes = [
  // ==========================================
  // ROTAS PÚBLICAS
  // ==========================================
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'cadastro', component: CadastroComponent },

  // ==========================================
  // ROTAS COMPARTILHADAS (Qualquer usuário logado pode acessar)
  // ==========================================
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_ADMIN'])] 
  },
  { 
    path: 'perfil', 
    component: PerfilComponent,
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_ADMIN'])] 
  },
  {
    path:'catalogo',
    component: CatalogoCursosComponent,
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_ADMIN'])]
  },

  // ==========================================
  //  ROTAS EXCLUSIVAS DO ALUNO (Apenas ROLE_USER)
  // ==========================================
   {
    path: 'aluno',
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_ALUNO'])], 
    children: [
      { path: 'meus-cursos', component: MeusCursosComponent }, 
      { path: '', redirectTo: 'meus-cursos', pathMatch: 'full' }
    ]
  },

  // ==========================================
  //  ROTAS EXCLUSIVAS DO ADMIN (Apenas ROLE_ADMIN) 
 //=============================================
 {
    path: 'admin',
    canActivate: [roleGuard(['ROLE_ADMIN'])], 
    children: [
      { path: 'cursos', component: GerenciarCursosComponent },
      { path: 'usuarios', component: GerenciarUsuariosComponent },
      { path: 'matriculas', component: GerenciarMatriculasComponent },
      { path: '', redirectTo: 'cursos', pathMatch: 'full' } // Se acessar /admin, vai para gerenciar cursos
    ]
  },

  // ==========================================
  //  REDIRECIONAMENTOS E ERROS (Wildcards)
  // ==========================================
  // Se o usuário tentar acessar uma rota inexistente, manda de volta para a Home
  { path: '**', redirectTo: '' } 
];
