"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { cuponsService, type CriarCupomRequisicao } from "@/services/bff/cupons";
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
