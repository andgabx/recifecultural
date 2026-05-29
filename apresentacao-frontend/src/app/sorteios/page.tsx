"use client";

import { AnimatePresence, motion } from "motion/react";
import {
  CalendarClock,
  Check,
  Crown,
  Gift,
  LogOut,
  Sparkles,
  Trophy,
  XCircle,
  type LucideIcon,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useDesistirDoSorteio,
  useSorteiosDoEspectador,
} from "@/hooks/useSorteios";
import type { ApiError } from "@/lib/api";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { useRole } from "@/lib/role";
import { cn } from "@/lib/utils";
import type { SorteioInscritoResumo } from "@/services/bff/sorteios";
import type { StatusInscricao, StatusSorteio } from "@/types/dominio";

const statusInscricaoConfig: Record<
  StatusInscricao,
  { label: string; icon: LucideIcon; cor: string; bg: string }
> = {
  GANHADOR: {
    label: "Ganhador",
    icon: Trophy,
    cor: "text-emerald-700",
    bg: "bg-emerald-100 border-emerald-300",
  },
  SUPLENTE: {
    label: "Suplente",
    icon: Crown,
    cor: "text-yellow-800",
    bg: "bg-frevo/20 border-frevo/40",
  },
  INSCRITO: {
    label: "Inscrito",
    icon: Sparkles,
    cor: "text-vinho",
    bg: "bg-vinho/10 border-vinho/30",
  },
  DESISTENTE: {
    label: "Desistente",
    icon: LogOut,
    cor: "text-muted-foreground",
    bg: "bg-border/30 border-border",
  },
  CANCELADA: {
    label: "Cancelada",
    icon: XCircle,
    cor: "text-destructive",
    bg: "bg-destructive/10 border-destructive/30",
  },
};

const statusSorteioLabel: Record<StatusSorteio, string> = {
  INSCRICOES_ABERTAS: "Inscrições abertas",
  EM_APURACAO: "Em apuração",
  CONCLUIDO: "Concluído",
  CANCELADO: "Cancelado",
};

function formatarData(iso: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(iso));
}

function progressoPrazo(prazoIso: string) {
  const agora = Date.now();
  const prazo = new Date(prazoIso).getTime();
  const diff = prazo - agora;
  if (diff <= 0) return 0;
  const seteDias = 7 * 24 * 60 * 60 * 1000;
  return Math.max(0, Math.min(100, (diff / seteDias) * 100));
}

function diasRestantes(prazoIso: string) {
  const diff = new Date(prazoIso).getTime() - Date.now();
  if (diff <= 0) return null;
  return Math.ceil(diff / (24 * 60 * 60 * 1000));
}

