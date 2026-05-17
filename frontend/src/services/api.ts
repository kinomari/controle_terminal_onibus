import axios, { AxiosError } from 'axios';
import type { ApiError } from '../types/api';

export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

const STORAGE_KEY = 'city.token';

export function setStoredToken(token: string | null) {
  if (token) localStorage.setItem(STORAGE_KEY, token);
  else localStorage.removeItem(STORAGE_KEY);
}

export function getStoredToken(): string | null {
  return localStorage.getItem(STORAGE_KEY);
}

api.interceptors.request.use((config) => {
  const token = getStoredToken();
  if (token) {
    config.headers = config.headers ?? {};
    (config.headers as Record<string, string>).Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (resp) => resp,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401) {
      setStoredToken(null);
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export function extractApiError(err: unknown): string {
  const axiosErr = err as AxiosError<ApiError>;
  return (
    axiosErr.response?.data?.message ||
    axiosErr.message ||
    'Erro desconhecido. Verifique sua conexao com o servidor.'
  );
}
