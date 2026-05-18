"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  checkoutService,
  type CompraComCupomRequisicao,
  type CompraRequisicao,
} from "@/services/bff/checkout";
import { cuponsService, type AplicarCupomRequisicao } from "@/services/bff/cupons";
import {
  ingressosService,
  type IngressoResumo,
} from "@/services/bff/ingressos";
import type { MetodoPagamento, UUID } from "@/types/dominio";

export const ingressosQueryKeys = {
  porEvento: (eventoId: UUID) => ["ingressos", "evento", eventoId] as const,
  estrategia: (metodo: MetodoPagamento) =>
    ["ingressos", "estrategia", metodo] as const,
};

export function useComprar() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompraRequisicao) => checkoutService.comprar(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ingressosQueryKeys.porEvento(variables.eventoId),
      });
    },
  });
}

export function useComprarComCupom() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompraComCupomRequisicao) =>
      checkoutService.comprarComCupom(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ingressosQueryKeys.porEvento(variables.eventoId),
      });
    },
  });
}

export function useIngressosPorEvento(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? ingressosQueryKeys.porEvento(eventoId)
      : ["ingressos", "vazio"],
    queryFn: () => ingressosService.listarPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function useReembolso(eventoId: UUID | undefined) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => ingressosService.reembolsar(id),
    onSuccess: () => {
      if (eventoId) {
        queryClient.invalidateQueries({
          queryKey: ingressosQueryKeys.porEvento(eventoId),
        });
      }
    },
  });
}

export function useEstrategiaReembolso(metodo: MetodoPagamento | undefined) {
  return useQuery({
    queryKey: metodo
      ? ingressosQueryKeys.estrategia(metodo)
      : ["ingressos", "estrategia", "vazio"],
    queryFn: () =>
      ingressosService.consultarEstrategiaReembolso(metodo as MetodoPagamento),
    enabled: Boolean(metodo),
  });
}

export function useAplicarCupom() {
  return useMutation({
    mutationFn: (payload: AplicarCupomRequisicao) => cuponsService.aplicar(payload),
  });
}

export type { IngressoResumo };
