import { api } from "@/lib/api";
import type {
  BffCriado,
  BffSemConteudo,
  StatusEvento,
  UUID,
} from "@/types/dominio";

export type EventoResumo = {
  id: UUID;
  titulo: string;
  descricaoCurta?: string;
  status: StatusEvento;
  promotorId: UUID;
  localId?: UUID;
  categoria?: string;
  periodoInicio?: string;
  periodoFim?: string;
};

export type ApresentacaoResumo = {
  id: UUID;
  eventoId: UUID;
  dataHora: string;
};

export type EventoResumoExpandido = EventoResumo & {
  descricaoLonga?: string;
  /** Backend serializa BigDecimal como string. */
  precoInteira?: string;
  precoMeia?: string;
  precoSocial?: string;
  apresentacoes?: ApresentacaoResumo[];
};

export type ReprovarEventoRequisicao = { feedback: string };

export type CriarEventoRequisicao = {
  promotorId: UUID;
  localId?: UUID;
  titulo: string;
  descricaoCurta?: string;
  descricaoLonga?: string;
  periodoInicio: string; // ISO LocalDateTime
  periodoFim: string;
  categoria?: string;
  precoInteira?: number;
  precoMeia?: number;
  artistas?: UUID[];
  datasApresentacao?: string[]; // ISO LocalDateTime[]
};

export type EditarEventoRequisicao = Omit<CriarEventoRequisicao, "promotorId">;

export const eventosService = {
  listar: () => api.get<EventoResumo[]>("/eventos").then((r) => r.data),

  listarPorProdutor: (promotorId: UUID) =>
    api
      .get<EventoResumo[]>(`/eventos/produtor/${promotorId}`)
      .then((r) => r.data),

  buscar: (id: UUID) =>
    api.get<EventoResumoExpandido>(`/eventos/${id}`).then((r) => r.data),

  criar: (payload: CriarEventoRequisicao) =>
    api.post<BffCriado>("/eventos", payload).then((r) => r.data),

  editar: (id: UUID, payload: EditarEventoRequisicao) =>
    api.put<BffSemConteudo>(`/eventos/${id}`, payload).then((r) => r.data),

  submeter: (id: UUID) =>
    api.post<BffSemConteudo>(`/eventos/${id}/submeter`).then((r) => r.data),

  aprovar: (id: UUID) =>
    api.post<BffSemConteudo>(`/eventos/${id}/aprovar`).then((r) => r.data),

  reprovar: (id: UUID, payload: ReprovarEventoRequisicao) =>
    api
      .post<BffSemConteudo>(`/eventos/${id}/reprovar`, payload)
      .then((r) => r.data),
};

export type EventosService = typeof eventosService;
