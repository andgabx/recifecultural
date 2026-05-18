"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  produtoresService,
  type CriarProdutorRequisicao,
} from "@/services/bff/produtores";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  todos: ["produtores"] as const,
};

export function useProdutores() {
  return useQuery({
    queryKey: queryKeys.todos,
    queryFn: () => produtoresService.listar(),
  });
}

export function useCadastrarProdutor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarProdutorRequisicao) =>
      produtoresService.cadastrar(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useSuspenderProdutor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => produtoresService.suspender(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useReativarProdutor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => produtoresService.reativar(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useInativarProdutor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => produtoresService.inativar(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}
