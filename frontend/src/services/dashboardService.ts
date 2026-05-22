import { api } from './api';
import type { Dashboard } from '../types/api';

export const dashboardService = {
  resumo: (terminalId?: number) =>
    api
      .get<Dashboard>('api/dashboard/resumo', { params: terminalId ? { terminalId } : {} })
      .then((r) => r.data),
};
