import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";

export type EspacoResumo = {
  id: UUID;
  nome: string;
  capacidadeMaxima: number;
  status: string;
};

export type CriarEspacoRequisicao = {
  nome: string;
  capacidadeMaxima: number;
  riderTecnico?: string[];
};

export type AtualizarCapacidadeRequisicao = {
  novaCapacidade: number;
  ingressosVendidosFuturos: number;
};

export const espacosService = {
  listar: () => api.get<EspacoResumo[]>("/espacos").then((r) => r.data),

  criar: (payload: CriarEspacoRequisicao) =>
    api.post<BffCriado>("/espacos", payload).then((r) => r.data),

  atualizarCapacidade: (id: UUID, payload: AtualizarCapacidadeRequisicao) =>
    api
      .put<BffSemConteudo>(`/espacos/${id}/capacidade`, payload)
      .then((r) => r.data),

  interditar: (id: UUID) =>
    api.post<BffSemConteudo>(`/espacos/${id}/interditar`).then((r) => r.data),
};

export type EspacosService = typeof espacosService;
