import { api } from './api';
import type { PageResponse, Perfil, Usuario } from '../types/api';

export interface UsuarioForm {
  nome: string;
  email: string;
  perfil: Perfil;
  ativo?: boolean;
  senha?: string;
}

export const usuarioService = {
  list: (params: { page?: number; size?: number } = {}) =>
    api.get<PageResponse<Usuario>>('/usuarios', { params }).then((r) => r.data),
  create: (data: UsuarioForm) => api.post<Usuario>('/usuarios', data).then((r) => r.data),
  update: (id: number, data: UsuarioForm) =>
    api.put<Usuario>(`/usuarios/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/usuarios/${id}`),
};
