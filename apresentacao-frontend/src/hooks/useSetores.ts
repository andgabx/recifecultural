"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  setoresService,
  type ConfigurarSetorRequisicao,
  type EditarSetorRequisicao,
} from "@/services/bff/setores";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  porEspaco: (espacoId: UUID) => ["setores", "espaco", espacoId] as const,
  capacidade: (espacoId: UUID) => ["setores", "capacidade", espacoId] as const,
};

export function useSetoresPorEspaco(espacoId: UUID | undefined) {
  return useQuery({
    queryKey: espacoId
      ? queryKeys.porEspaco(espacoId)
      : (["setores", "espaco", "vazio"] as const),
    queryFn: () => setoresService.listarPorEspaco(espacoId as UUID),
    enabled: Boolean(espacoId),
  });
}

export function useCapacidadeEspaco(espacoId: UUID | undefined) {
  return useQuery({
    queryKey: espacoId
      ? queryKeys.capacidade(espacoId)
      : (["setores", "capacidade", "vazio"] as const),
    queryFn: () => setoresService.contarAssentosDisponiveis(espacoId as UUID),
    enabled: Boolean(espacoId),
  });
}

export function useConfigurarSetor(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: ConfigurarSetorRequisicao) =>
      setoresService.configurar(payload),
    onSuccess: () => {
      if (espacoId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEspaco(espacoId),
        });
        queryClient.invalidateQueries({
          queryKey: queryKeys.capacidade(espacoId),
        });
      }
    },
  });
}

export function useEditarSetor(espacoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: UUID; payload: EditarSetorRequisicao }) =>
      setoresService.editar(id, payload),
    onSuccess: () => {
      if (espacoId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEspaco(espacoId),
        });
        queryClient.invalidateQueries({
          queryKey: queryKeys.capacidade(espacoId),
        });
      }
    },
  });
}
