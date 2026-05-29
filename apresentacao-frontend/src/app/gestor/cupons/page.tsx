"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Plus, Tag, Trash2 } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useCriarCupom,
  useCupons,
  useDeletarCupom,
} from "@/hooks/useCupons";
import type { ApiError } from "@/lib/api";
import type { CupomResumo } from "@/services/bff/cupons";

const cadastroSchema = z
  .object({
    codigo: z
      .string()
      .min(3, "Código deve ter ao menos 3 caracteres")
      .max(40, "Código muito longo"),
    tipoDesconto: z.enum(["PERCENTUAL", "VALOR_FIXO"]),
    valorDesconto: z.number().positive("Valor de desconto deve ser positivo"),
    valorMinimoPedido: z.number().min(0, "Valor mínimo não pode ser negativo"),
    limiteGlobal: z.number().int().positive("Limite global deve ser maior que zero"),
    limitePorCpf: z.number().int().positive("Limite por CPF deve ser maior que zero"),
    dataInicio: z.string().min(1, "Informe a data de início"),
    dataFim: z.string().min(1, "Informe a data de fim"),
    categoriaPermitida: z.string().optional(),
  })
  .refine(
    (v) =>
      v.tipoDesconto === "VALOR_FIXO" ||
      (v.valorDesconto > 0 && v.valorDesconto <= 100),
    {
      path: ["valorDesconto"],
      message: "Percentual deve ser entre 1 e 100",
    },
  );

type CadastroForm = z.infer<typeof cadastroSchema>;

const formatarData = (iso: string) =>
  new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(new Date(iso));

const formatarMoeda = (valor: number) =>
  new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(valor);

const descreverDesconto = (cupom: CupomResumo) =>
  cupom.tipoDesconto === "PERCENTUAL"
    ? `${cupom.valorDesconto}%`
    : formatarMoeda(cupom.valorDesconto);

