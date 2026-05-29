"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  artistasService,
  type CriarArtistaRequisicao,
} from "@/services/bff/artistas";
import type { UUID } from "@/types/dominio";

const queryKeys = {
  todos: ["artistas"] as const,
};

export function useArtistas() {
  return useQuery({
    queryKey: queryKeys.todos,
    queryFn: () => artistasService.listar(),
  });
}

export function useCadastrarArtista() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CriarArtistaRequisicao) =>
      artistasService.cadastrar(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}

export function useInativarArtista() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: UUID) => artistasService.inativar(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.todos }),
  });
}
