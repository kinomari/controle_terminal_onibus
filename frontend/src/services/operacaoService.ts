import { api } from './api';
import type {
  DocumentoCarga,
  Operacao,
  OperacaoResumo,
  PageResponse,
  StatusOperacao,
  TipoDocumento,
  TipoOperacao,
} from '../types/api';

export interface CheckinForm {
  terminalId: number;
  veiculoId: number;
  tipo: TipoOperacao;
  docaId?: number;
  vagaId?: number;
  descricaoCarga?: string;
  quantidadeVolume?: number;
  pesoEstimado?: number;
  observacao?: string;
}

export interface DocumentoForm {
  tipo: TipoDocumento;
  numero: string;
  emitidoEm?: string;
  observacao?: string;
}

export const operacaoService = {
  list: (params: { status?: StatusOperacao; terminalId?: number; page?: number; size?: number } = {}) =>
    api.get<PageResponse<OperacaoResumo>>('/operacoes', { params }).then((r) => r.data),
  findById: (id: number) => api.get<Operacao>(`/operacoes/${id}`).then((r) => r.data),
  checkin: (data: CheckinForm) =>
    api.post<Operacao>('/operacoes/checkin', data).then((r) => r.data),
  iniciar: (id: number) =>
    api.patch<Operacao>(`/operacoes/${id}/iniciar`).then((r) => r.data),
  finalizar: (id: number) =>
    api.patch<Operacao>(`/operacoes/${id}/finalizar`).then((r) => r.data),
  checkout: (id: number) =>
    api.patch<Operacao>(`/operacoes/${id}/checkout`).then((r) => r.data),
  cancelar: (id: number) =>
    api.patch<Operacao>(`/operacoes/${id}/cancelar`).then((r) => r.data),

  listDocumentos: (operacaoId: number) =>
    api.get<DocumentoCarga[]>(`/operacoes/${operacaoId}/documentos`).then((r) => r.data),
  addDocumento: (operacaoId: number, data: DocumentoForm) =>
    api.post<DocumentoCarga>(`/operacoes/${operacaoId}/documentos`, data).then((r) => r.data),
  removeDocumento: (id: number) => api.delete(`/documentos/${id}`),
};
