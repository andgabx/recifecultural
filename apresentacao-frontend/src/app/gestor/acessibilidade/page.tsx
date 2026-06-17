"use client";

import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Accessibility, AlertTriangle, Plus, Trash2 } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorApresentacao, SeletorEvento } from "@/components/form/Seletores";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useMarcarRecurso,
  useRecursosAcessibilidade,
  useRemoverRecurso,
} from "@/hooks/useAcessibilidade";
import { useEventos } from "@/hooks/useEventos";
import type { ApiError } from "@/lib/api";
import type { RecursoAcessibilidade } from "@/services/bff/acessibilidade";

const TIPOS = [
  { value: "LIBRAS", label: "Libras" },
  { value: "AUDIODESCRICAO", label: "Audiodescrição" },
  { value: "LEGENDA_DESCRITIVA", label: "Legenda descritiva" },
  { value: "ESPACO_PCD", label: "Espaço PCD" },
  { value: "PROGRAMA_BRAILE", label: "Programa em braile" },
] as const;

const tipoLabel = (tipo: string) =>
  TIPOS.find((t) => t.value === tipo)?.label ?? tipo;

const marcacaoSchema = z.object({
  eventoId: z.string().min(1, "Selecione um evento aprovado"),
  apresentacaoId: z.string().min(1, "Selecione uma apresentação"),
  tipo: z.enum([
    "LIBRAS",
    "AUDIODESCRICAO",
    "LEGENDA_DESCRITIVA",
    "ESPACO_PCD",
    "PROGRAMA_BRAILE",
  ]),
});
type MarcacaoForm = z.infer<typeof marcacaoSchema>;

const remocaoSchema = z.object({
  justificativa: z
    .string()
    .min(30, "Justificativa precisa de pelo menos 30 caracteres"),
});
type RemocaoForm = z.infer<typeof remocaoSchema>;

