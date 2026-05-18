import { api } from "@/lib/api";
import type { BffCriado, UUID } from "@/types/dominio";

export type IndicadoresFinanceiros = {
  ocupacao: number;
  receitaBruta: number;
  receitaLiquida: number;
  totalDespesas: number;
};

export type RegistrarDespesaRequisicao = {
  orcamentoId: UUID;
  descricao: string;
  valor: number;
  categoria: string;
};

export type ResultadoDespesa = {
  id: UUID;
  alertaOrcamento: boolean;
  saldoRestante: number;
};

export const financeiroService = {
  indicadores: (params: { periodoInicio: string; periodoFim: string }) =>
    api
      .get<IndicadoresFinanceiros>("/financeiro/indicadores", { params })
      .then((r) => r.data),

  registrarDespesa: (payload: RegistrarDespesaRequisicao) =>
    api
      .post<ResultadoDespesa>("/financeiro/despesas", payload)
      .then((r) => r.data),
};

export type FinanceiroService = typeof financeiroService;
