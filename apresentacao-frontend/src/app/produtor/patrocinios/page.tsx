"use client";

import { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  CheckCircle2,
  Handshake,
  Plus,
  Sparkles,
  XCircle,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEvento } from "@/components/form/Seletores";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useAtivarPatrocinio,
  useCancelarPatrocinioPorEvento,
  useCancelarPatrocinioPorPatrocinador,
  useCriarPatrocinio,
  usePatrociniosPorEvento,
} from "@/hooks/usePatrocinios";
import type { ApiError } from "@/lib/api";
import type { PatrocinioResumo } from "@/services/bff/patrocinios";
import type { StatusPatrocinio } from "@/types/dominio";

const novoSchema = z.object({
  eventoId: z.string().uuid("eventoId inválido"),
  patrocinadorNome: z.string().min(2),
  categoriaPatrocinio: z.string().min(2),
  tipo: z.enum(["MASTER", "ASSOCIADO"]),
  modalidade: z.enum(["FINANCEIRO", "SUBSIDIO_INGRESSO_SOCIAL"]),
  valorContribuicao: z.coerce.number().positive("Valor obrigatório"),
  dataEvento: z.string().min(1, "Data do evento obrigatória"),
});
type NovoFormInput = z.input<typeof novoSchema>;
type NovoFormOutput = z.output<typeof novoSchema>;

const statusVariant: Record<
  StatusPatrocinio,
  "default" | "success" | "violeta" | "destructive" | "outline"
> = {
  PROPOSTA: "violeta",
  ATIVO: "success",
  ENCERRADO: "outline",
  CANCELADO_EVENTO: "destructive",
  CANCELADO_PATROCINADOR: "destructive",
};

const statusLabel: Record<StatusPatrocinio, string> = {
  PROPOSTA: "Proposta",
  ATIVO: "Ativo",
  ENCERRADO: "Encerrado",
  CANCELADO_EVENTO: "Cancelado (evento)",
  CANCELADO_PATROCINADOR: "Cancelado (patrocinador)",
};

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);

