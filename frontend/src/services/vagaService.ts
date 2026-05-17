import { api } from './api';
import type { PageResponse, StatusVaga, Vaga } from '../types/api';

export interface VagaForm {
  estacionamentoId: number;
  codigo: string;
  status?: StatusVaga;
}

export const vagaService = {
  list: (params: { estacionamentoId?: number; page?: number; size?: number } = {}) =>
    api.get<PageResponse<Vaga>>('/vagas', { params }).then((r) => r.data),
  create: (data: VagaForm) => api.post<Vaga>('/vagas', data).then((r) => r.data),
  update: (id: number, data: VagaForm) => api.put<Vaga>(`/vagas/${id}`, data).then((r) => r.data),
  updateStatus: (id: number, status: StatusVaga) =>
    api.patch<Vaga>(`/vagas/${id}/status`, { status }).then((r) => r.data),
  remove: (id: number) => api.delete(`/vagas/${id}`),
};
