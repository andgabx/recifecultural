"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  comentariosService,
  type CurtirComentarioRequisicao,
  type PostarComentarioRequisicao,
} from "@/services/bff/comentarios";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  porEvento: (eventoId: UUID) => ["comentarios", "evento", eventoId] as const,
};

export function useComentarios(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? queryKeys.porEvento(eventoId)
      : (["comentarios", "evento", "vazio"] as const),
    queryFn: () => comentariosService.listarPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function usePostarComentario(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: PostarComentarioRequisicao) =>
      comentariosService.postar(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: queryKeys.porEvento(eventoId ?? variables.eventoId),
      });
    },
  });
}

export function useCurtirComentario(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: UUID; payload: CurtirComentarioRequisicao }) =>
      comentariosService.curtir(id, payload),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEvento(eventoId),
        });
      }
    },
  });
}

export function useDeletarComentario(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => comentariosService.deletar(id),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEvento(eventoId),
        });
      }
    },
  });
}
