import { api } from '@/lib/api';

export interface PrevisaoReceitaReq {
  orcamentoMarketing: number;
  patrocinio: number;
}

export interface PrevisaoReceitaRes {
  investimentoTotal: number;
  receitaEstimada: number;
}

export interface PrevisaoNoShowReq {
  ingressoId: string;
  antecedenciaCompraDias: number;
  previsaoClima: string;
}

export interface PrevisaoNoShowRes {
  ingressoId: string;
  probabilidadeFalta: number;
  alertaAltoRisco: boolean;
  seraFalta?: boolean; // Compatibilidade com a view
}

export interface AnaliseEventoResposta {
  receitaProjetada: number;
  taxaOcupacaoEsperada: number;
  publicoAlvoMaiorAdesao: string;
  riscoCancelamento: 'ALTO' | 'MEDIO' | 'BAIXO';
}

export interface VisitacaoPonto {
  teatro: string;
  mes: number;
  visitantes: number;
}

export interface NoshowPorGrupo {
  porTipo: { tipo: string; mediaNoShow: number; pctAltoRisco: number; }[];
  porFaixaPreco: { faixa: string; pctAltoRisco: number; }[];
  porCategoria: { categoria: string; pctAltoRisco: number; }[];
}

export interface MetricasClassificador {
  acuracia: number;
  precisao: number;
  recall: number;
  f1: number;
  aucRoc: number;
  averagePrecision: number;
  confusaoMatrix: number[][];
  featureImportance: { feature: string; importancia: number; }[];
  rocCurve: { fpr: number; tpr: number; }[];
  prCurve: { recall: number; precisao: number; }[];
}

export interface ReceitaScatterPonto {
  precoEfetivo: number;
  receitaReal: number;
  capacidade: number;
  categoria: string;
}

export const inteligenciaService = {
  preverReceita: async (data: PrevisaoReceitaReq): Promise<PrevisaoReceitaRes> => {
    const response = await api.post('/inteligencia/prever-receita', data);
    return response.data;
  },
  preverNoShow: async (data: PrevisaoNoShowReq): Promise<PrevisaoNoShowRes> => {
    const response = await api.post('/inteligencia/prever-noshow', data);
    return response.data;
  },
  analisarEvento: async (eventoId: string): Promise<AnaliseEventoResposta> => {
    const response = await api.get(`/inteligencia/analisar-evento/${eventoId}`);
    return response.data;
  },
  listarVisitacao: async (): Promise<VisitacaoPonto[]> => {
    const response = await api.get('/inteligencia/visitacao');
    return response.data;
  },
  buscarNoshowPorGrupo: async (): Promise<NoshowPorGrupo> => {
    const response = await api.get('/inteligencia/noshow-por-grupo');
    return response.data;
  },
  buscarMetricasClassificador: async (): Promise<MetricasClassificador> => {
    const response = await api.get('/inteligencia/metricas-classificador');
    return response.data;
  },
  listarReceitaScatter: async (): Promise<ReceitaScatterPonto[]> => {
    const response = await api.get('/inteligencia/receita-scatter');
    return response.data;
  }
};