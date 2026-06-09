import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../services/user.service';

export const roleGuard = (papeisPermitidos: string[]): CanActivateFn => {
  return (route, state) => {
    const userService = inject(UserService);
    const router = inject(Router);

    const logado = userService.estaLogado(); 
    const usuario = userService.usuarioLogado; 
    const papelUsuario = usuario?.papel;

    // Se NÃO está logado, manda para o Login
    if (!logado) {
      if (state.url === '/login') return true;
      return router.createUrlTree(['/login']);
    }

    //  Se ESTÁ logado, mas o papel ainda não foi carregado
    if (!papelUsuario) {
      console.warn('Papel do usuário não encontrado no Guard. Redirecionando para a Home.');
      if (state.url === '/') return true;
      return router.createUrlTree(['/']);
    }

    // Se o usuário tem o papel necessário (ex: se na rota diz 'ROLE_USER', e ele é 'ROLE_ALUNO', precisamos dar permissão)
    // Para garantir flexibilidade, vamos mapear que ROLE_ALUNO equivale a ROLE_USER nas permissões de rotas.
    const papeisCompativeis = [...papeisPermitidos];
    if (papeisCompativeis.includes('ROLE_USER')) {
      papeisCompativeis.push('ROLE_ALUNO');
    }

    if (papeisCompativeis.includes(papelUsuario)) {
      return true; 
    }

    //Redirecionamento de segurança para evitar loops:
    console.warn(`Usuário com papel ${papelUsuario} tentou acessar ${state.url}. Redirecionando com segurança...`);

    if (papelUsuario === 'ROLE_ADMIN') {
      if (state.url.startsWith('/admin')) return true;
      return router.createUrlTree(['/admin/cursos']);
    } 
    
    if (papelUsuario === 'ROLE_USER' || papelUsuario === 'ROLE_ALUNO') {
      if (state.url.startsWith('/aluno')) return true;
      return router.createUrlTree(['/aluno/meus-cursos']);
    }

    return router.createUrlTree(['/']);
  };
};
