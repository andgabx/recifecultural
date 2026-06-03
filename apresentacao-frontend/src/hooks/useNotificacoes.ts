"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { notificacoesService } from "@/services/bff/notificacoes";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  usuario: (usuarioId: UUID, somenteNaoLidas: boolean) =>
    ["notificacoes", usuarioId, { somenteNaoLidas }] as const,
};

export function useNotificacoes(
  usuarioId: UUID | undefined,
  somenteNaoLidas = false,
) {
  return useQuery({
    queryKey: usuarioId
      ? queryKeys.usuario(usuarioId, somenteNaoLidas)
      : (["notificacoes", "vazio"] as const),
    queryFn: () => notificacoesService.listar(usuarioId as UUID, somenteNaoLidas),
    enabled: Boolean(usuarioId),
  });
}

export function useEnviarBroadcast() {
  return useMutation({
    mutationFn: (payload: { mensagem: string; contexto: string; idReferencia?: UUID }) =>
      notificacoesService.broadcast(payload),
  });
}

export function useMarcarNotificacaoLida(usuarioId: UUID | undefined) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => notificacoesService.marcarLida(id),
    onSuccess: () => {
      if (usuarioId) {
        queryClient.invalidateQueries({
          queryKey: ["notificacoes", usuarioId],
        });
      }
    },
  });
}
