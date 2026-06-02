import { api } from "@/lib/api";
import type { BffCriado, MetodoPagamento, TipoIngresso, UUID } from "@/types/dominio";

export type CompraRequisicao = {
  eventoId: UUID;
  dataHoraApresentacao: string;
  tipo: TipoIngresso;
  valor: number;
  metodoPagamento: MetodoPagamento;
  capacidadeMaxima: number;
};

export type CompraComCupomRequisicao = CompraRequisicao & {
  codigoCupom: string;
  cpfComprador: string;
  categoriaEvento: string;
};

export type CompraComPreReservaRequisicao = CompraRequisicao & {
  preReservaId: UUID;
  assentoId: UUID;
};

export type ItemCompraMultipla = {
  preReservaId: UUID;
  assentoId: UUID;
  tipo: TipoIngresso;
  valor: number;
};

export type CompraMultiplaRequisicao = {
  eventoId: UUID;
  dataHoraApresentacao: string;
  metodoPagamento: MetodoPagamento;
  capacidadeMaxima: number;
  itens: ItemCompraMultipla[];
  codigoCupom?: string;
  cpfComprador?: string;
  categoriaEvento?: string;
};

export type CompraMultiplaResposta = {
  ids: UUID[];
  total: number;
};

export const checkoutService = {
  comprar: (payload: CompraRequisicao) =>
    api.post<BffCriado>("/checkout/comprar", payload).then((r) => r.data),

  comprarComCupom: (payload: CompraComCupomRequisicao) =>
    api.post<BffCriado>("/checkout/comprar-com-cupom", payload).then((r) => r.data),

  comprarComPreReserva: (payload: CompraComPreReservaRequisicao) =>
    api.post<BffCriado>("/checkout/comprar-com-prereserva", payload).then((r) => r.data),

  comprarMultiplos: (payload: CompraMultiplaRequisicao) =>
    api.post<CompraMultiplaResposta>("/checkout/comprar-multiplos", payload).then((r) => r.data),
};

export type CheckoutService = typeof checkoutService;

