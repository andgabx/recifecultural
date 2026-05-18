"use client";

import { useMemo } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  acessibilidadeService,
  type MarcarRecursoRequisicao,
  type RecursoAcessibilidade,
} from "@/services/bff/acessibilidade";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  todos: ["acessibilidade"] as const,
  porEvento: (eventoId: UUID) =>
    ["acessibilidade", "evento", eventoId] as const,
  ativosPorEvento: (eventoId: UUID) =>
    ["acessibilidade", "evento", eventoId, "ativos"] as const,
};

export function useRecursosAcessibilidade() {
  return useQuery({
    queryKey: queryKeys.todos,
    queryFn: () => acessibilidadeService.listarTodos(),
  });
}

/**
 * Agrupa todos os recursos confirmados em um Map<eventoId, recursos[]>.
 * Reusa o mesmo cache de `useRecursosAcessibilidade` — uma única chamada
 * de rede serve home, EventCard, detalhe e listagem do produtor.
 */
export function useRecursosConfirmadosPorEvento() {
  const query = useRecursosAcessibilidade();
  const mapa = useMemo(() => {
    const m = new Map<UUID, RecursoAcessibilidade[]>();
    for (const r of query.data ?? []) {
      if (r.status !== "CONFIRMADO") continue;
      const lista = m.get(r.eventoId) ?? [];
      lista.push(r);
      m.set(r.eventoId, lista);
    }
    return m;
  }, [query.data]);
  return { ...query, mapa };
}

export function useRecursosPorEvento(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? queryKeys.porEvento(eventoId)
      : (["acessibilidade", "evento", "vazio"] as const),
    queryFn: () => acessibilidadeService.listarPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function useRecursosAtivosPorEvento(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? queryKeys.ativosPorEvento(eventoId)
      : (["acessibilidade", "evento", "vazio", "ativos"] as const),
    queryFn: () =>
      acessibilidadeService.listarAtivosPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function useMarcarRecurso() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: MarcarRecursoRequisicao) =>
      acessibilidadeService.marcar(payload),
    onSuccess: (_, vars) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.todos });
      queryClient.invalidateQueries({
        queryKey: queryKeys.porEvento(vars.eventoId),
      });
      queryClient.invalidateQueries({
        queryKey: queryKeys.ativosPorEvento(vars.eventoId),
      });
    },
  });
}

export function useRemoverRecurso() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      justificativa,
    }: {
      id: UUID;
      justificativa: string;
    }) => acessibilidadeService.remover(id, justificativa),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["acessibilidade"] });
    },
  });
}
