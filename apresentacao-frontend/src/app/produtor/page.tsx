"use client";

import { useMemo } from "react";
import Link from "next/link";
import {
  CalendarDays,
  Drama,
  ReceiptText,
  TicketCheck,
  TrendingUp,
  Wallet,
} from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { buttonVariants } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { PageLayout } from "@/components/layout/PageLayout";
import { useEventosPorProdutor } from "@/hooks/useEventosProdutor";
import { useIndicadoresFinanceiros } from "@/hooks/useFinanceiro";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { cn } from "@/lib/utils";
import type { EventoResumo } from "@/services/bff/eventos";

const formatarMoeda = (valor: number) =>
  new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(valor);

const formatarData = (iso: string) =>
  new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(iso));

function rangeDoMesAtual() {
  const agora = new Date();
  const inicio = new Date(agora.getFullYear(), agora.getMonth(), 1);
  const fim = new Date(agora.getFullYear(), agora.getMonth() + 1, 0, 23, 59, 59);
  return {
    periodoInicio: inicio.toISOString(),
    periodoFim: fim.toISOString(),
  };
}

export default function ProdutorDashboardPage() {
  const produtorId = IDENTIDADES_MOCK.produtor.id;
  const { data: eventos, isLoading: carregandoEventos } =
    useEventosPorProdutor(produtorId);
  const { periodoInicio, periodoFim } = useMemo(rangeDoMesAtual, []);
  const {
    data: indicadores,
    isLoading: carregandoIndicadores,
    isError: erroIndicadores,
  } = useIndicadoresFinanceiros(periodoInicio, periodoFim);

  const ativos = useMemo(
    () => (eventos ?? []).filter((e) => e.status === "APROVADO"),
    [eventos],
  );
  const proximosEventos = useMemo(
    () =>
      [...(eventos ?? [])]
        .filter((e) => e.periodoInicio)
        .sort(
          (a, b) =>
            new Date(a.periodoInicio!).getTime() -
            new Date(b.periodoInicio!).getTime(),
        )
        .slice(0, 5),
    [eventos],
  );

  const stats = [
    {
      label: "Eventos ativos",
      valor: carregandoEventos ? null : ativos.length.toString(),
      icone: Drama,
      hint: "Aprovados pelo gestor",
    },
    {
      label: "Receita bruta (mês)",
      valor: carregandoIndicadores
        ? null
        : indicadores
          ? formatarMoeda(indicadores.receitaBruta)
          : "—",
      icone: Wallet,
      hint: "Ingressos vendidos no mês",
    },
    {
      label: "Receita líquida (mês)",
      valor: carregandoIndicadores
        ? null
        : indicadores
          ? formatarMoeda(indicadores.receitaLiquida)
          : "—",
      icone: TrendingUp,
      hint: `Despesas: ${
        indicadores ? formatarMoeda(indicadores.totalDespesas) : "—"
      }`,
    },
    {
      label: "Ocupação média",
      valor: carregandoIndicadores
        ? null
        : indicadores
          ? `${Math.round(indicadores.ocupacao * 100)}%`
          : "—",
      icone: TicketCheck,
      hint: "Lugares vendidos / capacidade",
    },
  ];

  return (
    <PageLayout
      titulo="Olá, produtor"
      subtitulo="Visão geral dos seus eventos e da receita do mês."
    >
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((s) => (
          <Card key={s.label}>
            <CardContent className="flex items-start gap-3 p-5">
              <span className="bg-vinho/10 text-vinho rounded-lg p-2">
                <s.icone className="h-5 w-5" />
              </span>
              <div className="min-w-0">
                <p className="text-muted-foreground text-xs uppercase tracking-wide">
                  {s.label}
                </p>
                {s.valor == null ? (
                  <Skeleton className="mt-2 h-7 w-24" />
                ) : (
                  <p className="font-display text-palco mt-1 truncate text-2xl font-bold">
                    {s.valor}
                  </p>
                )}
                <p className="text-muted-foreground/80 mt-1 text-[10px]">
                  {s.hint}
                </p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {erroIndicadores && (
        <Card className="border-destructive/30 bg-destructive/5 p-4 text-sm">
          <p className="text-destructive">
            Não foi possível carregar os indicadores financeiros do mês.
          </p>
        </Card>
      )}

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-[2fr_1fr]">
        <Card className="p-6">
          <header className="flex items-end justify-between gap-2">
            <div>
              <h2 className="font-display text-palco text-lg font-semibold">
                Próximas apresentações
              </h2>
              <p className="text-muted-foreground text-sm">
                Eventos com data de início mais próxima.
              </p>
            </div>
            <Link
              href="/produtor/eventos"
              className={cn(buttonVariants({ variant: "outline", size: "sm" }))}
            >
              Ver todos
            </Link>
          </header>

          {carregandoEventos && (
            <div className="mt-4 space-y-2">
              {Array.from({ length: 3 }).map((_, i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          )}

          {!carregandoEventos && proximosEventos.length === 0 && (
            <EmptyState
              icon={CalendarDays}
              title="Nenhum evento programado"
              description="Crie um novo evento para que ele apareça aqui."
              action={
                <Link
                  href="/produtor/eventos/novo"
                  className={cn(buttonVariants({ variant: "outline" }))}
                >
                  Criar evento
                </Link>
              }
            />
          )}

          {!carregandoEventos && proximosEventos.length > 0 && (
            <ul className="divide-border mt-4 divide-y">
              {proximosEventos.map((e) => (
                <LinhaEvento key={e.id} evento={e} />
              ))}
            </ul>
          )}
        </Card>

        <Card className="p-6">
          <h2 className="font-display text-palco text-lg font-semibold">
            Atalhos
          </h2>
          <ul className="mt-4 space-y-2 text-sm">
            <Atalho
              href="/produtor/eventos/novo"
              icone={Drama}
              titulo="Novo evento"
              descricao="Cadastrar rascunho para análise"
            />
            <Atalho
              href="/produtor/financeiro"
              icone={ReceiptText}
              titulo="Financeiro"
              descricao="Registrar despesas e ver detalhes"
            />
            <Atalho
              href="/produtor/sorteios"
              icone={TicketCheck}
              titulo="Sorteios"
              descricao="Apurar e gerenciar inscrições"
            />
          </ul>
        </Card>
      </div>
    </PageLayout>
  );
}

function LinhaEvento({ evento }: { evento: EventoResumo }) {
  return (
    <li className="flex items-center justify-between gap-3 py-3">
      <div className="min-w-0">
        <Link
          href={`/produtor/eventos/${evento.id}/editar`}
          className="text-palco hover:text-vinho block truncate text-sm font-medium transition-colors"
        >
          {evento.titulo}
        </Link>
        <p className="text-muted-foreground text-xs">
          {evento.periodoInicio
            ? formatarData(evento.periodoInicio)
            : "Sem data"}
        </p>
      </div>
      <Badge
        variant={evento.status === "APROVADO" ? "success" : "secondary"}
        className="shrink-0"
      >
        {evento.status}
      </Badge>
    </li>
  );
}

function Atalho({
  href,
  icone: Icone,
  titulo,
  descricao,
}: {
  href: string;
  icone: typeof Drama;
  titulo: string;
  descricao: string;
}) {
  return (
    <li>
      <Link
        href={href}
        className="hover:bg-marquee-muted -mx-2 flex items-start gap-3 rounded-lg px-2 py-2 transition-colors"
      >
        <span className="bg-ouro/10 text-ouro-dark rounded-md p-2">
          <Icone className="h-4 w-4" />
        </span>
        <div>
          <p className="text-palco text-sm font-medium">{titulo}</p>
          <p className="text-muted-foreground text-xs">{descricao}</p>
        </div>
      </Link>
    </li>
  );
}
