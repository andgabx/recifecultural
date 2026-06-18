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
  }
};