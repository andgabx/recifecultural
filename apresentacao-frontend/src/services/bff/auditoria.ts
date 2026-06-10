import { api } from "@/lib/api";

import type { AcaoAuditoria } from "@/types/dominio";
export type { AcaoAuditoria } from "@/types/dominio";

export type RegistroAuditoria = {
  id: string;
  entidade: string;
  entidadeId: string;
  acao: AcaoAuditoria;
  statusAnterior: string | null;
  statusNovo: string | null;
  descricao: string | null;
  momento: string;
};

export const auditoriaService = {
  listar: (params?: { entidade?: string; limite?: number }) =>
    api
      .get<RegistroAuditoria[]>("/auditoria", { params })
      .then((r) => r.data),
};

export type AuditoriaService = typeof auditoriaService;
