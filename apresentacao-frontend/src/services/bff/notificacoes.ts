import { api } from "@/lib/api";
import type { BffSemConteudo, UUID } from "@/types/dominio";

export type NotificacaoResumo = {
  id: UUID;
  usuarioAlvo?: UUID;
  mensagem: string;
  contexto: string;
  idReferencia?: UUID;
  foiLida: boolean;
  dataCriacao: string;
};

export const notificacoesService = {
  listar: (usuarioId: UUID, somenteNaoLidas = false) =>
    api
      .get<NotificacaoResumo[]>("/notificacoes", {
        params: { usuarioId, somenteNaoLidas },
      })
      .then((r) => r.data),

  marcarLida: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/notificacoes/${id}/marcar-lida`)
      .then((r) => r.data),

  marcarNaoLida: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/notificacoes/${id}/marcar-nao-lida`)
      .then((r) => r.data),
  broadcast: (payload: { mensagem: string; contexto: string; idReferencia?: UUID }) =>
    api
      .post<BffSemConteudo>("/notificacoes/broadcast", payload)
      .then((r) => r.data),
};

export type NotificacoesService = typeof notificacoesService;
