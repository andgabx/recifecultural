"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { AlertTriangle, CalendarOff, CheckCircle, Plus, Unlock } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEspaco } from "@/components/form/Seletores";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useBloqueios,
  useCadastrarBloqueio,
  useDesativarBloqueio,
  usePreviewBloqueio,
} from "@/hooks/useBloqueios";
import type { ApiError } from "@/lib/api";
import type { BloqueioResumo, EventoConflitante } from "@/services/bff/bloqueios";

const cadastroSchema = z.object({
  espacoId: z.string().uuid("Id de espaço inválido"),
  dataInicio: z.string().min(1, "Informe a data de início"),
  dataFim: z.string().min(1, "Informe a data de fim"),
  justificativa: z
    .string()
    .min(10, "Justificativa muito curta (mín. 10 caracteres)"),
});
type CadastroForm = z.infer<typeof cadastroSchema>;

const formatarData = (iso: string) =>
  new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(iso));

export default function BloqueiosPage() {
  const { data, isLoading, isError } = useBloqueios();
  const [cadastroAberto, setCadastroAberto] = useState(false);
  const [etapa, setEtapa] = useState<"form" | "preview">("form");
  const [conflitos, setConflitos] = useState<EventoConflitante[]>([]);
  const [pendingValues, setPendingValues] = useState<CadastroForm | null>(null);
  const [bloqueioParaDesativar, setBloqueioParaDesativar] = useState<BloqueioResumo | null>(null);

  const preview = usePreviewBloqueio();
  const cadastrar = useCadastrarBloqueio();
  const desativar = useDesativarBloqueio();

  const form = useForm<CadastroForm>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: {
      espacoId: "",
      dataInicio: "",
      dataFim: "",
      justificativa: "",
    },
  });

  function fecharModal() {
    form.reset();
    setCadastroAberto(false);
    setEtapa("form");
    setConflitos([]);
    setPendingValues(null);
  }

  async function onVerificarImpacto(values: CadastroForm) {
    try {
      const resultado = await preview.mutateAsync({
        espacoId: values.espacoId,
        inicio: values.dataInicio,
        fim: values.dataFim,
      });
      setPendingValues(values);
      setConflitos(resultado);
      setEtapa("preview");
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onConfirmar() {
    if (!pendingValues) return;
    try {
      await cadastrar.mutateAsync({
        espacoId: pendingValues.espacoId,
        inicio: pendingValues.dataInicio,
        fim: pendingValues.dataFim,
        justificativa: pendingValues.justificativa,
      });
      toast.success("Bloqueio criado. Eventos conflitantes foram cancelados.");
      fecharModal();
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function confirmarDesativacao(reativarEventos: boolean) {
    if (!bloqueioParaDesativar) return;
    try {
      await desativar.mutateAsync({ id: bloqueioParaDesativar.id, reativarEventos });
      toast.success(
        reativarEventos
          ? "Bloqueio desativado e eventos reativados."
          : "Bloqueio desativado.",
      );
      setBloqueioParaDesativar(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const ativos = data?.filter((b) => b.ativo) ?? [];
  const historico = data?.filter((b) => !b.ativo) ?? [];

  const colunas: Coluna<BloqueioResumo>[] = [
    {
      header: "Espaço",
      cell: (b) => (
        <span className="font-mono text-xs">{b.espacoId.slice(0, 8)}…</span>
      ),
    },
    {
      header: "Período",
      cell: (b) => (
        <span>
          {formatarData(b.dataInicio)} → {formatarData(b.dataFim)}
        </span>
      ),
    },
    {
      header: "Justificativa",
      cell: (b) => (
        <span className="line-clamp-2 max-w-md text-sm">{b.justificativa}</span>
      ),
    },
    {
      header: "Status",
      cell: (b) => (
        <Badge variant={b.ativo ? "destructive" : "outline"}>
          {b.ativo ? "Ativo" : "Encerrado"}
        </Badge>
      ),
    },
    {
      header: "",
      width: "1%",
      cell: (b) =>
        b.ativo && (
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => setBloqueioParaDesativar(b)}
            aria-label="Desativar bloqueio"
            className="text-emerald-700 hover:bg-emerald-100"
          >
            <Unlock className="h-3.5 w-3.5" />
          </Button>
        ),
    },
  ];

  return (
    <PageLayout
      titulo="Bloqueios administrativos"
      subtitulo="Períodos em que um espaço fica indisponível para eventos."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          className="bg-vinho hover:bg-vinho-light text-marquee"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo bloqueio
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={CalendarOff}
          title="Falha ao carregar bloqueios"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && (
        <div className="space-y-8">
          <section className="space-y-2">
            <h2 className="text-muted-foreground border-b border-border pb-1 text-xs font-semibold uppercase tracking-widest">
              Ativos
            </h2>
            <DataTable
              data={ativos}
              rowKey={(b) => b.id}
              columns={colunas}
              empty={
                <EmptyState
                  icon={CalendarOff}
                  title="Nenhum bloqueio ativo"
                  description="Crie um bloqueio quando precisar inviabilizar reservas em um espaço."
                  action={
                    <Button onClick={() => setCadastroAberto(true)} variant="outline">
                      Novo bloqueio
                    </Button>
                  }
                />
              }
            />
          </section>

          {historico.length > 0 && (
            <section className="space-y-2">
              <h2 className="text-muted-foreground border-b border-border pb-1 text-xs font-semibold uppercase tracking-widest">
                Histórico
              </h2>
              <DataTable
                data={historico}
                rowKey={(b) => b.id}
                columns={colunas}
                empty={null}
              />
            </section>
          )}
        </div>
      )}

      {/* Modal: confirmação de desativação */}
      <Modal
        open={!!bloqueioParaDesativar}
        onClose={() => setBloqueioParaDesativar(null)}
        title="Desativar bloqueio"
        description={
          bloqueioParaDesativar?.eventosCancelados.length
            ? `Este bloqueio cancelou ${bloqueioParaDesativar.eventosCancelados.length} evento${bloqueioParaDesativar.eventosCancelados.length !== 1 ? "s" : ""}. Deseja reativá-los?`
            : "Nenhum evento foi cancelado por este bloqueio."
        }
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => setBloqueioParaDesativar(null)}
              disabled={desativar.isPending}
            >
              Cancelar
            </Button>
            {bloqueioParaDesativar?.eventosCancelados.length ? (
              <>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => confirmarDesativacao(false)}
                  disabled={desativar.isPending}
                >
                  Só desativar
                </Button>
                <Button
                  type="button"
                  onClick={() => confirmarDesativacao(true)}
                  disabled={desativar.isPending}
                  className="bg-emerald-600 hover:bg-emerald-700 text-white"
                >
                  {desativar.isPending && <LoadingSpinner className="mr-2 text-white" />}
                  Desativar e reativar eventos
                </Button>
              </>
            ) : (
              <Button
                type="button"
                onClick={() => confirmarDesativacao(false)}
                disabled={desativar.isPending}
                className="bg-vinho hover:bg-vinho-light text-marquee"
              >
                {desativar.isPending && <LoadingSpinner className="mr-2 text-marquee" />}
                Desativar
              </Button>
            )}
          </>
        }
      >
        {bloqueioParaDesativar && (
          <div className="text-sm text-muted-foreground space-y-1">
            <p>
              <strong>Espaço:</strong>{" "}
              <span className="font-mono">{bloqueioParaDesativar.espacoId.slice(0, 8)}…</span>
            </p>
            <p>
              <strong>Período:</strong>{" "}
              {formatarData(bloqueioParaDesativar.dataInicio)} →{" "}
              {formatarData(bloqueioParaDesativar.dataFim)}
            </p>
            <p>
              <strong>Justificativa:</strong> {bloqueioParaDesativar.justificativa}
            </p>
          </div>
        )}
      </Modal>

      {/* Etapa 1: formulário */}
      <Modal
        open={cadastroAberto && etapa === "form"}
        onClose={fecharModal}
        title="Novo bloqueio"
        description="Informe o espaço, período e justificativa. Antes de confirmar você verá quais eventos serão afetados."
        footer={
          <>
            <Button type="button" variant="outline" onClick={fecharModal}>
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onVerificarImpacto)}
              disabled={preview.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {preview.isPending && (
                <LoadingSpinner className="mr-2 text-marquee" />
              )}
              Verificar impacto
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Espaço"
            htmlFor="espacoId"
            error={form.formState.errors.espacoId?.message}
            required
          >
            <SeletorEspaco
              id="espacoId"
              value={form.watch("espacoId") ?? ""}
              onChange={(v) => form.setValue("espacoId", v)}
            />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Data de início"
              htmlFor="dataInicio"
              error={form.formState.errors.dataInicio?.message}
              required
            >
              <Input
                id="dataInicio"
                type="date"
                {...form.register("dataInicio")}
              />
            </FormField>
            <FormField
              label="Data de fim"
              htmlFor="dataFim"
              error={form.formState.errors.dataFim?.message}
              required
            >
              <Input id="dataFim" type="date" {...form.register("dataFim")} />
            </FormField>
          </div>
          <FormField
            label="Justificativa"
            htmlFor="justificativa"
            error={form.formState.errors.justificativa?.message}
            hint="Mín. 10 caracteres. Será exibida nas notificações aos afetados."
            required
          >
            <textarea
              id="justificativa"
              rows={3}
              {...form.register("justificativa")}
              className="border-border bg-marquee-card placeholder:text-muted-foreground focus-visible:border-vinho focus-visible:ring-vinho/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
              placeholder="Manutenção emergencial do sistema de som, prevista para..."
            />
          </FormField>
        </form>
      </Modal>

      {/* Etapa 2: preview de impacto */}
      <Modal
        open={cadastroAberto && etapa === "preview"}
        onClose={fecharModal}
        title="Impacto do bloqueio"
        description={
          conflitos.length === 0
            ? "Nenhum evento aprovado será afetado por este bloqueio."
            : `${conflitos.length} evento${conflitos.length > 1 ? "s" : ""} será${conflitos.length > 1 ? "ão" : ""} cancelado${conflitos.length > 1 ? "s" : ""}. Promotores, artistas e titulares de ingresso serão notificados.`
        }
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => setEtapa("form")}
              disabled={cadastrar.isPending}
            >
              Voltar
            </Button>
            <Button
              type="button"
              onClick={onConfirmar}
              disabled={cadastrar.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {cadastrar.isPending && (
                <LoadingSpinner className="mr-2 text-marquee" />
              )}
              Confirmar bloqueio
            </Button>
          </>
        }
      >
        {conflitos.length === 0 ? (
          <div className="flex items-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">
            <CheckCircle className="h-4 w-4 shrink-0" />
            Nenhum evento agendado neste período.
          </div>
        ) : (
          <div className="space-y-2">
            <div className="flex items-center gap-2 rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm text-amber-700">
              <AlertTriangle className="h-4 w-4 shrink-0" />
              Os eventos abaixo serão cancelados imediatamente.
            </div>
            <ul className="divide-y rounded-lg border text-sm">
              {conflitos.map((e) => (
                <li key={e.id} className="space-y-1 px-3 py-2">
                  <div className="flex items-start justify-between gap-4">
                    <span className="font-medium">{e.titulo}</span>
                    {e.periodoInicio && (
                      <span className="shrink-0 text-xs text-muted-foreground">
                        {formatarData(e.periodoInicio)}
                        {e.periodoFim && e.periodoFim !== e.periodoInicio
                          ? ` → ${formatarData(e.periodoFim)}`
                          : ""}
                      </span>
                    )}
                  </div>
                  <div className="flex gap-3 text-xs text-muted-foreground">
                    <span>{e.totalEspectadores} espectador{e.totalEspectadores !== 1 ? "es" : ""}</span>
                    <span>·</span>
                    <span>
                      {new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(e.totalReembolso)} em reembolsos
                    </span>
                  </div>
                </li>
              ))}
            </ul>
            {conflitos.some((e) => e.totalEspectadores > 0) && (
              <p className="text-xs text-muted-foreground text-right">
                Total em reembolsos:{" "}
                <strong>
                  {new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(
                    conflitos.reduce((sum, e) => sum + e.totalReembolso, 0),
                  )}
                </strong>
              </p>
            )}
          </div>
        )}
      </Modal>
    </PageLayout>
  );
}
