import { useMutation, useQuery } from '@tanstack/react-query';
import {
  inteligenciaService,
  PrevisaoReceitaReq,
  PrevisaoReceitaRes,
  PrevisaoNoShowReq,
  PrevisaoNoShowRes,
  AnaliseEventoResposta,
  VisitacaoPonto,
  NoshowPorGrupo,
  MetricasClassificador,
  ReceitaScatterPonto
} from '@/services/bff/inteligencia';

// Exportando os tipos para compatibilidade com outros arquivos (como page.tsx)
export type {
  PrevisaoReceitaRes,
  PrevisaoNoShowRes,
  AnaliseEventoResposta,
  VisitacaoPonto,
  NoshowPorGrupo,
  MetricasClassificador,
  ReceitaScatterPonto
};

export function usePreverReceita() {
  return useMutation<PrevisaoReceitaRes, Error, PrevisaoReceitaReq>({
    mutationFn: (form: PrevisaoReceitaReq) => inteligenciaService.preverReceita(form),
  });
}

export function usePreverNoShow() {
  return useMutation<PrevisaoNoShowRes, Error, PrevisaoNoShowReq>({
    mutationFn: async (form: PrevisaoNoShowReq) => {
        const response = await inteligenciaService.preverNoShow(form);
        // Map backend field to frontend expected field if needed
        return {
            ...response,
            seraFalta: response.alertaAltoRisco
        };
    }
  });
}

export function useAnalisarEvento() {
  return useMutation<AnaliseEventoResposta, Error, string>({
    mutationFn: (eventoId: string) => inteligenciaService.analisarEvento(eventoId),
  });
}

export function useVisitacao() {
  return useQuery({
    queryKey: ['inteligencia', 'visitacao'],
    queryFn: () => inteligenciaService.listarVisitacao(),
    staleTime: 5 * 60_000,
  });
}

export function useNoshowPorGrupo() {
  return useQuery({
    queryKey: ['inteligencia', 'noshow-por-grupo'],
    queryFn: () => inteligenciaService.buscarNoshowPorGrupo(),
    staleTime: 5 * 60_000,
  });
}

export function useMetricasClassificador() {
  return useQuery({
    queryKey: ['inteligencia', 'metricas-classificador'],
    queryFn: () => inteligenciaService.buscarMetricasClassificador(),
    staleTime: 5 * 60_000,
  });
}

export function useReceitaScatter() {
  return useQuery({
    queryKey: ['inteligencia', 'receita-scatter'],
    queryFn: () => inteligenciaService.listarReceitaScatter(),
    staleTime: 5 * 60_000,
  });
}