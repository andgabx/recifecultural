"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  AlertTriangle,
  CalendarDays,
  PercentCircle,
  Plus,
  Receipt,
  TrendingUp,
  Wallet,
  type LucideIcon,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useIndicadoresFinanceiros,
  useRegistrarDespesa,
} from "@/hooks/useFinanceiro";
import type { ApiError } from "@/lib/api";

const inicioPadrao = (() => {
  const d = new Date();
  d.setDate(1);
  return d.toISOString().slice(0, 10);
})();
const fimPadrao = (() => {
  const d = new Date();
  d.setMonth(d.getMonth() + 1, 0);
  return d.toISOString().slice(0, 10);
})();

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);
const formatarPercentual = (v: number) =>
  new Intl.NumberFormat("pt-BR", {
    style: "percent",
    maximumFractionDigits: 1,
  }).format(v);

const despesaSchema = z.object({
  orcamentoId: z.string().uuid("Id do orçamento inválido"),
  descricao: z.string().min(3, "Descrição muito curta"),
  valor: z.coerce.number().positive("Valor deve ser maior que zero"),
  categoria: z.enum(["INFRAESTRUTURA", "PESSOAL", "MARKETING", "OUTROS"]),
});
type DespesaFormInput = z.input<typeof despesaSchema>;
type DespesaFormOutput = z.output<typeof despesaSchema>;

export default function FinanceiroPage() {
  const [periodoInicio, setPeriodoInicio] = useState(inicioPadrao);
  const [periodoFim, setPeriodoFim] = useState(fimPadrao);
  const [despesaAberto, setDespesaAberto] = useState(false);

  const { data, isLoading, isError } = useIndicadoresFinanceiros(
    periodoInicio ? `${periodoInicio}T00:00:00` : undefined,
    periodoFim ? `${periodoFim}T23:59:59` : undefined,
  );
  const registrar = useRegistrarDespesa();

  const form = useForm<DespesaFormInput, unknown, DespesaFormOutput>({
    resolver: zodResolver(despesaSchema),
    defaultValues: {
      orcamentoId: "",
      descricao: "",
      valor: 0,
      categoria: "INFRAESTRUTURA",
    },
  });

  async function onRegistrar(values: DespesaFormOutput) {
    try {
      const resposta = await registrar.mutateAsync(values);
      toast.success(
        resposta.alertaOrcamento
          ? `Despesa registrada. Atenção: orçamento próximo do limite (saldo ${formatarMoeda(resposta.saldoRestante)}).`
          : "Despesa registrada com sucesso",
      );
      form.reset();
      setDespesaAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Financeiro"
      subtitulo="Indicadores de desempenho e controle de despesas."
      acoes={
        <Button
          onClick={() => setDespesaAberto(true)}
          className="bg-azul hover:bg-azul-light text-nevoa"
        >
          <Plus className="mr-1 h-4 w-4" />
          Nova despesa
        </Button>
      }
    >
      <Card className="grid gap-4 p-5 sm:grid-cols-2">
        <FormField label="Período — início" htmlFor="periodoInicio">
          <Input
            id="periodoInicio"
            type="date"
            value={periodoInicio}
            onChange={(e) => setPeriodoInicio(e.target.value)}
          />
        </FormField>
        <FormField label="Período — fim" htmlFor="periodoFim">
          <Input
            id="periodoFim"
            type="date"
            value={periodoFim}
            onChange={(e) => setPeriodoFim(e.target.value)}
          />
        </FormField>
      </Card>

      {isLoading && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-28 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Wallet}
          title="Falha ao carregar indicadores"
          description="Verifique se o backend está rodando e se o período é válido."
        />
      )}

      {data && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Indicador
            icon={PercentCircle}
            label="Ocupação"
            valor={formatarPercentual(data.ocupacao)}
          />
          <Indicador
            icon={TrendingUp}
            label="Receita bruta"
            valor={formatarMoeda(data.receitaBruta)}
          />
          <Indicador
            icon={Wallet}
            label="Receita líquida"
            valor={formatarMoeda(data.receitaLiquida)}
          />
          <Indicador
            icon={Receipt}
            label="Despesas"
            valor={formatarMoeda(data.totalDespesas)}
            alerta={data.totalDespesas > data.receitaBruta * 0.8}
          />
        </div>
      )}

      <Card className="border-dashed border-laranja/30 bg-laranja/5 p-5">
        <h2 className="font-display text-noite text-sm font-semibold">
          Próximos incrementos
        </h2>
        <p className="text-muted-foreground mt-1 text-xs">
          Tabela de despesas, gráfico de receita por mês (recharts) e
          comparativo com período anterior ainda dependem de endpoints adicionais
          no BFF (<code className="font-mono">GET /financeiro/despesas</code>,
          <code className="font-mono"> GET /financeiro/comparativo</code>).
        </p>
      </Card>

      <Modal
        open={despesaAberto}
        onClose={() => {
          form.reset();
          setDespesaAberto(false);
        }}
        title="Registrar despesa"
        description="A despesa é debitada do orçamento informado."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setDespesaAberto(false);
              }}
              disabled={registrar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onRegistrar)}
              disabled={registrar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {registrar.isPending && <LoadingSpinner className="mr-2 text-nevoa" />}
              Registrar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Id do orçamento"
            htmlFor="orcamentoId"
            error={form.formState.errors.orcamentoId?.message}
            required
          >
            <Input id="orcamentoId" {...form.register("orcamentoId")} />
          </FormField>
          <FormField
            label="Descrição"
            htmlFor="descricao"
            error={form.formState.errors.descricao?.message}
            required
          >
            <Input id="descricao" {...form.register("descricao")} />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Valor (R$)"
              htmlFor="valor"
              error={form.formState.errors.valor?.message}
              required
            >
              <Input
                id="valor"
                type="number"
                step="0.01"
                min={0.01}
                {...form.register("valor")}
              />
            </FormField>
            <FormField label="Categoria" htmlFor="categoria" required>
              <Select id="categoria" {...form.register("categoria")}>
                <option value="INFRAESTRUTURA">Infraestrutura</option>
                <option value="PESSOAL">Pessoal</option>
                <option value="MARKETING">Marketing</option>
                <option value="OUTROS">Outros</option>
              </Select>
            </FormField>
          </div>
        </form>
      </Modal>
    </PageLayout>
  );
}

function Indicador({
  icon: Icon,
  label,
  valor,
  alerta,
}: {
  icon: LucideIcon;
  label: string;
  valor: string;
  alerta?: boolean;
}) {
  return (
    <Card className="p-5">
      <div className="flex items-start gap-3">
        <span
          className={
            alerta
              ? "bg-destructive/10 text-destructive rounded-lg p-2"
              : "bg-azul/10 text-azul rounded-lg p-2"
          }
        >
          <Icon className="h-5 w-5" />
        </span>
        <div>
          <p className="text-muted-foreground text-xs uppercase tracking-wide">
            {label}
          </p>
          <p className="font-display text-noite mt-1 text-2xl font-bold">
            {valor}
          </p>
        </div>
      </div>
      {alerta && (
        <Badge variant="destructive" className="mt-3">
          <AlertTriangle className="mr-1 h-3 w-3" />
          Acima de 80% da receita
        </Badge>
      )}
    </Card>
  );
}
