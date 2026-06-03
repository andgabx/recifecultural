import type { StatusEvento, StatusSorteio } from '@/types/dominio';

export type BadgeVariant =
  | 'default'
  | 'secondary'
  | 'accent'
  | 'frevo'
  | 'success'
  | 'warning'
  | 'destructive'
  | 'outline';

export const statusEventoLabel: Record<StatusEvento, string> = {
  RASCUNHO: 'Rascunho',
  EM_ANALISE: 'Em análise',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
  CANCELADO: 'Cancelado',
  FINALIZADO: 'Finalizado',
};

export const statusEventoVariant: Record<StatusEvento, BadgeVariant> = {
  RASCUNHO: 'secondary',
  EM_ANALISE: 'frevo',
  APROVADO: 'success',
  REPROVADO: 'destructive',
  CANCELADO: 'destructive',
  FINALIZADO: 'outline',
};

export const statusSorteioLabel: Record<StatusSorteio, string> = {
  INSCRICOES_ABERTAS: 'Inscrições abertas',
  EM_APURACAO: 'Em apuração',
  CONCLUIDO: 'Concluído',
  CANCELADO: 'Cancelado',
};

export const statusSorteioVariant: Record<StatusSorteio, BadgeVariant> = {
  INSCRICOES_ABERTAS: 'frevo',
  EM_APURACAO: 'default',
  CONCLUIDO: 'success',
  CANCELADO: 'destructive',
};
