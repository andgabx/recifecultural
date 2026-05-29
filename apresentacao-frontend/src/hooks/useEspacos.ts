"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  espacosService,
  type AtualizarCapacidadeRequisicao,
  type CriarEspacoRequisicao,
} from "@/services/bff/espacos";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  todos: ["espacos"] as const,
};

export function useEspacos() {
  return useQuery({
    queryKey: queryKeys.todos,
    queryFn: () => espacosService.listar(),
  });
}

export function useCadastrarEspaco() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarEspacoRequisicao) =>
      espacosService.criar(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useAtualizarCapacidade() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: UUID;
      payload: AtualizarCapacidadeRequisicao;
    }) => espacosService.atualizarCapacidade(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useInterditarEspaco() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => espacosService.interditar(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}
