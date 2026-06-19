import { api } from "@/lib/api";
import type { BffCriado, StatusAssento, UUID } from "@/types/dominio";

export type AssentoResumo = {
  id: UUID;
  codigo: string;
  fileira: string;
  numero: number;
  status: StatusAssento;
  motivoIndisponibilidade?: string;
};

export type SetorComAssentos = {
  id: UUID;
  espacoId: UUID;
  nome: string;
  tipoSetor: string;
  fileirasHorizontais: number;
  assentosPorFileiraVertical: number;
  assentos: AssentoResumo[];
};

export type ConfigurarSetorRequisicao = {
  espacoId: UUID;
  nome: string;
  tipoSetor: string;
  fileirasHorizontais: number;
  assentosPorFileiraVertical: number;
};

export type EditarSetorRequisicao = {
  nome: string;
  tipoSetor: string;
  fileirasHorizontais: number;
  assentosPorFileiraVertical: number;
};

export type CapacidadeEspaco = {
  assentosDisponiveis: number;
};

export const setoresService = {
  listarPorEspaco: (espacoId: UUID, eventoId?: UUID) =>
    api
      .get<SetorComAssentos[]>(`/setores/espaco/${espacoId}`, {
        params: { ...(eventoId && { eventoId }) },
      })
      .then((r) => r.data),

  configurar: (payload: ConfigurarSetorRequisicao) =>
    api.post<BffCriado>("/setores", payload).then((r) => r.data),

  editar: (id: UUID, payload: EditarSetorRequisicao) =>
    api.put<BffCriado>(`/setores/${id}`, payload).then((r) => r.data),

  contarAssentosDisponiveis: (espacoId: UUID) =>
    api
      .get<CapacidadeEspaco>(`/setores/espaco/${espacoId}/capacidade`)
      .then((r) => r.data),
};

export type SetoresService = typeof setoresService;
