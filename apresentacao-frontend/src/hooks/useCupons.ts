"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  cuponsService,
  type AplicarCupomRequisicao,
  type CriarCupomRequisicao,
  type PreviewCupomRequisicao,
} from "@/services/bff/cupons";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  listar: ["cupons"] as const,
};

export function useCupons() {
  return useQuery({
    queryKey: queryKeys.listar,
    queryFn: () => cuponsService.listar(),
  });
}

export function useCriarCupom() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarCupomRequisicao) => cuponsService.criar(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.listar });
    },
  });
}

export function useDeletarCupom() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => cuponsService.deletar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.listar });
    },
  });
}

export function useAplicarCupom() {
  return useMutation({
    mutationFn: (payload: AplicarCupomRequisicao) =>
      cuponsService.aplicar(payload),
  });
}

export function usePreviewCupom() {
  return useMutation({
    mutationFn: (payload: PreviewCupomRequisicao) =>
      cuponsService.preview(payload),
  });
}
