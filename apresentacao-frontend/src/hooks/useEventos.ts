"use client";

import { useQuery } from "@tanstack/react-query";

import { eventosService } from "@/services/bff/eventos";
import type { StatusEvento, UUID } from "@/types/dominio";

export const eventosQueryKeys = {
  todos: () => ["eventos"] as const,
  detalhe: (id: UUID) => ["eventos", id] as const,
};

type FiltroEventos = {
  /** Filtra a lista por um ou mais status. Sem filtro = todos. */
  status?: StatusEvento | StatusEvento[];
};

export function useEventos(filtros?: FiltroEventos) {
  const statusFiltro = filtros?.status
    ? Array.isArray(filtros.status)
      ? filtros.status
      : [filtros.status]
    : undefined;

  return useQuery({
    queryKey: statusFiltro
      ? (["eventos", { status: statusFiltro }] as const)
      : eventosQueryKeys.todos(),
    queryFn: async () => {
      const eventos = await eventosService.listar();
      if (!statusFiltro) return eventos;
      return eventos.filter((e) => statusFiltro.includes(e.status));
    },
  });
}

export function useEvento(id: UUID | undefined) {
  return useQuery({
    queryKey: id ? eventosQueryKeys.detalhe(id) : ["eventos", "vazio"],
    queryFn: () => eventosService.buscar(id as UUID),
    enabled: Boolean(id),
  });
}
