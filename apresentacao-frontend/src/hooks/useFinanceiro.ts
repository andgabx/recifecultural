"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  financeiroService,
  type RegistrarDespesaRequisicao,
} from "@/services/bff/financeiro";

const queryKeys = {
  indicadores: (periodoInicio: string, periodoFim: string) =>
    ["financeiro", "indicadores", periodoInicio, periodoFim] as const,
};

export function useIndicadoresFinanceiros(
  periodoInicio: string | undefined,
  periodoFim: string | undefined,
) {
  return useQuery({
    queryKey: periodoInicio && periodoFim
      ? queryKeys.indicadores(periodoInicio, periodoFim)
      : (["financeiro", "indicadores", "vazio"] as const),
    queryFn: () =>
      financeiroService.indicadores({
        periodoInicio: periodoInicio as string,
        periodoFim: periodoFim as string,
      }),
    enabled: Boolean(periodoInicio && periodoFim),
  });
}

export function useRegistrarDespesa() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: RegistrarDespesaRequisicao) =>
      financeiroService.registrarDespesa(payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: ["financeiro"] }),
  });
}
