"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  equipamentosService,
  type AdquirirEquipamentoRequisicao,
} from "@/services/bff/equipamentos";
import type { DisponibilidadeEquipamento, UUID } from "@/types/dominio";

const queryKeys = {
  porEspaco: (espacoId: UUID) => ["equipamentos", "espaco", espacoId] as const,
};

export function useEquipamentosPorEspaco(espacoId: UUID | undefined) {
  return useQuery({
    queryKey: espacoId
      ? queryKeys.porEspaco(espacoId)
      : (["equipamentos", "espaco", "vazio"] as const),
    queryFn: () => equipamentosService.listarPorEspaco(espacoId as UUID),
    enabled: Boolean(espacoId),
  });
}

function invalidateEspaco(
  queryClient: ReturnType<typeof useQueryClient>,
  espacoId?: UUID,
) {
  if (espacoId) {
    queryClient.invalidateQueries({ queryKey: queryKeys.porEspaco(espacoId) });
  } else {
    queryClient.invalidateQueries({ queryKey: ["equipamentos"] });
  }
}

export function useAdquirirEquipamento(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: AdquirirEquipamentoRequisicao) =>
      equipamentosService.adquirir(payload),
    onSuccess: () => invalidateEspaco(queryClient, espacoId),
  });
}

export function useMarcarManutencao(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => equipamentosService.marcarManutencao(id),
    onSuccess: () => invalidateEspaco(queryClient, espacoId),
  });
}

export function useLiberarEquipamento(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => equipamentosService.liberar(id),
    onSuccess: () => invalidateEspaco(queryClient, espacoId),
  });
}

export function useRemoverEquipamento(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => equipamentosService.remover(id),
    onSuccess: () => invalidateEspaco(queryClient, espacoId),
  });
}

export function useVerificarDisponibilidade(
  espacoId: string | undefined,
  nome: string,
  quantidade: number,
  inicio?: string,
  fim?: string,
): {
  disponivel: boolean | undefined;
  quantidadeDisponivel: number | undefined;
  isLoading: boolean;
} {
  const query = useQuery<DisponibilidadeEquipamento>({
    queryKey: [
      "equipamentos",
      "disponibilidade",
      espacoId,
      nome,
      quantidade,
      inicio ?? null,
      fim ?? null,
    ] as const,
    queryFn: () =>
      equipamentosService.verificarDisponibilidade(
        espacoId as string,
        nome,
        quantidade,
        inicio,
        fim,
      ),
    enabled: Boolean(espacoId) && nome.trim().length > 0 && quantidade >= 1,
    staleTime: 30_000,
  });

  return {
    disponivel: query.data?.disponivel,
    quantidadeDisponivel: query.data?.quantidadeDisponivel,
    isLoading: query.isLoading,
  };
}