export default function AcessibilidadeGestaoPage() {
  const { data, isLoading, isError } = useRecursosAcessibilidade();
  const { data: eventos } = useEventos();
  const marcar = useMarcarRecurso();
  const remover = useRemoverRecurso();
  const [cadastroAberto, setCadastroAberto] = useState(false);
  const [recursoParaRemover, setRecursoParaRemover] =
    useState<RecursoAcessibilidade | null>(null);

  const form = useForm<MarcacaoForm>({
    resolver: zodResolver(marcacaoSchema),
    defaultValues: {
      eventoId: "",
      apresentacaoId: "",
      tipo: "LIBRAS",
    },
  });

  const formRemocao = useForm<RemocaoForm>({
    resolver: zodResolver(remocaoSchema),
    defaultValues: { justificativa: "" },
  });

  const tituloPorEvento = useMemo(() => {
    const m = new Map<string, string>();
    eventos?.forEach((e) => m.set(e.id, e.titulo));
    return m;
  }, [eventos]);

  const agrupado = useMemo(() => {
    if (!data) return [];
    const mapa = new Map<string, RecursoAcessibilidade[]>();
    for (const r of data) {
      const lista = mapa.get(r.eventoId) ?? [];
      lista.push(r);
      mapa.set(r.eventoId, lista);
    }
    return Array.from(mapa.entries()).map(([eventoId, recursos]) => ({
      eventoId,
      titulo: tituloPorEvento.get(eventoId) ?? `Evento ${eventoId.slice(0, 8)}…`,
      recursos,
    }));
  }, [data, tituloPorEvento]);

  async function onMarcar(values: MarcacaoForm) {
    try {
      await marcar.mutateAsync(values);
      toast.success("Recurso de acessibilidade anunciado");
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onRemover(values: RemocaoForm) {
    if (!recursoParaRemover) return;
    try {
      await remover.mutateAsync({
        id: recursoParaRemover.id,
        justificativa: values.justificativa,
      });
      toast.success("Recurso removido e público notificado.");
      formRemocao.reset();
      setRecursoParaRemover(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Acessibilidade"
      subtitulo="Recursos anunciados por apresentação. A remoção é notificada publicamente com a justificativa."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          className="bg-azul hover:bg-azul-light text-nevoa"
        >
          <Plus className="mr-1 h-4 w-4" />
          Marcar recurso
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-16 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Accessibility}
          title="Falha ao carregar recursos"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && agrupado.length === 0 && (
        <EmptyState
          icon={Accessibility}
          title="Nenhum recurso cadastrado"
          description="Marque recursos por apresentação para que apareçam na ficha pública do evento."
          action={
            <Button onClick={() => setCadastroAberto(true)} variant="outline">
              Marcar recurso
            </Button>
          }
        />
      )}

      <div className="space-y-6">
        {agrupado.map((grupo) => (
          <section
            key={grupo.eventoId}
            className="border-border rounded-xl border bg-card"
          >
            <header className="border-border flex items-center justify-between border-b px-4 py-3">
              <div>
                <h2 className="text-noite font-display text-base font-semibold">
                  {grupo.titulo}
                </h2>
                <p className="text-muted-foreground text-xs">
                  {grupo.recursos.length} recurso(s) anunciado(s)
                </p>
              </div>
            </header>
            <ul className="divide-y divide-border">
              {grupo.recursos.map((r) => (
                <li
                  key={r.id}
                  className="flex items-center justify-between gap-4 px-4 py-3"
                >
                  <div className="flex items-center gap-3">
                    <Accessibility
                      className={
                        r.status === "CONFIRMADO"
                          ? "text-azul h-5 w-5"
                          : "text-muted-foreground h-5 w-5"
                      }
                    />
                    <div>
                      <p className="text-sm font-medium">
                        {tipoLabel(r.tipo)}{" "}
                        <Badge
                          variant={
                            r.status === "CONFIRMADO" ? "outline" : "secondary"
                          }
                          className="ml-1"
                        >
                          {r.status === "CONFIRMADO" ? "Confirmado" : "Removido"}
                        </Badge>
                      </p>
                      <p className="text-muted-foreground font-mono text-xs">
                        Apresentação {r.apresentacaoId ? `${r.apresentacaoId.slice(0, 8)}…` : "—"}
                      </p>
                      {r.justificativaRemocao && (
                        <p className="text-destructive mt-1 flex items-start gap-1 text-xs">
                          <AlertTriangle className="mt-0.5 h-3 w-3 shrink-0" />
                          {r.justificativaRemocao}
                        </p>
                      )}
                    </div>
                  </div>
                  {r.status === "CONFIRMADO" && (
                    <Button
                      size="icon-sm"
                      variant="ghost"
                      onClick={() => setRecursoParaRemover(r)}
                      aria-label="Remover recurso"
                      className="text-destructive hover:bg-destructive/10"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </Button>
                  )}
                </li>
              ))}
            </ul>
          </section>
        ))}
      </div>

      <Modal
        open={cadastroAberto}
        onClose={() => {
          form.reset();
          setCadastroAberto(false);
        }}
        title="Marcar recurso de acessibilidade"
        description="Anuncia ao público que essa apresentação contará com o recurso. Só eventos APROVADOS podem receber marcações."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setCadastroAberto(false);
              }}
              disabled={marcar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onMarcar)}
              disabled={marcar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {marcar.isPending && (
                <LoadingSpinner className="mr-2 text-nevoa" />
              )}
              Marcar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Evento"
            htmlFor="eventoId"
            error={form.formState.errors.eventoId?.message}
            required
          >
            <SeletorEvento
              id="eventoId"
              value={form.watch("eventoId") ?? ""}
              onChange={(v) => form.setValue("eventoId", v)}
              status="APROVADO"
            />
          </FormField>
          <FormField
            label="Apresentação"
            htmlFor="apresentacaoId"
            error={form.formState.errors.apresentacaoId?.message}
            hint={
              form.watch("eventoId")
                ? "Selecione qual sessão receberá o recurso."
                : "Escolha um evento para listar as apresentações."
            }
            required
          >
            <SeletorApresentacao
              id="apresentacaoId"
              eventoId={form.watch("eventoId") || undefined}
              value={form.watch("apresentacaoId") ?? ""}
              onChange={(v) => form.setValue("apresentacaoId", v)}
            />
          </FormField>
          <FormField
            label="Tipo de recurso"
            htmlFor="tipo"
            error={form.formState.errors.tipo?.message}
            required
          >
            <Select id="tipo" {...form.register("tipo")}>
              {TIPOS.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </Select>
          </FormField>
        </form>
      </Modal>

      <Modal
        open={recursoParaRemover !== null}
        onClose={() => {
          formRemocao.reset();
          setRecursoParaRemover(null);
        }}
        title="Remover recurso anunciado"
        description="Espectadores e produtor serão notificados com a justificativa. Mínimo de 30 caracteres."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                formRemocao.reset();
                setRecursoParaRemover(null);
              }}
              disabled={remover.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={formRemocao.handleSubmit(onRemover)}
              disabled={remover.isPending}
              className="bg-destructive hover:bg-destructive/90 text-white"
            >
              {remover.isPending && (
                <LoadingSpinner className="mr-2 text-white" />
              )}
              Remover e notificar
            </Button>
          </>
        }
      >
        {recursoParaRemover && (
          <div className="space-y-3">
            <div className="text-muted-foreground text-sm">
              Recurso{" "}
              <span className="text-foreground font-semibold">
                {tipoLabel(recursoParaRemover.tipo)}
              </span>{" "}
              na apresentação{" "}
              <span className="font-mono text-xs">
                {recursoParaRemover.apresentacaoId.slice(0, 8)}…
              </span>
            </div>
            <FormField
              label="Justificativa"
              htmlFor="justificativa"
              error={formRemocao.formState.errors.justificativa?.message}
              required
            >
              <textarea
                id="justificativa"
                rows={4}
                {...formRemocao.register("justificativa")}
                className="border-border bg-white placeholder:text-muted-foreground focus-visible:border-azul focus-visible:ring-azul/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
                placeholder="O intérprete confirmado teve emergência médica e não há substituto disponível para esta data..."
              />
            </FormField>
          </div>
        )}
      </Modal>
    </PageLayout>
  );
}
