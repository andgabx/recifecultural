import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, StatusArtista, UUID } from "@/types/dominio";

export type ArtistaResumo = {
  id: UUID;
  produtorId: UUID;
  nome: string;
  status: StatusArtista;
};

export type CriarArtistaRequisicao = {
  produtorId: UUID;
  nome: string;
  riderTecnico?: string[];
};

export const artistasService = {
  listar: () => api.get<ArtistaResumo[]>("/artistas").then((r) => r.data),

  cadastrar: (payload: CriarArtistaRequisicao) =>
    api.post<BffCriado>("/artistas", payload).then((r) => r.data),

  inativar: (id: UUID) =>
    api.post<BffSemConteudo>(`/artistas/${id}/inativar`).then((r) => r.data),
};

export type ArtistasService = typeof artistasService;
