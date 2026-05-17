import { api } from './api';
import type { PageResponse, Terminal } from '../types/api';

export interface TerminalForm {
  nome: string;
  endereco?: string;
  cidade: string;
  ativo?: boolean;
}

export const terminalService = {
  list: (page = 0, size = 20) =>
    api.get<PageResponse<Terminal>>('/terminais', { params: { page, size } }).then((r) => r.data),
  create: (data: TerminalForm) => api.post<Terminal>('/terminais', data).then((r) => r.data),
  update: (id: number, data: TerminalForm) =>
    api.put<Terminal>(`/terminais/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/terminais/${id}`),
};
