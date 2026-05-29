import { api } from "@/lib/api";
import type { UUID } from "@/types/dominio";

export type ResultadoValidacaoCatraca = {
  liberado: boolean;
  motivo?: string;
  ingressoId?: UUID;
};

export type ValidarAcessoRequisicao = {
  codigoQr: string;
  portaoAcesso: string;
  horario: string; // ISO LocalDateTime
};

export const catracaService = {
  validar: (payload: ValidarAcessoRequisicao) =>
    api
      .post<ResultadoValidacaoCatraca>("/catraca/validar", payload)
      .then((r) => r.data),
};

export type CatracaService = typeof catracaService;
