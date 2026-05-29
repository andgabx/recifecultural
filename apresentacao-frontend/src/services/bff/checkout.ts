import { api } from "@/lib/api";
import type { BffCriado, MetodoPagamento, TipoIngresso, UUID } from "@/types/dominio";

export type CompraRequisicao = {
  eventoId: UUID;
  dataHoraApresentacao: string; // ISO LocalDateTime
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

export const checkoutService = {
  comprar: (payload: CompraRequisicao) =>
    api.post<BffCriado>("/checkout/comprar", payload).then((r) => r.data),

  comprarComCupom: (payload: CompraComCupomRequisicao) =>
    api
      .post<BffCriado>("/checkout/comprar-com-cupom", payload)
      .then((r) => r.data),
};

export type CheckoutService = typeof checkoutService;
