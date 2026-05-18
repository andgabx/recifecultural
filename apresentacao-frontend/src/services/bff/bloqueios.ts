import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";

export type BloqueioResumo = {
  id: UUID;
  espacoId: UUID;
  dataInicio: string; // ISO date
  dataFim: string;
  justificativa: string;
  ativo: boolean;
};

export type CriarBloqueioRequisicao = {
  espacoId: UUID;
  dataInicio: string;
  dataFim: string;
  justificativa: string;
};

export const bloqueiosService = {
  listarAtivos: () =>
    api.get<BloqueioResumo[]>("/bloqueios").then((r) => r.data),

  criar: (payload: CriarBloqueioRequisicao) =>
    api.post<BffCriado>("/bloqueios", payload).then((r) => r.data),

  desativar: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/bloqueios/${id}/desativar`)
      .then((r) => r.data),
};

export type BloqueiosService = typeof bloqueiosService;
