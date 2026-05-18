"use client";

import { useQuery } from "@tanstack/react-query";

import { auditoriaService } from "@/services/bff/auditoria";

export function useAuditoria(params?: { entidade?: string; limite?: number }) {
  return useQuery({
    queryKey: ["auditoria", params?.entidade ?? null, params?.limite ?? 100],
    queryFn: () => auditoriaService.listar(params),
  });
}
