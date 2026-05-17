import { api } from './api';
import type { Estacionamento, PageResponse, StatusEstacionamento } from '../types/api';

export interface EstacionamentoForm {
  terminalId: number;
  nome: string;
  capacidade: number;
  status?: StatusEstacionamento;
}

export const estacionamentoService = {
  list: (params: { terminalId?: number; page?: number; size?: number } = {}) =>
    api.get<PageResponse<Estacionamento>>('/estacionamentos', { params }).then((r) => r.data),
  create: (data: EstacionamentoForm) =>
    api.post<Estacionamento>('/estacionamentos', data).then((r) => r.data),
  update: (id: number, data: EstacionamentoForm) =>
    api.put<Estacionamento>(`/estacionamentos/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/estacionamentos/${id}`),
};