export default function PatrociniosPage() {
  const [eventoId, setEventoId] = useState("");
  const [eventoConsultado, setEventoConsultado] = useState<string | undefined>(
    undefined,
  );
  const [novoAberto, setNovoAberto] = useState(false);
  const [cancelar, setCancelar] = useState<
    | { tipo: "evento" | "patrocinador"; patrocinio: PatrocinioResumo }
    | null
  >(null);

  const { data, isLoading, isError } = usePatrociniosPorEvento(eventoConsultado);
  const criar = useCriarPatrocinio(eventoConsultado);
  const ativar = useAtivarPatrocinio(eventoConsultado);
  const cancelarPorEvento = useCancelarPatrocinioPorEvento(eventoConsultado);
  const cancelarPorPatrocinador =
    useCancelarPatrocinioPorPatrocinador(eventoConsultado);

  const form = useForm<NovoFormInput, unknown, NovoFormOutput>({
    resolver: zodResolver(novoSchema),
    defaultValues: {
      eventoId: "",
      patrocinadorNome: "",
      categoriaPatrocinio: "",
      tipo: "MASTER",
      modalidade: "FINANCEIRO",
      valorContribuicao: 0,
      dataEvento: "",
    },
  });

  function consultar() {
    if (eventoId) setEventoConsultado(eventoId);
  }

  async function onCriar(values: NovoFormOutput) {
    try {
      // Remove o 'Z' do ISO — backend espera LocalDateTime sem timezone
      const dataIso = new Date(values.dataEvento).toISOString().slice(0, 19);
      await criar.mutateAsync({
        ...values,
        dataEvento: dataIso,
        eventoAprovado: true, // dropdown só lista APROVADO
      });
      toast.success("Patrocínio criado (status PROPOSTA)");
      form.reset();
      setNovoAberto(false);
      if (!eventoConsultado) setEventoConsultado(values.eventoId);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onAtivar(p: PatrocinioResumo) {
    try {
      const resultado = await ativar.mutateAsync(p.id);
      if (resultado && resultado.novoPrecoSocial != null) {
        const precoFormatado = formatarMoeda(resultado.novoPrecoSocial);
        toast.success(
          `"${p.patrocinadorNome}" ativado. Subsídio aplicado: novo preço social = ${precoFormatado}${resultado.pisoAplicado ? " (piso mínimo aplicado)" : ""}.`,
        );
      } else {
        toast.success(`"${p.patrocinadorNome}" ativado`);
      }
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onConfirmarCancelamento() {
    if (!cancelar) return;
    const fn =
      cancelar.tipo === "evento" ? cancelarPorEvento : cancelarPorPatrocinador;
    try {
      const resultado = await fn.mutateAsync(cancelar.patrocinio.id);
      toast.success(
        `Cancelado. Reembolso ${formatarMoeda(resultado.valorReembolsado)}${resultado.multaAplicada > 0 ? `, multa ${formatarMoeda(resultado.multaAplicada)}` : ""}${resultado.motivo ? ` — ${resultado.motivo}` : ""}.`,
      );
      setCancelar(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Patrocínios"
      subtitulo="Cadastro e gestão de patrocinadores por evento."
      acoes={
        <Button
          onClick={() => {
            form.reset({
              eventoId: eventoConsultado ?? "",
              patrocinadorNome: "",
              categoriaPatrocinio: "",
              tipo: "MASTER",
              modalidade: "FINANCEIRO",
              valorContribuicao: 0,
              dataEvento: "",
            });
            setNovoAberto(true);
          }}
          className="bg-azul hover:bg-azul-light text-nevoa"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo patrocínio
        </Button>
      }
    >
      <Card className="space-y-3 p-5">
        <FormField
          label="Evento aprovado"
          htmlFor="eventoIdConsulta"
          hint="Apenas eventos aprovados aceitam patrocínio."
        >
          <SeletorEvento
            id="eventoIdConsulta"
            value={eventoId}
            onChange={(v) => {
              setEventoId(v);
              if (v) setEventoConsultado(v);
            }}
            status="APROVADO"
          />
        </FormField>
      </Card>

      {isLoading && eventoConsultado && (
        <div className="grid gap-3 sm:grid-cols-2">
          {Array.from({ length: 2 }).map((_, i) => (
            <Skeleton key={i} className="h-32 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Handshake}
          title="Falha ao carregar patrocínios"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && data.length === 0 && eventoConsultado && (
        <EmptyState
          icon={Handshake}
          title="Sem patrocínios para este evento"
          description="Cadastre o primeiro patrocinador para começar."
        />
      )}

      {data && data.length > 0 && (
        <div className="grid gap-3 sm:grid-cols-2">
          {data.map((p) => (
            <Card key={p.id} className="space-y-3 p-5">
              <div className="flex items-start justify-between gap-2">
                <div>
                  <p className="text-muted-foreground text-xs uppercase tracking-widest">
                    {p.tipo} · {p.modalidade}
                  </p>
                  <h3 className="font-display text-noite mt-0.5 text-lg font-semibold">
                    {p.patrocinadorNome}
                  </h3>
                  <p className="text-muted-foreground text-xs">
                    Categoria: <strong>{p.categoriaPatrocinio}</strong>
                  </p>
                </div>
                <Badge variant={statusVariant[p.status]}>
                  {statusLabel[p.status]}
                </Badge>
              </div>
              <div className="text-azul font-mono text-lg font-bold">
                {p.valorContribuicao != null ? formatarMoeda(Number(p.valorContribuicao)) : "—"}
              </div>
              {p.valorReembolsado != null && Number(p.valorReembolsado) > 0 && (
                <p className="text-muted-foreground text-xs">
                  Reembolsado: {formatarMoeda(Number(p.valorReembolsado))}
                  {p.multaAplicada != null && Number(p.multaAplicada) > 0 &&
                    ` · multa ${formatarMoeda(Number(p.multaAplicada))}`}
                </p>
              )}
              <div className="flex flex-wrap gap-2 pt-2">
                {p.status === "PROPOSTA" && (
                  <Button
                    size="sm"
                    onClick={() => onAtivar(p)}
                    disabled={ativar.isPending}
                    className="bg-emerald-600 text-white hover:bg-emerald-700"
                  >
                    <CheckCircle2 className="mr-1 h-3 w-3" />
                    Ativar
                  </Button>
                )}
                {p.status === "ATIVO" && (
                  <>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => setCancelar({ tipo: "evento", patrocinio: p })}
                      className="border-destructive/40 text-destructive hover:bg-destructive/10"
                    >
                      <XCircle className="mr-1 h-3 w-3" />
                      Cancelar (evento)
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() =>
                        setCancelar({ tipo: "patrocinador", patrocinio: p })
                      }
                      className="border-violeta/40 text-yellow-800 hover:bg-violeta/10"
                    >
                      <Sparkles className="mr-1 h-3 w-3" />
                      Cancelar (patrocinador)
                    </Button>
                  </>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}

      <Modal
        open={novoAberto}
        onClose={() => {
          form.reset();
          setNovoAberto(false);
        }}
        title="Novo patrocínio"
        description="Cria a proposta. Ativação acontece depois pelo botão Ativar."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setNovoAberto(false);
              }}
              disabled={criar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onCriar)}
              disabled={criar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {criar.isPending && <LoadingSpinner className="mr-2 text-nevoa" />}
              Cadastrar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Evento aprovado"
            htmlFor="eventoIdNovo"
            hint="Só aparecem eventos aprovados pelo gestor."
            error={form.formState.errors.eventoId?.message}
            required
          >
            <Controller
              control={form.control}
              name="eventoId"
              render={({ field }) => (
                <SeletorEvento
                  id="eventoIdNovo"
                  value={field.value ?? ""}
                  onChange={field.onChange}
                  status="APROVADO"
                />
              )}
            />
          </FormField>
          <FormField
            label="Nome do patrocinador"
            htmlFor="patrocinadorNome"
            error={form.formState.errors.patrocinadorNome?.message}
            required
          >
            <Input
              id="patrocinadorNome"
              {...form.register("patrocinadorNome")}
            />
          </FormField>
          <FormField
            label="Categoria"
            htmlFor="categoriaPatrocinio"
            hint="Ex: Bebidas, Telecom, Serviços"
            error={form.formState.errors.categoriaPatrocinio?.message}
            required
          >
            <Input
              id="categoriaPatrocinio"
              {...form.register("categoriaPatrocinio")}
            />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField label="Tipo" htmlFor="tipo" required>
              <Select id="tipo" {...form.register("tipo")}>
                <option value="MASTER">Master (naming rights, exclusivo)</option>
                <option value="ASSOCIADO">Associado</option>
              </Select>
            </FormField>
            <FormField label="Modalidade" htmlFor="modalidade" required>
              <Select id="modalidade" {...form.register("modalidade")}>
                <option value="FINANCEIRO">Financeiro</option>
                <option value="SUBSIDIO_INGRESSO_SOCIAL">
                  Subsídio ingresso social
                </option>
              </Select>
            </FormField>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Valor (R$)"
              htmlFor="valorContribuicao"
              error={form.formState.errors.valorContribuicao?.message}
              required
            >
              <Input
                id="valorContribuicao"
                type="number"
                step="0.01"
                min={0.01}
                {...form.register("valorContribuicao")}
              />
            </FormField>
            <FormField
              label="Data do evento"
              htmlFor="dataEvento"
              error={form.formState.errors.dataEvento?.message}
              required
            >
              <Input
                id="dataEvento"
                type="datetime-local"
                {...form.register("dataEvento")}
              />
            </FormField>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!cancelar}
        onClose={() => setCancelar(null)}
        onConfirm={onConfirmarCancelamento}
        title={
          cancelar?.tipo === "evento"
            ? "Cancelar pelo evento"
            : "Cancelar pelo patrocinador"
        }
        confirmLabel="Cancelar patrocínio"
        cancelLabel="Voltar"
        dangerous
        loading={
          cancelarPorEvento.isPending || cancelarPorPatrocinador.isPending
        }
        description={
          cancelar && (
            <div className="space-y-2 text-sm">
              <p>
                Patrocínio: <strong>{cancelar.patrocinio.patrocinadorNome}</strong>
                {cancelar.patrocinio.valorContribuicao != null &&
                  ` (${formatarMoeda(Number(cancelar.patrocinio.valorContribuicao))})`}
              </p>
              <p className="text-muted-foreground text-xs">
                {cancelar.tipo === "evento"
                  ? "Política: > 7 dias → 100% · 2–7 dias → 50% · < 2 dias → 0%"
                  : "Política: > 15 dias → 100% · ≤ 15 dias → 80% + 20% multa"}
              </p>
            </div>
          )
        }
      />
    </PageLayout>
  );
}
