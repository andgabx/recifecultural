"use client";

import Link from "next/link";
import { AnimatePresence, motion } from "motion/react";
import {
  Accessibility,
  CalendarDays,
  Drama,
  Pencil,
  Plus,
  Send,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PageLayout } from "@/components/layout/PageLayout";
import { useRecursosConfirmadosPorEvento } from "@/hooks/useAcessibilidade";
import { useSubmeterEvento } from "@/hooks/useAprovacao";
import { useEventosPorProdutor } from "@/hooks/useEventosProdutor";
import { detalheAcessibilidade } from "@/lib/acessibilidade";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import type { ApiError } from "@/lib/api";
import { formatarDataCurta } from "@/lib/format";
import { statusEventoLabel, statusEventoVariant } from "@/lib/statusMaps";
import { cn } from "@/lib/utils";
import { containerVariants, itemVariants } from "@/lib/motion";
import type { EventoResumo } from "@/services/bff/eventos";
import type { StatusEvento } from "@/types/dominio";

const promotor = IDENTIDADES_MOCK.produtor;

export default function MeusEventosPage() {
  const { data: eventos, isLoading, isError, refetch } = useEventosPorProdutor(
    promotor.id,
  );
  const { mapa: recursosPorEvento } = useRecursosConfirmadosPorEvento();
  const submeter = useSubmeterEvento();

  async function onSubmeter(evento: EventoResumo) {
    try {
      await submeter.mutateAsync(evento.id);
      toast.success(`"${evento.titulo}" enviado para análise`);
      refetch();
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const colunas: Coluna<EventoResumo>[] = [
    {
      header: "Título",
      cell: (e) => (
        <div className="min-w-0">
          <p className="font-display text-noite truncate font-medium">
            {e.titulo}
          </p>
          {e.descricaoCurta && (
            <p className="text-muted-foreground truncate text-xs">
              {e.descricaoCurta}
            </p>
          )}
        </div>
      ),
    },
    {
      header: "Categoria",
      cell: (e) => (
        <span className="text-xs uppercase tracking-wide text-muted-foreground">
          {e.categoria ?? "—"}
        </span>
      ),
    },
    {
      header: "Período",
      cell: (e) => (
        <span className="text-xs">
          {formatarDataCurta(e.periodoInicio)}
          {e.periodoFim && ` → ${formatarDataCurta(e.periodoFim)}`}
        </span>
      ),
    },
    {
      header: "Status",
      cell: (e) => <Badge variant={statusEventoVariant[e.status]}>{statusEventoLabel[e.status]}</Badge>,
    },
    {
      header: "Acessibilidade",
      cell: (e) => {
        const recursos = recursosPorEvento.get(e.id) ?? [];
        if (recursos.length === 0) {
          return (
            <span className="text-muted-foreground text-xs">
              {e.status === "APROVADO" ? "Nenhum recurso" : "—"}
            </span>
          );
        }
        const tipos = Array.from(new Set(recursos.map((r) => r.tipo)));
        return (
          <Link
            href={`/acessibilidade/${e.id}`}
            className="hover:text-azul flex flex-wrap items-center gap-1 text-xs transition-colors"
            title="Ver visão pública"
          >
            <Accessibility className="text-azul h-3.5 w-3.5" />
            <span className="font-semibold">{recursos.length}</span>
            <span className="text-muted-foreground">recurso(s):</span>
            {tipos.slice(0, 3).map((tipo) => {
              const Icone = detalheAcessibilidade(tipo).icon;
              return (
                <span
                  key={tipo}
                  title={detalheAcessibilidade(tipo).label}
                  className="bg-azul/10 text-azul rounded-full p-1"
                >
                  <Icone className="h-3 w-3" />
                </span>
              );
            })}
            {tipos.length > 3 && (
              <span className="text-muted-foreground text-[10px]">
                +{tipos.length - 3}
              </span>
            )}
          </Link>
        );
      },
    },
    {
      header: "",
      width: "1%",
      cell: (e) => (
        <div className="flex items-center gap-1">
          {e.status === "RASCUNHO" && (
            <>
              <Link
                href={`/produtor/eventos/${e.id}/editar`}
                className={cn(
                  buttonVariants({ variant: "outline", size: "sm" }),
                )}
              >
                <Pencil className="mr-1 h-3 w-3" />
                Editar
              </Link>
              <Button
                size="sm"
                variant="outline"
                onClick={() => onSubmeter(e)}
                disabled={submeter.isPending}
                className="border-laranja/40 text-laranja-dark hover:bg-laranja/10"
              >
                {submeter.isPending ? (
                  <LoadingSpinner className="mr-1 text-laranja" />
                ) : (
                  <Send className="mr-1 h-3 w-3" />
                )}
                Submeter
              </Button>
            </>
          )}
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Meus eventos"
      subtitulo={`Eventos criados por ${promotor.nome}.`}
      acoes={
        <Link
          href="/produtor/eventos/novo"
          className={cn(
            buttonVariants({ variant: "default" }),
            "bg-azul hover:bg-azul-light text-nevoa",
          )}
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo evento
        </Link>
      }
    >
      <Card className="border-dashed border-laranja/30 bg-laranja/5 p-4">
        <p className="text-xs text-muted-foreground">
          <span className="text-laranja-dark font-semibold uppercase tracking-widest">
            Modo demonstração
          </span>{" "}
          — promotor fixo{" "}
          <code className="font-mono">{promotor.id.slice(0, 8)}…</code>.
          Mude o papel no canto superior direito para outras visões.
        </p>
      </Card>

      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-14 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Drama}
          title="Falha ao carregar seus eventos"
          description="Verifique se o backend está rodando em http://localhost:8080."
        />
      )}

      {eventos && eventos.length === 0 && (
        <EmptyState
          icon={CalendarDays}
          title="Você ainda não criou eventos"
          description="Comece com um novo evento em rascunho."
          action={
            <Link
              href="/produtor/eventos/novo"
              className={cn(buttonVariants({ variant: "outline" }))}
            >
              Criar primeiro evento
            </Link>
          }
        />
      )}

      {eventos && eventos.length > 0 && (
        <motion.div
          variants={containerVariants}
          initial="initial"
          animate="animate"
        >
          <AnimatePresence>
            <motion.div variants={itemVariants}>
              <DataTable
                data={eventos}
                rowKey={(e) => e.id}
                columns={colunas}
              />
            </motion.div>
          </AnimatePresence>
        </motion.div>
      )}
    </PageLayout>
  );
}
