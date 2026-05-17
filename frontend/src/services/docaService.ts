import { api } from './api';
import type { Doca, PageResponse, StatusDoca } from '../types/api';

export interface DocaForm {
  terminalId: number;
  codigo: string;
  localizacao?: string;
  status?: StatusDoca;
}

export const docaService = {
  list: (params: { terminalId?: number; page?: number; size?: number } = {}) =>
    api.get<PageResponse<Doca>>('/docas', { params }).then((r) => r.data),
  listByTerminal: (terminalId: number) =>
    api.get<Doca[]>(`/docas/por-terminal/${terminalId}`).then((r) => r.data),
  create: (data: DocaForm) => api.post<Doca>('/docas', data).then((r) => r.data),
  update: (id: number, data: DocaForm) => api.put<Doca>(`/docas/${id}`, data).then((r) => r.data),
  updateStatus: (id: number, status: StatusDoca) =>
    api.patch<Doca>(`/docas/${id}/status`, { status }).then((r) => r.data),
  remove: (id: number) => api.delete(`/docas/${id}`),
};
