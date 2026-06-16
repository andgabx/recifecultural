import { useMutation } from '@tanstack/react-query';
import {
  inteligenciaService,
  PrevisaoReceitaReq,
  PrevisaoReceitaRes,
  PrevisaoNoShowReq,
  PrevisaoNoShowRes,
  AnaliseEventoResposta
} from '@/services/bff/inteligencia';

// Exportando os tipos para compatibilidade com outros arquivos (como page.tsx)
export type { PrevisaoReceitaRes, PrevisaoNoShowRes, AnaliseEventoResposta };

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