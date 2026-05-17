import { api } from './api';
import type { NivelGravidade, PageResponse, TipoIncidente } from '../types/api';

export interface TipoIncidenteForm {
  nome: string;
  descricao?: string;
  nivelGravidade: NivelGravidade;
}

export const tipoIncidenteService = {
  list: (params: { page?: number; size?: number } = {}) =>
    api.get<PageResponse<TipoIncidente>>('/tipos-incidente', { params }).then((r) => r.data),
  create: (data: TipoIncidenteForm) =>
    api.post<TipoIncidente>('/tipos-incidente', data).then((r) => r.data),
  update: (id: number, data: TipoIncidenteForm) =>
    api.put<TipoIncidente>(`/tipos-incidente/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/tipos-incidente/${id}`),
};
