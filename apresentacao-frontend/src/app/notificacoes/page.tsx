"use client";

import { AnimatePresence, motion } from "motion/react";
import { useMemo } from "react";
import {
  AlertCircle,
  Bell,
  CheckCircle2,
  Gift,
  Inbox,
  ShieldAlert,
  Ticket,
  type LucideIcon,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useMarcarNotificacaoLida,
  useNotificacoes,
} from "@/hooks/useNotificacoes";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { useRole } from "@/lib/role";
import { cn } from "@/lib/utils";
import type { NotificacaoResumo } from "@/services/bff/notificacoes";

type IconContexto = { icone: LucideIcon; cor: string };

const iconePorContexto: Record<string, IconContexto> = {
  EVENTO_CANCELADO: { icone: AlertCircle, cor: "text-destructive" },
  PARTICIPANTES_EVENTO_CANCELADO: { icone: AlertCircle, cor: "text-destructive" },
  EVENTO_APROVADO: { icone: CheckCircle2, cor: "text-emerald-600" },
  EVENTO_REPROVADO: { icone: ShieldAlert, cor: "text-destructive" },
  SORTEIO_CANCELADO: { icone: Gift, cor: "text-violeta" },
  SORTEIO_GANHADOR: { icone: Gift, cor: "text-laranja-dark" },
  SORTEIO_PROMOCAO: { icone: Gift, cor: "text-laranja-dark" },
  ACESSIBILIDADE_REMOVIDA: { icone: ShieldAlert, cor: "text-laranja" },
  INGRESSO_CONFIRMADO: { icone: Ticket, cor: "text-emerald-600" },
};

function iconePara(contexto: string): IconContexto {
  return iconePorContexto[contexto] ?? { icone: Bell, cor: "text-azul" };
}

function chaveDoDia(iso: string) {
  const hoje = new Date();
  const data = new Date(iso);
  const diff = Math.floor(
    (hoje.setHours(0, 0, 0, 0) - new Date(iso).setHours(0, 0, 0, 0)) /
      (1000 * 60 * 60 * 24),
  );
  if (diff === 0) return "Hoje";
  if (diff === 1) return "Ontem";
  if (diff < 7) return "Esta semana";
  return new Intl.DateTimeFormat("pt-BR", {
    month: "long",
    year: "numeric",
  }).format(data);
}

export default function NotificacoesPage() {
  const { papel } = useRole();
  const usuario = IDENTIDADES_MOCK[papel];
  const { data, isLoading, isError } = useNotificacoes(usuario.id);
  const marcar = useMarcarNotificacaoLida(usuario.id);

  const grupos = useMemo(() => {
    const arr = data ?? [];
    return arr.reduce<Record<string, NotificacaoResumo[]>>((acc, n) => {
      const chave = chaveDoDia(n.dataCriacao);
      if (!acc[chave]) acc[chave] = [];
      acc[chave].push(n);
      return acc;
    }, {});
  }, [data]);

  const naoLidas = data?.filter((n) => !n.foiLida).length ?? 0;

  async function marcarLida(n: NotificacaoResumo) {
    try {
      await marcar.mutateAsync(n.id);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PublicLayout>
      <section className="mx-auto max-w-3xl space-y-6 px-6 py-10">
        <header className="flex items-end justify-between gap-4">
          <div>
            <h1 className="font-display text-noite text-2xl font-semibold tracking-tight">
              Notificações
            </h1>
            <p className="text-muted-foreground text-sm">
              Recebidas por <strong>{usuario.nome}</strong>{" "}
              <span className="font-mono text-[10px]">({usuario.id.slice(0, 8)}…)</span>
            </p>
          </div>
          {naoLidas > 0 && (
            <Badge variant="default">
              {naoLidas} não {naoLidas === 1 ? "lida" : "lidas"}
            </Badge>
          )}
        </header>

        {isLoading && (
          <div className="space-y-2">
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} className="h-16 w-full" />
            ))}
          </div>
        )}

        {isError && (
          <EmptyState
            icon={AlertCircle}
            title="Falha ao carregar notificações"
            description="Verifique se o backend está rodando em http://localhost:8080."
          />
        )}

        {data && data.length === 0 && (
          <EmptyState
            icon={Inbox}
            title="Caixa vazia"
            description={
              `Como ${usuario.nome}, você ainda não recebeu nenhuma notificação. ` +
              "Tente criar um bloqueio em /gestor/bloqueios ou cancelar um sorteio."
            }
          />
        )}

        {data && data.length > 0 && (
          <div className="space-y-6">
            {Object.entries(grupos).map(([dia, lista]) => (
              <section key={dia} className="space-y-2">
                <h2 className="text-muted-foreground border-b border-border pb-1 text-xs font-semibold uppercase tracking-widest">
                  {dia}
                </h2>
                <AnimatePresence initial={false}>
                  {lista.map((n) => {
                    const meta = iconePara(n.contexto);
                    const Icon = meta.icone;
                    return (
                      <motion.div
                        key={n.id}
                        initial={{ opacity: 0, y: 8 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0.5, x: 8 }}
                        layout
                      >
                        <Card
                          className={cn(
                            "flex items-start gap-3 p-4",
                            !n.foiLida && "bg-azul/5 border-l-azul border-l-2",
                          )}
                        >
                          <span
                            className={cn(
                              "mt-0.5 flex h-9 w-9 items-center justify-center rounded-lg bg-nevoa-muted",
                            )}
                          >
                            <Icon className={cn("h-4 w-4", meta.cor)} />
                          </span>
                          <div className="min-w-0 flex-1 space-y-1">
                            <p className="text-noite text-sm">{n.mensagem}</p>
                            <p className="text-muted-foreground flex items-center gap-2 text-xs">
                              <span className="font-mono uppercase tracking-widest text-[10px]">
                                {n.contexto}
                              </span>
                              <span>·</span>
                              <span>{formatarDataHora(n.dataCriacao)}</span>
                            </p>
                          </div>
                          {!n.foiLida && (
                            <Button
                              size="sm"
                              variant="ghost"
                              onClick={() => marcarLida(n)}
                              disabled={marcar.isPending}
                              className="text-azul hover:bg-azul/10 shrink-0"
                            >
                              {marcar.isPending && (
                                <LoadingSpinner className="mr-1 text-azul" />
                              )}
                              Marcar lida
                            </Button>
                          )}
                        </Card>
                      </motion.div>
                    );
                  })}
                </AnimatePresence>
              </section>
            ))}
          </div>
        )}
      </section>
    </PublicLayout>
  );
}
