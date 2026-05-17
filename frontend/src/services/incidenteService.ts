import { api } from './api';
import type {
  Incidente,
  NivelGravidade,
  PageResponse,
  StatusIncidente,
} from '../types/api';

export interface IncidenteForm {
  tipoIncidenteId: number;
  terminalId: number;
  docaId?: number;
  estacionamentoId?: number;
  vagaId?: number;
  operacaoId?: number;
  ocorridoEm: string;
  descricao: string;
  acaoTomada?: string;
}

export const incidenteService = {
  list: (params: {
    status?: StatusIncidente;
    terminalId?: number;
    gravidade?: NivelGravidade;
    de?: string;
    ate?: string;
    page?: number;
    size?: number;
  } = {}) =>
    api.get<PageResponse<Incidente>>('/incidentes', { params }).then((r) => r.data),
  findById: (id: number) => api.get<Incidente>(`/incidentes/${id}`).then((r) => r.data),
  create: (data: IncidenteForm) =>
    api.post<Incidente>('/incidentes', data).then((r) => r.data),
  update: (id: number, data: IncidenteForm) =>
    api.put<Incidente>(`/incidentes/${id}`, data).then((r) => r.data),
  encerrar: (id: number, acaoTomada: string) =>
    api.patch<Incidente>(`/incidentes/${id}/encerrar`, { acaoTomada }).then((r) => r.data),
  remove: (id: number) => api.delete(`/incidentes/${id}`),
};