export default function CuponsPage() {
  const { data, isLoading, isError } = useCupons();
  const criar = useCriarCupom();
  const deletar = useDeletarCupom();
  const [cadastroAberto, setCadastroAberto] = useState(false);
  const [cupomParaExcluir, setCupomParaExcluir] = useState<CupomResumo | null>(null);

  const form = useForm<CadastroForm>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: {
      codigo: "",
      tipoDesconto: "PERCENTUAL",
      valorDesconto: 10,
      valorMinimoPedido: 0,
      limiteGlobal: 100,
      limitePorCpf: 1,
      dataInicio: "",
      dataFim: "",
      categoriaPermitida: "",
    },
  });

  async function onSubmit(values: CadastroForm) {
    try {
      await criar.mutateAsync({
        codigo: values.codigo.toUpperCase().trim(),
        tipoDesconto: values.tipoDesconto,
        valorDesconto: values.valorDesconto,
        valorMinimoPedido: values.valorMinimoPedido,
        limiteGlobal: values.limiteGlobal,
        limitePorCpf: values.limitePorCpf,
        dataInicio: `${values.dataInicio}T00:00:00`,
        dataFim: `${values.dataFim}T23:59:59`,
        categoriaPermitida: values.categoriaPermitida?.trim() || null,
      });
      toast.success("Cupom criado com sucesso");
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function confirmarExclusao() {
    if (!cupomParaExcluir) return;
    try {
      await deletar.mutateAsync(cupomParaExcluir.id);
      toast.success("Cupom removido");
      setCupomParaExcluir(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const colunas: Coluna<CupomResumo>[] = [
    {
      header: "Código",
      cell: (c) => (
        <span className="font-mono text-sm font-semibold uppercase">
          {c.codigo}
        </span>
      ),
    },
    {
      header: "Desconto",
      cell: (c) => (
        <Badge variant="outline" className="text-vinho border-vinho/40">
          {descreverDesconto(c)}
        </Badge>
      ),
    },
    {
      header: "Pedido mín.",
      cell: (c) =>
        c.valorMinimoPedido > 0 ? formatarMoeda(c.valorMinimoPedido) : "—",
    },
    {
      header: "Usos",
      cell: (c) => (
        <span className="text-sm">
          <span className="font-semibold">{c.usosGlobais}</span>
          <span className="text-muted-foreground"> / {c.limiteGlobal}</span>
        </span>
      ),
    },
    {
      header: "Vigência",
      cell: (c) => (
        <span className="text-sm">
          {formatarData(c.dataInicio)} → {formatarData(c.dataFim)}
        </span>
      ),
    },
    {
      header: "Categoria",
      cell: (c) =>
        c.categoriaPermitida ? (
          <Badge variant="secondary">{c.categoriaPermitida}</Badge>
        ) : (
          <span className="text-muted-foreground text-xs">Todas</span>
        ),
    },
    {
      header: "",
      width: "1%",
      cell: (c) => (
        <Button
          size="icon-sm"
          variant="ghost"
          onClick={() => setCupomParaExcluir(c)}
          aria-label="Remover cupom"
          className="text-destructive hover:bg-destructive/10"
        >
          <Trash2 className="h-3.5 w-3.5" />
        </Button>
      ),
    },
  ];

  const tipoSelecionado = form.watch("tipoDesconto");

  return (
    <PageLayout
      titulo="Cupons"
      subtitulo="Códigos promocionais aplicáveis no checkout (Template Method F7.1)."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          className="bg-vinho hover:bg-vinho-light text-marquee"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo cupom
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
          icon={Tag}
          title="Falha ao carregar cupons"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && (
        <DataTable
          data={data}
          rowKey={(c) => c.id}
          columns={colunas}
          empty={
            <EmptyState
              icon={Tag}
              title="Nenhum cupom cadastrado"
              description="Crie cupons promocionais para que espectadores apliquem no checkout."
              action={
                <Button onClick={() => setCadastroAberto(true)} variant="outline">
                  Novo cupom
                </Button>
              }
            />
          }
        />
      )}

      <Modal
        open={cadastroAberto}
        onClose={() => {
          form.reset();
          setCadastroAberto(false);
        }}
        title="Novo cupom"
        description="O código será validado em pipeline (vigência → categoria → mínimo → limites) ao ser aplicado no checkout."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setCadastroAberto(false);
              }}
              disabled={criar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onSubmit)}
              disabled={criar.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {criar.isPending && (
                <LoadingSpinner className="mr-2 text-marquee" />
              )}
              Criar cupom
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Código"
            htmlFor="codigo"
            error={form.formState.errors.codigo?.message}
            hint="Convertido para maiúsculas automaticamente."
            required
          >
            <Input
              id="codigo"
              placeholder="FREVO2026"
              className="font-mono uppercase"
              {...form.register("codigo")}
            />
          </FormField>

          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Tipo de desconto"
              htmlFor="tipoDesconto"
              error={form.formState.errors.tipoDesconto?.message}
              required
            >
              <Select id="tipoDesconto" {...form.register("tipoDesconto")}>
                <option value="PERCENTUAL">Percentual (%)</option>
                <option value="VALOR_FIXO">Valor fixo (R$)</option>
              </Select>
            </FormField>
            <FormField
              label={
                tipoSelecionado === "PERCENTUAL"
                  ? "Desconto (%)"
                  : "Desconto (R$)"
              }
              htmlFor="valorDesconto"
              error={form.formState.errors.valorDesconto?.message}
              required
            >
              <Input
                id="valorDesconto"
                type="number"
                step={tipoSelecionado === "PERCENTUAL" ? "1" : "0.01"}
                min="0"
                {...form.register("valorDesconto", { valueAsNumber: true })}
              />
            </FormField>
          </div>

          <FormField
            label="Valor mínimo do pedido (R$)"
            htmlFor="valorMinimoPedido"
            error={form.formState.errors.valorMinimoPedido?.message}
            hint="Use 0 para não exigir mínimo."
          >
            <Input
              id="valorMinimoPedido"
              type="number"
              step="0.01"
              min="0"
              {...form.register("valorMinimoPedido", { valueAsNumber: true })}
            />
          </FormField>

          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Limite global"
              htmlFor="limiteGlobal"
              error={form.formState.errors.limiteGlobal?.message}
              hint="Quantos usos totais o cupom permite."
              required
            >
              <Input
                id="limiteGlobal"
                type="number"
                min="1"
                {...form.register("limiteGlobal", { valueAsNumber: true })}
              />
            </FormField>
            <FormField
              label="Limite por CPF"
              htmlFor="limitePorCpf"
              error={form.formState.errors.limitePorCpf?.message}
              hint="Quantas vezes o mesmo CPF pode usar."
              required
            >
              <Input
                id="limitePorCpf"
                type="number"
                min="1"
                {...form.register("limitePorCpf", { valueAsNumber: true })}
              />
            </FormField>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Início da vigência"
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
              label="Fim da vigência"
              htmlFor="dataFim"
              error={form.formState.errors.dataFim?.message}
              required
            >
              <Input id="dataFim" type="date" {...form.register("dataFim")} />
            </FormField>
          </div>

          <FormField
            label="Categoria permitida"
            htmlFor="categoriaPermitida"
            error={form.formState.errors.categoriaPermitida?.message}
            hint="Deixe em branco para liberar para qualquer categoria."
          >
            <Input
              id="categoriaPermitida"
              placeholder="MUSICA, TEATRO..."
              {...form.register("categoriaPermitida")}
            />
          </FormField>
        </form>
      </Modal>

      <ConfirmDialog
        open={cupomParaExcluir !== null}
        onClose={() => setCupomParaExcluir(null)}
        onConfirm={confirmarExclusao}
        title="Remover cupom?"
        description={
          cupomParaExcluir && (
            <p>
              O cupom{" "}
              <span className="font-mono font-semibold uppercase">
                {cupomParaExcluir.codigo}
              </span>{" "}
              será removido permanentemente. Esta ação não pode ser desfeita.
            </p>
          )
        }
        confirmLabel="Remover"
        dangerous
        loading={deletar.isPending}
      />
    </PageLayout>
  );
}
