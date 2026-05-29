"use client";

import { useMutation } from "@tanstack/react-query";

import { catracaService, type ValidarAcessoRequisicao } from "@/services/bff/catraca";

export function useValidarCatraca() {
  return useMutation({
    mutationFn: (payload: ValidarAcessoRequisicao) => catracaService.validar(payload),
  });
}
