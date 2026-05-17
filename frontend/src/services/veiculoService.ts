import { api } from './api';
import type { PageResponse, TipoEmpresa, TipoVeiculo, Veiculo } from '../types/api';

export interface VeiculoForm {
  placa: string;
  tipo: TipoVeiculo;
  empresaResponsavel: string;
  tipoEmpresa: TipoEmpresa;
  modelo?: string;
}

export const veiculoService = {
  list: (params: { placa?: string; page?: number; size?: number } = {}) =>
    api.get<PageResponse<Veiculo>>('/veiculos', { params }).then((r) => r.data),
  create: (data: VeiculoForm) => api.post<Veiculo>('/veiculos', data).then((r) => r.data),
  update: (id: number, data: VeiculoForm) =>
    api.put<Veiculo>(`/veiculos/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/veiculos/${id}`),
};
