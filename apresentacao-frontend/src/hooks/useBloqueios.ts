"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  bloqueiosService,
  type CriarBloqueioRequisicao,
} from "@/services/bff/bloqueios";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  ativos: ["bloqueios", "ativos"] as const,
};

export function useBloqueios() {
  return useQuery({
    queryKey: queryKeys.ativos,
    queryFn: () => bloqueiosService.listarAtivos(),
  });
}

export function useCadastrarBloqueio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarBloqueioRequisicao) =>
      bloqueiosService.criar(payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: queryKeys.ativos }),
  });
}

export function useDesativarBloqueio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => bloqueiosService.desativar(id),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: queryKeys.ativos }),
  });
}
