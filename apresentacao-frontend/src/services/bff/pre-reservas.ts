import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";

export type PreReservaRequisicao = {
  setorId: UUID;
  assentoId: UUID;
  usuarioId: UUID;
};

export const preReservasService = {
  reservar: (payload: PreReservaRequisicao) =>
    api.post<BffCriado>("/pre-reservas", payload).then((r) => r.data),

  cancelar: (preReservaId: UUID) =>
    api.delete<BffSemConteudo>(`/pre-reservas/${preReservaId}`).then((r) => r.data),
};
