"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  eventosService,
  type CriarEventoRequisicao,
  type EditarEventoRequisicao,
} from "@/services/bff/eventos";
import type { UUID } from "@/types/dominio";
import { eventosQueryKeys } from "@/hooks/useEventos";

export function useEventosPorProdutor(promotorId: UUID | undefined) {
  return useQuery({
    queryKey: promotorId
      ? (["eventos", "produtor", promotorId] as const)
      : (["eventos", "produtor", "vazio"] as const),
    queryFn: () => eventosService.listarPorProdutor(promotorId as UUID),
    enabled: Boolean(promotorId),
  });
}

export function useCriarEvento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarEventoRequisicao) =>
      eventosService.criar(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: eventosQueryKeys.todos() });
      queryClient.invalidateQueries({
        queryKey: ["eventos", "produtor", variables.promotorId],
      });
    },
  });
}

export function useEditarEvento(promotorId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: UUID; payload: EditarEventoRequisicao }) =>
      eventosService.editar(id, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: eventosQueryKeys.todos() });
      queryClient.invalidateQueries({
        queryKey: eventosQueryKeys.detalhe(variables.id),
      });
      if (promotorId) {
        queryClient.invalidateQueries({
          queryKey: ["eventos", "produtor", promotorId],
        });
      }
    },
  });
}
