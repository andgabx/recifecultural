import { api } from "@/lib/api";
import type { BffCriado, BffSemConteudo, UUID } from "@/types/dominio";

import type { TipoDesconto } from "@/types/dominio";
export type { TipoDesconto } from "@/types/dominio";

export type AplicarCupomRequisicao = {
  codigoCupom: string;
  cpfComprador: string;
  categoriaEvento: string;
  valorPedido: number;
};

export type ResultadoAplicacaoCupom = {
  valorComDesconto: number;
  descontoAplicado: number;
  aplicavel: boolean;
  motivo?: string;
};

export type CupomResumo = {
  id: UUID;
  codigo: string;
  tipoDesconto: TipoDesconto;
  valorDesconto: number;
  valorMinimoPedido: number;
  limiteGlobal: number;
  usosGlobais: number;
  limitePorCpf: number;
  dataInicio: string;
  dataFim: string;
  categoriaPermitida: string | null;
};

export type CriarCupomRequisicao = {
  codigo: string;
  tipoDesconto: TipoDesconto;
  valorDesconto: number;
  valorMinimoPedido: number;
  limiteGlobal: number;
  limitePorCpf: number;
  dataInicio: string;
  dataFim: string;
  categoriaPermitida?: string | null;
};

export type PreviewCupomRequisicao = {
  codigo: string;
  cpf: string;
  valor: number;
  categoria: string;
};

export type PreviewCupomResposta = {
  tipoDesconto: TipoDesconto;
  configuracaoDesconto: number;
  descontoCalculado: number;
  valorFinal: number;
};

export const cuponsService = {
  aplicar: (payload: AplicarCupomRequisicao) =>
    api
      .post<ResultadoAplicacaoCupom>("/cupons/aplicar", payload)
      .then((r) => r.data),

  preview: (payload: PreviewCupomRequisicao) =>
    api.post<PreviewCupomResposta>("/cupons/preview", payload).then((r) => r.data),

  listar: () => api.get<CupomResumo[]>("/cupons").then((r) => r.data),

  criar: (payload: CriarCupomRequisicao) =>
    api.post<BffCriado>("/cupons", payload).then((r) => r.data),

  deletar: (id: UUID) =>
    api.delete<BffSemConteudo>(`/cupons/${id}`).then((r) => r.data),
};

export type CuponsService = typeof cuponsService;
