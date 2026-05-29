"use client";

import { AnimatePresence, motion } from "motion/react";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { CalendarDays, CheckCircle2, FileSearch, MapPin, XCircle } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { FormField } from "@/components/form/FormField";
import { PageLayout } from "@/components/layout/PageLayout";
import { useAprovarEvento, useReprovarEvento } from "@/hooks/useAprovacao";
import { useEventos } from "@/hooks/useEventos";
import { containerVariants, itemVariants } from "@/lib/motion";
import type { ApiError } from "@/lib/api";
import type { EventoResumo } from "@/services/bff/eventos";
import type { StatusEvento } from "@/types/dominio";

const reprovacaoSchema = z.object({
  feedback: z
    .string()
    .min(20, "Feedback deve ter no mínimo 20 caracteres")
    .max(1000, "Feedback muito longo"),
});

type ReprovacaoForm = z.infer<typeof reprovacaoSchema>;

const statusVariant: Record<
  StatusEvento,
  "default" | "success" | "frevo" | "secondary" | "destructive" | "outline"
> = {
  RASCUNHO: "secondary",
  EM_ANALISE: "frevo",
  APROVADO: "success",
  REPROVADO: "destructive",
  CANCELADO: "destructive",
  FINALIZADO: "outline",
};

const statusLabel: Record<StatusEvento, string> = {
  RASCUNHO: "Rascunho",
  EM_ANALISE: "Em análise",
  APROVADO: "Aprovado",
  REPROVADO: "Reprovado",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

function formatarData(iso?: string) {
  if (!iso) return "Sem data";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(iso));
}

export default function AprovacoesPage() {
  const { data: eventos, isLoading, isError, refetch } = useEventos();
  const [reprovacao, setReprovacao] = useState<EventoResumo | null>(null);

  const aprovar = useAprovarEvento();
  const reprovar = useReprovarEvento();

  const grupos = useMemo(() => {
    const todos = eventos ?? [];
    return {
      EM_ANALISE: todos.filter((e) => e.status === "EM_ANALISE"),
      RASCUNHO: todos.filter((e) => e.status === "RASCUNHO"),
      OUTROS: todos.filter(
        (e) => e.status !== "EM_ANALISE" && e.status !== "RASCUNHO",
      ),
    };
  }, [eventos]);

  async function onAprovar(evento: EventoResumo) {
    try {
      await aprovar.mutateAsync(evento.id);
      toast.success(`"${evento.titulo}" aprovado`);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Fila de aprovação"
      subtitulo="Eventos submetidos pelos produtores aguardando análise."
      acoes={
        <Button
          variant="outline"
          size="sm"
          onClick={() => refetch()}
          disabled={isLoading}
        >
          Atualizar
        </Button>
      }
    >
      {isLoading && <GradeSkeleton />}

      {isError && (
        <EmptyState
          icon={FileSearch}
          title="Não foi possível carregar a fila"
          description="Verifique se o backend está rodando em http://localhost:8080"
        />
      )}

      {eventos && eventos.length === 0 && (
        <EmptyState
          icon={FileSearch}
          title="Nenhum evento cadastrado"
          description="Quando produtores criarem e submeterem eventos, eles aparecerão aqui."
        />
      )}

      {eventos && eventos.length > 0 && (
        <div className="space-y-8">
          <ColunaStatus
            titulo="Em análise"
            badge="frevo"
            eventos={grupos.EM_ANALISE}
            onAprovar={onAprovar}
            aprovando={aprovar.isPending}
            onReprovar={(e) => setReprovacao(e)}
          />
          {grupos.RASCUNHO.length > 0 && (
            <ColunaStatus
              titulo="Rascunhos (aguardando submissão)"
              badge="secondary"
              eventos={grupos.RASCUNHO}
              somenteLeitura
            />
          )}
          {grupos.OUTROS.length > 0 && (
            <ColunaStatus
              titulo="Histórico"
              badge="outline"
              eventos={grupos.OUTROS}
              somenteLeitura
            />
          )}
        </div>
      )}

      <ReprovacaoModal
        evento={reprovacao}
        loading={reprovar.isPending}
        onClose={() => setReprovacao(null)}
        onConfirm={async (feedback) => {
          if (!reprovacao) return;
          try {
            await reprovar.mutateAsync({ id: reprovacao.id, feedback });
            toast.success(`"${reprovacao.titulo}" reprovado`);
            setReprovacao(null);
          } catch (error) {
            toast.error((error as ApiError).message);
          }
        }}
      />
    </PageLayout>
  );
}

function ColunaStatus({
  titulo,
  badge,
  eventos,
  onAprovar,
  onReprovar,
  aprovando,
  somenteLeitura,
}: {
  titulo: string;
  badge: "frevo" | "secondary" | "outline";
  eventos: EventoResumo[];
  onAprovar?: (e: EventoResumo) => void;
  onReprovar?: (e: EventoResumo) => void;
  aprovando?: boolean;
  somenteLeitura?: boolean;
}) {
  if (eventos.length === 0 && badge === "frevo") {
    return (
      <section>
        <header className="flex items-baseline justify-between border-b border-border pb-2">
          <h2 className="font-display text-palco text-lg font-semibold">
            {titulo}
          </h2>
          <Badge variant={badge}>0</Badge>
        </header>
        <p className="text-muted-foreground mt-4 text-sm">
          Nenhum evento aguardando análise no momento.
        </p>
      </section>
    );
  }
  return (
    <section>
      <header className="flex items-baseline justify-between border-b border-border pb-2">
        <h2 className="font-display text-palco text-lg font-semibold">
          {titulo}
        </h2>
        <Badge variant={badge}>{eventos.length}</Badge>
      </header>
      <motion.div
        variants={containerVariants}
        initial="initial"
        animate="animate"
        className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3"
      >
        <AnimatePresence>
          {eventos.map((evento) => (
            <motion.div
              key={evento.id}
              variants={itemVariants}
              exit={{ x: 160, opacity: 0, transition: { duration: 0.25 } }}
              layout
            >
              <Card className="flex h-full flex-col gap-3 p-5">
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0 flex-1">
                    <p className="font-mono text-[10px] text-muted-foreground uppercase tracking-widest">
                      {evento.id.slice(0, 8)}
                    </p>
                    <h3 className="font-display text-palco mt-0.5 truncate text-base font-semibold">
                      {evento.titulo}
                    </h3>
                  </div>
                  <Badge variant={statusVariant[evento.status]}>
                    {statusLabel[evento.status]}
                  </Badge>
                </div>
                {evento.descricaoCurta && (
                  <p className="text-muted-foreground line-clamp-2 text-sm">
                    {evento.descricaoCurta}
                  </p>
                )}
                <div className="text-muted-foreground space-y-1 text-xs">
                  <p className="flex items-center gap-1.5">
                    <CalendarDays className="text-ouro h-3 w-3" />
                    {formatarData(evento.periodoInicio)}
                    {evento.periodoFim && ` → ${formatarData(evento.periodoFim)}`}
                  </p>
                  {evento.localId && (
                    <p className="flex items-center gap-1.5">
                      <MapPin className="text-ouro h-3 w-3" />
                      <span className="font-mono">
                        {evento.localId.slice(0, 8)}…
                      </span>
                    </p>
                  )}
                </div>
                {!somenteLeitura && onAprovar && onReprovar && (
                  <div className="mt-auto flex gap-2 pt-3">
                    <Button
                      size="sm"
                      onClick={() => onAprovar(evento)}
                      disabled={aprovando}
                      className="flex-1 bg-emerald-600 text-white hover:bg-emerald-700"
                    >
                      {aprovando ? (
                        <LoadingSpinner className="mr-1 text-white" />
                      ) : (
                        <CheckCircle2 className="mr-1 h-3.5 w-3.5" />
                      )}
                      Aprovar
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onReprovar(evento)}
                      className="flex-1 border-destructive/40 text-destructive hover:bg-destructive/10"
                    >
                      <XCircle className="mr-1 h-3.5 w-3.5" />
                      Reprovar
                    </Button>
                  </div>
                )}
              </Card>
            </motion.div>
          ))}
        </AnimatePresence>
      </motion.div>
    </section>
  );
}

function GradeSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
      {Array.from({ length: 3 }).map((_, i) => (
        <Card key={i} className="space-y-2 p-5">
          <Skeleton className="h-4 w-1/2" />
          <Skeleton className="h-3 w-3/4" />
          <Skeleton className="h-3 w-1/3" />
          <div className="flex gap-2 pt-3">
            <Skeleton className="h-8 flex-1" />
            <Skeleton className="h-8 flex-1" />
          </div>
        </Card>
      ))}
    </div>
  );
}

function ReprovacaoModal({
  evento,
  loading,
  onClose,
  onConfirm,
}: {
  evento: EventoResumo | null;
  loading: boolean;
  onClose: () => void;
  onConfirm: (feedback: string) => Promise<void>;
}) {
  const form = useForm<ReprovacaoForm>({
    resolver: zodResolver(reprovacaoSchema),
    defaultValues: { feedback: "" },
  });

  return (
    <Modal
      open={!!evento}
      onClose={() => {
        form.reset();
        onClose();
      }}
      title={`Reprovar: ${evento?.titulo ?? ""}`}
      description="O produtor receberá esse feedback. Mínimo de 20 caracteres."
      footer={
        <>
          <Button
            type="button"
            variant="outline"
            onClick={() => {
              form.reset();
              onClose();
            }}
            disabled={loading}
          >
            Cancelar
          </Button>
          <Button
            type="button"
            onClick={form.handleSubmit(async (values) => {
              await onConfirm(values.feedback);
              form.reset();
            })}
            disabled={loading}
            className="bg-destructive text-white hover:bg-destructive/90"
          >
            {loading && <LoadingSpinner className="mr-2 text-white" />}
            Reprovar evento
          </Button>
        </>
      }
    >
      <FormField
        label="Feedback de reprovação"
        htmlFor="feedback"
        error={form.formState.errors.feedback?.message}
        hint={`${form.watch("feedback")?.length ?? 0} / 20 mínimo`}
        required
      >
        <textarea
          id="feedback"
          rows={5}
          {...form.register("feedback")}
          className="border-border bg-marquee-card placeholder:text-muted-foreground focus-visible:border-vinho focus-visible:ring-vinho/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
          placeholder="Explique de forma clara o motivo da reprovação..."
        />
      </FormField>
    </Modal>
  );
}
