"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { Accessibility, ArrowLeft } from "lucide-react";
import { motion } from "motion/react";

import { buttonVariants } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { PublicLayout } from "@/components/layout/PublicLayout";
import { useRecursosAtivosPorEvento } from "@/hooks/useAcessibilidade";
import { useEvento } from "@/hooks/useEventos";
import { detalheAcessibilidade } from "@/lib/acessibilidade";
import { containerVariants, itemVariants, pageVariants } from "@/lib/motion";
import { cn } from "@/lib/utils";

export default function AcessibilidadePublicaPage() {
  const params = useParams<{ eventoId: string }>();
  const { data: evento, isLoading: carregandoEvento } = useEvento(
    params.eventoId,
  );
  const {
    data: recursos,
    isLoading,
    isError,
  } = useRecursosAtivosPorEvento(params.eventoId);

  return (
    <PublicLayout>
      <motion.div
        variants={pageVariants}
        initial="initial"
        animate="animate"
        exit="exit"
        className="mx-auto max-w-3xl space-y-6 p-6"
      >
        <Link
          href={evento ? `/eventos/${evento.id}` : "/"}
          className={cn(
            buttonVariants({ variant: "ghost", size: "sm" }),
            "text-azul",
          )}
        >
          <ArrowLeft className="mr-1 h-4 w-4" />
          Voltar ao evento
        </Link>

        <header className="space-y-2">
          <p className="text-muted-foreground text-xs uppercase tracking-wider">
            Acessibilidade
          </p>
          <h1 className="font-display text-noite text-3xl font-semibold">
            {carregandoEvento ? <Skeleton className="h-9 w-72" /> : evento?.titulo}
          </h1>
          <p className="text-muted-foreground text-sm">
            Recursos anunciados pela produção para este evento. Em caso de remoção,
            o cancelamento é comunicado publicamente.
          </p>
        </header>

        {isLoading && (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-24 w-full" />
            ))}
          </div>
        )}

        {isError && (
          <EmptyState
            icon={Accessibility}
            title="Falha ao carregar recursos"
            description="Tente novamente em instantes."
          />
        )}

        {recursos && recursos.length === 0 && (
          <EmptyState
            icon={Accessibility}
            title="Nenhum recurso anunciado"
            description="Esta apresentação ainda não tem recursos de acessibilidade confirmados. Em caso de dúvida, fale com a produção."
          />
        )}

        {recursos && recursos.length > 0 && (
          <motion.div
            variants={containerVariants}
            initial="initial"
            animate="animate"
            className="grid gap-3 sm:grid-cols-2"
          >
            {recursos.map((r) => {
              const detalhe = detalheAcessibilidade(r.tipo);
              const Icone = detalhe.icon;
              return (
                <motion.div key={r.id} variants={itemVariants}>
                  <Card className="border-azul/20 from-azul/5 to-white flex h-full gap-3 bg-gradient-to-br p-4">
                    <div className="bg-azul/10 text-azul flex h-10 w-10 shrink-0 items-center justify-center rounded-full">
                      <Icone className="h-5 w-5" />
                    </div>
                    <div className="space-y-1">
                      <p className="text-noite font-semibold">{detalhe.label}</p>
                      <p className="text-muted-foreground text-xs">
                        {detalhe.descricao}
                      </p>
                      <p className="text-muted-foreground/80 font-mono text-[10px]">
                        Apresentação {r.apresentacaoId.slice(0, 8)}…
                      </p>
                    </div>
                  </Card>
                </motion.div>
              );
            })}
          </motion.div>
        )}
      </motion.div>
    </PublicLayout>
  );
}