export default function MeusSorteiosPage() {
  const { papel } = useRole();
  const usuario = IDENTIDADES_MOCK[papel];

  const { data, isLoading, isError } = useSorteiosDoEspectador(usuario.id);
  const desistir = useDesistirDoSorteio(usuario.id);

  async function onDesistir(s: SorteioInscritoResumo) {
    try {
      await desistir.mutateAsync({
        sorteioId: s.sorteioId,
        espectadorId: usuario.id,
      });
      toast.success(
        "Desistência registrada. Se você era ganhador, o primeiro suplente foi promovido.",
      );
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PublicLayout>
      <section className="mx-auto max-w-4xl space-y-6 px-6 py-10">
        <header className="space-y-1">
          <p className="text-frevo font-mono text-xs uppercase tracking-[0.25em]">
            Espectador · {usuario.nome}
          </p>
          <h1 className="font-display text-palco text-3xl font-bold tracking-tight">
            Meus sorteios
          </h1>
          <p className="text-muted-foreground text-sm">
            Acompanhe sua participação em sorteios de apresentações com lotação
            limitada. A ordem de apuração segue o padrão{" "}
            <strong className="text-vinho">Iterator</strong>: ganhadores
            primeiro, depois suplentes em fila.
          </p>
        </header>

        {isLoading && (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-32 w-full" />
            ))}
          </div>
        )}

        {isError && (
          <EmptyState
            icon={Gift}
            title="Não foi possível carregar seus sorteios"
            description="Verifique se o backend está rodando."
          />
        )}

        {data && data.length === 0 && (
          <EmptyState
            icon={Gift}
            title="Você ainda não participa de sorteios"
            description="Quando se inscrever em um sorteio de evento, ele aparecerá aqui com o status (Inscrito, Ganhador, Suplente)."
          />
        )}

        {data && data.length > 0 && (
          <ul className="space-y-3">
            <AnimatePresence>
              {data.map((s) => {
                const cfg = statusInscricaoConfig[s.statusInscricao];
                const Icon = cfg.icon;
                const progresso = progressoPrazo(s.prazoInscricao);
                const diasFalta = diasRestantes(s.prazoInscricao);
                const aindaAberto = s.statusSorteio === "INSCRICOES_ABERTAS";
                const podeDesistir =
                  s.statusInscricao === "GANHADOR" ||
                  s.statusInscricao === "SUPLENTE" ||
                  (s.statusInscricao === "INSCRITO" && aindaAberto);

                return (
                  <motion.li
                    key={s.sorteioId}
                    initial={{ opacity: 0, y: 8 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0.3, x: 16 }}
                    layout
                  >
                    <Card className="space-y-3 p-5">
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0 flex-1">
                          <p className="text-muted-foreground font-mono text-[10px] uppercase tracking-widest">
                            Apresentação {s.apresentacaoId.slice(0, 8)}…
                          </p>
                          <h3 className="font-display text-palco mt-1 text-lg font-semibold">
                            {s.vagas} {s.vagas === 1 ? "vaga" : "vagas"} ·{" "}
                            <span className="text-muted-foreground text-sm font-normal">
                              {s.totalInscritos} inscritos
                            </span>
                          </h3>
                          <p className="text-muted-foreground text-xs">
                            {statusSorteioLabel[s.statusSorteio]}
                          </p>
                        </div>
                        <div
                          className={cn(
                            "border flex items-center gap-2 rounded-full px-3 py-1",
                            cfg.bg,
                          )}
                        >
                          <Icon className={cn("h-4 w-4", cfg.cor)} />
                          <span className={cn("text-xs font-semibold", cfg.cor)}>
                            {cfg.label}
                          </span>
                          {s.posicao != null &&
                            (s.statusInscricao === "GANHADOR" ||
                              s.statusInscricao === "SUPLENTE") && (
                              <span className={cn("font-mono text-xs", cfg.cor)}>
                                #{s.posicao}
                              </span>
                            )}
                        </div>
                      </div>

                      <div className="text-muted-foreground space-y-1 text-xs">
                        <p className="flex items-center gap-1.5">
                          <CalendarClock className="text-ouro h-3 w-3" />
                          Apresentação: {formatarData(s.dataApresentacao)}
                        </p>
                        <p className="flex items-center gap-1.5">
                          <CalendarClock className="text-ouro h-3 w-3" />
                          Prazo de inscrição: {formatarData(s.prazoInscricao)}
                        </p>
                      </div>

                      {aindaAberto && (
                        <div className="space-y-1">
                          <div className="bg-marquee-muted h-1.5 overflow-hidden rounded-full">
                            <div
                              className="bg-ouro h-full transition-all"
                              style={{ width: `${progresso}%` }}
                            />
                          </div>
                          <p className="text-muted-foreground text-[10px]">
                            {diasFalta != null
                              ? `${diasFalta} ${diasFalta === 1 ? "dia restante" : "dias restantes"}`
                              : "Prazo encerrado"}
                          </p>
                        </div>
                      )}

                      {s.statusInscricao === "GANHADOR" && (
                        <Card className="bg-emerald-50 border-emerald-200 p-3 text-xs">
                          <p className="text-emerald-900">
                            <Check className="mr-1 inline h-3 w-3" />
                            Parabéns! Seu ingresso está garantido. Se desistir,
                            o primeiro suplente é promovido automaticamente.
                          </p>
                        </Card>
                      )}

                      {s.statusInscricao === "SUPLENTE" && (
                        <Card className="bg-frevo/10 border-frevo/30 p-3 text-xs">
                          <p className="text-yellow-900">
                            <Crown className="mr-1 inline h-3 w-3" />
                            Você é o {s.posicao}º suplente. Se um ganhador
                            desistir, sua vaga é confirmada automaticamente.
                          </p>
                        </Card>
                      )}

                      {podeDesistir && (
                        <div className="flex justify-end pt-1">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => onDesistir(s)}
                            disabled={desistir.isPending}
                            className="border-destructive/40 text-destructive hover:bg-destructive/10"
                          >
                            <LogOut className="mr-1 h-3 w-3" />
                            Desistir
                          </Button>
                        </div>
                      )}
                    </Card>
                  </motion.li>
                );
              })}
            </AnimatePresence>
          </ul>
        )}
      </section>
    </PublicLayout>
  );
}
