"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";

import { eventosService } from "@/services/bff/eventos";
import type { UUID } from "@/types/dominio";
import { eventosQueryKeys } from "@/hooks/useEventos";

export function useAprovarEvento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => eventosService.aprovar(id),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: eventosQueryKeys.todos() }),
  });
}

export function useReprovarEvento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, feedback }: { id: UUID; feedback: string }) =>
      eventosService.reprovar(id, { feedback }),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: eventosQueryKeys.todos() }),
  });
}

export function useSubmeterEvento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => eventosService.submeter(id),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: eventosQueryKeys.todos() }),
  });
}
