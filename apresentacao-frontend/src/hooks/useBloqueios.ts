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

export function usePreviewBloqueio() {
  return useMutation({
    mutationFn: (params: { espacoId: UUID; inicio: string; fim: string }) =>
      bloqueiosService.preview(params),
  });
}

export function useCadastrarBloqueio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarBloqueioRequisicao) =>
      bloqueiosService.criar(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.ativos });
      queryClient.invalidateQueries({ queryKey: ["espacos"] });
    },
  });
}

export function useDesativarBloqueio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, reativarEventos }: { id: UUID; reativarEventos: boolean }) =>
      bloqueiosService.desativar(id, reativarEventos),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.ativos });
      queryClient.invalidateQueries({ queryKey: ["espacos"] });
    },
  });
}
