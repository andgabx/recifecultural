import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";
import type { StatusComentario } from "@/types/dominio";

export type ComentarioResumo = {
  id: UUID;
  espectadorId: UUID;
  eventoId: UUID;
  comentarioPaiId?: UUID;
  texto: string;
  nota?: number;
  status: StatusComentario;
  totalCurtidas: number;
  criadoEm: string; // ISO LocalDateTime
};

export type PostarComentarioRequisicao = {
  eventoId: UUID;
  espectadorId: UUID;
  texto: string;
  comentarioPaiId?: UUID;
};

export type CurtirComentarioRequisicao = {
  espectadorId: UUID;
};

export const comentariosService = {
  listarPorEvento: (eventoId: UUID) =>
    api
      .get<ComentarioResumo[]>(`/comentarios/evento/${eventoId}`)
      .then((r) => r.data),

  postar: (payload: PostarComentarioRequisicao) =>
    api.post<BffCriado>("/comentarios", payload).then((r) => r.data),

  curtir: (id: UUID, payload: CurtirComentarioRequisicao) =>
    api
      .post<BffSemConteudo>(`/comentarios/${id}/curtir`, payload)
      .then((r) => r.data),

  deletar: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/comentarios/${id}/deletar`)
      .then((r) => r.data),
};

export type ComentariosService = typeof comentariosService;
