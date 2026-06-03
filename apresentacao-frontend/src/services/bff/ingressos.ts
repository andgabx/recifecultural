import { api } from "@/lib/api";
import type {
  BffSemConteudo,
  MetodoPagamento,
  StatusIngresso,
  TipoIngresso,
  UUID,
} from "@/types/dominio";

export type IngressoResumo = {
  id: UUID;
  eventoId: UUID;
  assentoId?: UUID;
  status: StatusIngresso;
  tipo: TipoIngresso;
  valorPago: number;
  metodoPagamento: MetodoPagamento;
  codigoQr: string;
  dataCompra: string;
  dataHoraApresentacao: string;
};

export type SimulacaoReembolsoResposta = {
  prazo: string;
  descricao: string;
  metodoPagamento: MetodoPagamento;
};

export const ingressosService = {
  listarTodos: () =>
    api.get<IngressoResumo[]>("/meus-ingressos").then((r) => r.data),

  listarPorEvento: (eventoId: UUID) =>
    api
      .get<IngressoResumo[]>(`/meus-ingressos/evento/${eventoId}`)
      .then((r) => r.data),

  reembolsar: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/meus-ingressos/${id}/reembolso`)
      .then((r) => r.data),

  consultarEstrategiaReembolso: (metodoPagamento: MetodoPagamento) =>
    api
      .get<SimulacaoReembolsoResposta>("/meus-ingressos/reembolso/estrategia", {
        params: { metodoPagamento },
      })
      .then((r) => r.data),
};

export type IngressosService = typeof ingressosService;
