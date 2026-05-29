"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  patrociniosService,
  type CriarPatrocinioRequisicao,
} from "@/services/bff/patrocinios";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  porEvento: (eventoId: UUID) => ["patrocinios", "evento", eventoId] as const,
  subsidio: (id: UUID, preco: number) =>
    ["patrocinios", "subsidio", id, preco] as const,
};

export function usePatrociniosPorEvento(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? queryKeys.porEvento(eventoId)
      : (["patrocinios", "evento", "vazio"] as const),
    queryFn: () => patrociniosService.listarPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function useCriarPatrocinio(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarPatrocinioRequisicao) =>
      patrociniosService.criar(payload),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
      }
    },
  });
}

export function useAtivarPatrocinio(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => patrociniosService.ativar(id),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
      }
    },
  });
}

export function useCancelarPatrocinioPorEvento(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => patrociniosService.cancelarPorEvento(id),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
      }
    },
  });
}

export function useCancelarPatrocinioPorPatrocinador(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => patrociniosService.cancelarPorPatrocinador(id),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
      }
    },
  });
}

export function useCalcularSubsidio(id: UUID, precoSocialAtual: number) {
  return useQuery({
    queryKey: queryKeys.subsidio(id, precoSocialAtual),
    queryFn: () => patrociniosService.calcularSubsidio(id, precoSocialAtual),
    enabled: Boolean(id) && precoSocialAtual > 0,
  });
}
