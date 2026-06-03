import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";

export type BloqueioResumo = {
  id: UUID;
  espacoId: UUID;
  dataInicio: string;
  dataFim: string;
  justificativa: string;
  ativo: boolean;
  eventosCancelados: string[];
};

export type CriarBloqueioRequisicao = {
  espacoId: UUID;
  inicio: string;
  fim: string;
  justificativa: string;
};

export type EventoConflitante = {
  id: string;
  titulo: string;
  periodoInicio: string | null;
  periodoFim: string | null;
  totalEspectadores: number;
  totalReembolso: number;
};

export const bloqueiosService = {
  listarAtivos: () =>
    api.get<BloqueioResumo[]>("/bloqueios").then((r) => r.data),

  preview: (params: { espacoId: UUID; inicio: string; fim: string }) =>
    api
      .get<EventoConflitante[]>("/bloqueios/preview", { params })
      .then((r) => r.data),

  criar: (payload: CriarBloqueioRequisicao) =>
    api.post<BffCriado>("/bloqueios", payload).then((r) => r.data),

  desativar: (id: UUID, reativarEventos: boolean) =>
    api
      .post<BffSemConteudo>(`/bloqueios/${id}/desativar`, { reativarEventos })
      .then((r) => r.data),
};

export type BloqueiosService = typeof bloqueiosService;
