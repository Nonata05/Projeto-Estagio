export type Papel = 'ROLE_ADMIN' | 'ROLE_USER';
export type CursoStatus = 'NAO_INICIADO' | 'EM_ANDAMENTO' | 'CONCLUIDO';

// Representa o Usuário do sistema
export interface Usuario {
  id: number;
  nome: string;
  email: string;
  papel: Papel;
  dataCriacao?: string;
}

// DTOs de Envio (Request) para o Back-end
export interface CursoRequestDTO {
  titulo: string;
  descricao?: string;
  linkCurso: string;
}

export interface MatriculaRequestDTO {
  cursoId: number;
}

export interface MatriculaStatusDTO {
  status: CursoStatus;
}

// DTOs de Retorno (Response) do Back-end
export interface CursoRespostaDTO {
  id: number;
  titulo: string;
  descricao?: string;
  linkCurso: string;
  ativo: boolean;
  dataCriacao: string;
  status: CursoStatus;
}

export interface MatriculaRespostaDTO {
  id: number;
  usuario: {
    id: number;
    nome: string;
    email: string;
  };
  curso: CursoRespostaDTO;
  status: CursoStatus;
  dataInicio?: string;
  dataFinal?: string;
}

// Retorno do Login (Token JWT + dados básicos)
export interface LoginResponse {
  token: string;
  usuario: Usuario;
}
