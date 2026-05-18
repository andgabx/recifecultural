import axios, { AxiosError, type AxiosInstance } from "axios";

/*
 * Axios client unico para o backend BFF do Recife Cultural.
 * Base URL e configuravel via NEXT_PUBLIC_API_URL (default: http://localhost:8080).
 * Todos os endpoints BFF moram em /api/bff/ (ver Fase 4).
 */

const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL?.replace(/\/+$/, "") ??
  "http://localhost:8080";

export const api: AxiosInstance = axios.create({
  baseURL: `${BASE_URL}/api/bff`,
  timeout: 15000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

export type ApiError = {
  message: string;
  status?: number;
  payload?: unknown;
};

export function toApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string; erro?: string }>;
    const payload = axiosError.response?.data;
    const message =
      payload?.message ??
      payload?.erro ??
      axiosError.message ??
      "Erro de comunicacao com o backend.";
    return {
      message,
      status: axiosError.response?.status,
      payload,
    };
  }
  if (error instanceof Error) return { message: error.message };
  return { message: "Erro desconhecido." };
}

api.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(toApiError(error)),
);
