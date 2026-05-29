import { api } from "@/lib/api";
import type {
  BffCriado,
  BffSemConteudo,
  StatusRecursoAcessibilidade,
  TipoRecursoAcessibilidade,
  UUID,
} from "@/types/dominio";

export type RecursoAcessibilidade = {
  id: UUID;
  apresentacaoId: UUID;
  eventoId: UUID;
  tipo: TipoRecursoAcessibilidade;
  status: StatusRecursoAcessibilidade;
  justificativaRemocao?: string | null;
};

export type MarcarRecursoRequisicao = {
  apresentacaoId: UUID;
  eventoId: UUID;
  tipo: TipoRecursoAcessibilidade;
};

export const acessibilidadeService = {
  listarTodos: () =>
    api.get<RecursoAcessibilidade[]>("/acessibilidade").then((r) => r.data),

  listarPorEvento: (eventoId: UUID) =>
    api
      .get<RecursoAcessibilidade[]>(`/acessibilidade/evento/${eventoId}`)
      .then((r) => r.data),

  listarAtivosPorEvento: (eventoId: UUID) =>
    api
      .get<RecursoAcessibilidade[]>(`/acessibilidade/evento/${eventoId}/ativos`)
      .then((r) => r.data),

  marcar: (payload: MarcarRecursoRequisicao) =>
    api.post<BffCriado>("/acessibilidade", payload).then((r) => r.data),

  remover: (id: UUID, justificativa: string) =>
    api
      .delete<BffSemConteudo>(`/acessibilidade/${id}`, {
        data: { justificativa },
      })
      .then((r) => r.data),
};

export type AcessibilidadeService = typeof acessibilidadeService;
