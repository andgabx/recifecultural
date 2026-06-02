"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  checkoutService,
  type CompraComCupomRequisicao,
  type CompraComPreReservaRequisicao,
  type CompraMultiplaRequisicao,
  type CompraRequisicao,
} from "@/services/bff/checkout";
import { cuponsService, type AplicarCupomRequisicao, type PreviewCupomRequisicao } from "@/services/bff/cupons";
import {
  ingressosService,
  type IngressoResumo,
} from "@/services/bff/ingressos";
import { preReservasService, type PreReservaRequisicao } from "@/services/bff/pre-reservas";
import type { MetodoPagamento, UUID } from "@/types/dominio";

export const ingressosQueryKeys = {
  todos: ["ingressos", "todos"] as const,
  porEvento: (eventoId: UUID) => ["ingressos", "evento", eventoId] as const,
  estrategia: (metodo: MetodoPagamento) =>
    ["ingressos", "estrategia", metodo] as const,
};

export function useTodosIngressos() {
  return useQuery({
    queryKey: ingressosQueryKeys.todos,
    queryFn: () => ingressosService.listarTodos(),
  });
}

export function useComprar() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompraRequisicao) => checkoutService.comprar(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ingressosQueryKeys.todos });
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
      queryClient.invalidateQueries({ queryKey: ingressosQueryKeys.todos });
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

export function useComprarComPreReserva() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompraComPreReservaRequisicao) =>
      checkoutService.comprarComPreReserva(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ingressosQueryKeys.todos });
      queryClient.invalidateQueries({
        queryKey: ingressosQueryKeys.porEvento(variables.eventoId),
      });
    },
  });
}

export function useComprarMultiplos() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CompraMultiplaRequisicao) =>
      checkoutService.comprarMultiplos(payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ingressosQueryKeys.todos });
      queryClient.invalidateQueries({
        queryKey: ingressosQueryKeys.porEvento(variables.eventoId),
      });
      // Invalida o mapa de assentos para que os OCUPADOS apareçam corretamente
      queryClient.invalidateQueries({ queryKey: ["setores"] });
    },
  });
}

export function useReservarAssento() {
  return useMutation({
    mutationFn: (payload: PreReservaRequisicao) =>
      preReservasService.reservar(payload),
  });
}

export function useCancelarPreReserva() {
  return useMutation({
    mutationFn: (preReservaId: UUID) => preReservasService.cancelar(preReservaId),
  });
}

export function useAplicarCupom() {
  return useMutation({
    mutationFn: (payload: AplicarCupomRequisicao) => cuponsService.aplicar(payload),
  });
}

export function usePreviewCupom() {
  return useMutation({
    mutationFn: (payload: PreviewCupomRequisicao) => cuponsService.preview(payload),
  });
}

export type { IngressoResumo };
