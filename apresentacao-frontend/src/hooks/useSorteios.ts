"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  sorteiosService,
  type CriarSorteioRequisicao,
} from "@/services/bff/sorteios";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  abertos: ["sorteios", "abertos"] as const,
  porEvento: (eventoId: UUID) => ["sorteios", "evento", eventoId] as const,
  porEspectador: (espectadorId: UUID) =>
    ["sorteios", "espectador", espectadorId] as const,
  inscricoes: (sorteioId: UUID) => ["sorteios", sorteioId, "inscricoes"] as const,
};

function invalidarSorteiosPorEvento(queryClient: ReturnType<typeof useQueryClient>, eventoId: UUID | undefined) {
  if (eventoId) {
    queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
  }
}

export function useSorteiosAbertos() {
  return useQuery({
    queryKey: queryKeys.abertos,
    queryFn: () => sorteiosService.listarAbertos(),
  });
}

export function useSorteiosPorEvento(eventoId: UUID | undefined) {
  return useQuery({
    queryKey: eventoId
      ? queryKeys.porEvento(eventoId)
      : (["sorteios", "evento", "vazio"] as const),
    queryFn: () => sorteiosService.listarPorEvento(eventoId as UUID),
    enabled: Boolean(eventoId),
  });
}

export function useSorteiosDoEspectador(espectadorId: UUID | undefined) {
  return useQuery({
    queryKey: espectadorId
      ? queryKeys.porEspectador(espectadorId)
      : (["sorteios", "espectador", "vazio"] as const),
    queryFn: () => sorteiosService.listarPorEspectador(espectadorId as UUID),
    enabled: Boolean(espectadorId),
  });
}

export function useInscreverNoSorteio(espectadorId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      sorteioId,
      espectadorId: id,
    }: { sorteioId: UUID; espectadorId: UUID }) =>
      sorteiosService.inscrever(sorteioId, id),
    onSuccess: () => {
      if (espectadorId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEspectador(espectadorId),
        });
      }
      queryClient.invalidateQueries({ queryKey: ["sorteios", "evento"] });
      queryClient.invalidateQueries({ queryKey: queryKeys.abertos });
    },
  });
}

export function useDesistirDoSorteio(espectadorId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      sorteioId,
      espectadorId: id,
    }: { sorteioId: UUID; espectadorId: UUID }) =>
      sorteiosService.desistir(sorteioId, id),
    onSuccess: () => {
      if (espectadorId) {
        queryClient.invalidateQueries({
          queryKey: queryKeys.porEspectador(espectadorId),
        });
      }
    },
  });
}

export function useCriarSorteio(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarSorteioRequisicao) =>
      sorteiosService.criar(payload),
    onSuccess: () => {
      invalidarSorteiosPorEvento(queryClient, eventoId);
    },
  });
}

export function useApurarSorteio(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sorteioId: UUID) => sorteiosService.apurar(sorteioId),
    onSuccess: (_, sorteioId) => {
      if (eventoId) {
        queryClient.invalidateQueries({ queryKey: queryKeys.porEvento(eventoId) });
      }
      queryClient.invalidateQueries({ queryKey: queryKeys.inscricoes(sorteioId) });
    },
  });
}

export function useCancelarSorteio(eventoId?: UUID) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sorteioId: UUID) => sorteiosService.cancelar(sorteioId),
    onSuccess: () => {
      invalidarSorteiosPorEvento(queryClient, eventoId);
    },
  });
}

export function useInscricoesSorteio(sorteioId: UUID | undefined) {
  return useQuery({
    queryKey: sorteioId
      ? queryKeys.inscricoes(sorteioId)
      : (["sorteios", "vazio", "inscricoes"] as const),
    queryFn: () => sorteiosService.listarInscricoes(sorteioId as UUID),
    enabled: Boolean(sorteioId),
  });
}
