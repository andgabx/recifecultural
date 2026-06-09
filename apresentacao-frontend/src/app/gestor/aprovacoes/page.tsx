"use client";

import { AnimatePresence, motion } from "motion/react";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  CalendarDays,
  CheckCircle2,
  FileSearch,
  MapPin,
  Ticket,
  Users,
  XCircle,
} from "lucide-react";
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
import { useEvento, useEventos } from "@/hooks/useEventos";
import { containerVariants, itemVariants } from "@/lib/motion";
import type { ApiError } from "@/lib/api";
import { formatarDataCurta, formatarDataHora } from "@/lib/format";
import { statusEventoLabel, statusEventoVariant } from "@/lib/statusMaps";
import type { EventoResumo } from "@/services/bff/eventos";
import type { StatusEvento, UUID } from "@/types/dominio";

const reprovacaoSchema = z.object({
  feedback: z
    .string()
    .min(20, "Feedback deve ter no mínimo 20 caracteres")
    .max(1000, "Feedback muito longo"),
});

type ReprovacaoForm = z.infer<typeof reprovacaoSchema>;

function formatarPreco(valor?: string) {
  if (!valor) return null;
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(Number(valor));
}

export default function AprovacoesPage() {
  const { data: eventos, isLoading, isError, refetch } = useEventos();
  const [reprovacao, setReprovacao] = useState<EventoResumo | null>(null);
  const [detalheId, setDetalheId] = useState<UUID | null>(null);

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
            badge="violeta"
            eventos={grupos.EM_ANALISE}
            onAprovar={onAprovar}
            aprovando={aprovar.isPending}
            onReprovar={(e) => setReprovacao(e)}
            onVerDetalhe={(id) => setDetalheId(id)}
          />
          {grupos.RASCUNHO.length > 0 && (
            <ColunaStatus
              titulo="Rascunhos (aguardando submissão)"
              badge="secondary"
              eventos={grupos.RASCUNHO}
              somenteLeitura
              onVerDetalhe={(id) => setDetalheId(id)}
            />
          )}
          {grupos.OUTROS.length > 0 && (
            <ColunaStatus
              titulo="Histórico"
              badge="outline"
              eventos={grupos.OUTROS}
              somenteLeitura
              onVerDetalhe={(id) => setDetalheId(id)}
            />
          )}
        </div>
      )}

      <DetalheModal
        eventoId={detalheId}
        onClose={() => setDetalheId(null)}
      />

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
  onVerDetalhe,
  aprovando,
  somenteLeitura,
}: {
  titulo: string;
  badge: "violeta" | "secondary" | "outline";
  eventos: EventoResumo[];
  onAprovar?: (e: EventoResumo) => void;
  onReprovar?: (e: EventoResumo) => void;
  onVerDetalhe: (id: UUID) => void;
  aprovando?: boolean;
  somenteLeitura?: boolean;
}) {
  if (eventos.length === 0 && badge === "violeta") {
    return (
      <section>
        <header className="flex items-baseline justify-between border-b border-border pb-2">
          <h2 className="font-display text-noite text-lg font-semibold">
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
        <h2 className="font-display text-noite text-lg font-semibold">
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
              <Card
                className="flex h-full cursor-pointer flex-col gap-3 p-5 transition-shadow hover:shadow-md"
                onClick={() => onVerDetalhe(evento.id)}
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0 flex-1">
                    <p className="font-mono text-[10px] text-muted-foreground uppercase tracking-widest">
                      {evento.id.slice(0, 8)}
                    </p>
                    <h3 className="font-display text-noite mt-0.5 truncate text-base font-semibold">
                      {evento.titulo}
                    </h3>
                  </div>
                  <Badge variant={statusEventoVariant[evento.status]}>
                    {statusEventoLabel[evento.status]}
                  </Badge>
                </div>
                {evento.descricaoCurta && (
                  <p className="text-muted-foreground line-clamp-2 text-sm">
                    {evento.descricaoCurta}
                  </p>
                )}
                <div className="text-muted-foreground space-y-1 text-xs">
                  <p className="flex items-center gap-1.5">
                    <CalendarDays className="text-laranja h-3 w-3" />
                    {formatarDataCurta(evento.periodoInicio)}
                    {evento.periodoFim && ` → ${formatarDataCurta(evento.periodoFim)}`}
                  </p>
                  {evento.localId && (
                    <p className="flex items-center gap-1.5">
                      <MapPin className="text-laranja h-3 w-3" />
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
                      onClick={(e) => { e.stopPropagation(); onAprovar(evento); }}
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
                      onClick={(e) => { e.stopPropagation(); onReprovar(evento); }}
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

function DetalheModal({
  eventoId,
  onClose,
}: {
  eventoId: UUID | null;
  onClose: () => void;
}) {
  const { data: evento, isLoading } = useEvento(eventoId ?? undefined);

  return (
    <Modal
      open={!!eventoId}
      onClose={onClose}
      title={evento?.titulo ?? "Carregando…"}
      description={evento?.descricaoCurta}
      footer={
        <Button variant="outline" onClick={onClose}>
          Fechar
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-4 w-full" />
          ))}
        </div>
      )}

      {evento && (
        <div className="space-y-5 text-sm">
          {/* Status e categoria */}
          <div className="flex flex-wrap gap-2">
            <Badge variant={statusEventoVariant[evento.status as StatusEvento]}>
              {statusEventoLabel[evento.status as StatusEvento]}
            </Badge>
            {evento.categoria && (
              <Badge variant="outline">{evento.categoria}</Badge>
            )}
          </div>

          {/* Período */}
          <Section label="Período">
            <p className="flex items-center gap-1.5 text-muted-foreground">
              <CalendarDays className="h-3.5 w-3.5 text-laranja" />
              {formatarDataCurta(evento.periodoInicio)}
              {evento.periodoFim && ` → ${formatarDataCurta(evento.periodoFim)}`}
            </p>
          </Section>

          {/* Produtor */}
          <Section label="Produtor">
            <p className="font-mono text-xs text-muted-foreground">
              {evento.promotorId ?? "—"}
            </p>
          </Section>

          {/* Artistas */}
          {evento.artistas && evento.artistas.length > 0 && (
            <Section label="Artistas">
              <ul className="space-y-1">
                {evento.artistas.map((id) => (
                  <li key={id} className="flex items-center gap-1.5 text-muted-foreground">
                    <Users className="h-3.5 w-3.5 shrink-0 text-laranja" />
                    <span className="font-mono text-xs">{id}</span>
                  </li>
                ))}
              </ul>
            </Section>
          )}

          {/* Preços */}
          {(evento.precoInteira || evento.precoMeia || evento.precoSocial) && (
            <Section label="Preços">
              <ul className="space-y-1 text-muted-foreground">
                {evento.precoInteira && (
                  <li className="flex items-center gap-1.5">
                    <Ticket className="h-3.5 w-3.5 text-laranja" />
                    Inteira: <strong>{formatarPreco(evento.precoInteira)}</strong>
                  </li>
                )}
                {evento.precoMeia && (
                  <li className="flex items-center gap-1.5">
                    <Ticket className="h-3.5 w-3.5 text-laranja" />
                    Meia: <strong>{formatarPreco(evento.precoMeia)}</strong>
                  </li>
                )}
                {evento.precoSocial && (
                  <li className="flex items-center gap-1.5">
                    <Ticket className="h-3.5 w-3.5 text-laranja" />
                    Social: <strong>{formatarPreco(evento.precoSocial)}</strong>
                  </li>
                )}
              </ul>
            </Section>
          )}

          {/* Descrição longa */}
          {evento.descricaoLonga && (
            <Section label="Descrição">
              <p className="whitespace-pre-wrap text-muted-foreground leading-relaxed">
                {evento.descricaoLonga}
              </p>
            </Section>
          )}

          {/* Apresentações */}
          {evento.apresentacoes && evento.apresentacoes.length > 0 && (
            <Section label="Apresentações">
              <ul className="space-y-1 text-muted-foreground">
                {evento.apresentacoes.map((a) => (
                  <li key={a.id} className="flex items-center gap-1.5">
                    <CalendarDays className="h-3.5 w-3.5 text-laranja" />
                    {formatarDataHora(a.dataHora)}
                  </li>
                ))}
              </ul>
            </Section>
          )}
        </div>
      )}
    </Modal>
  );
}

function Section({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1">
      <p className="text-[10px] font-semibold uppercase tracking-widest text-muted-foreground">
        {label}
      </p>
      {children}
    </div>
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
          className="border-border bg-white placeholder:text-muted-foreground focus-visible:border-azul focus-visible:ring-azul/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
          placeholder="Explique de forma clara o motivo da reprovação..."
        />
      </FormField>
    </Modal>
  );
}
