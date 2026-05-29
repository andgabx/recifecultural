"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { CalendarOff, Plus, Unlock } from "lucide-react";
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
} from "@/hooks/useBloqueios";
import type { ApiError } from "@/lib/api";
import type { BloqueioResumo } from "@/services/bff/bloqueios";

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

  async function onSubmit(values: CadastroForm) {
    try {
      await cadastrar.mutateAsync(values);
      toast.success("Bloqueio criado. Eventos conflitantes foram cancelados.");
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function handleDesativar(bloqueio: BloqueioResumo) {
    try {
      await desativar.mutateAsync(bloqueio.id);
      toast.success("Bloqueio desativado");
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

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
            onClick={() => handleDesativar(b)}
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
        <DataTable
          data={data}
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
      )}

      <Modal
        open={cadastroAberto}
        onClose={() => {
          form.reset();
          setCadastroAberto(false);
        }}
        title="Novo bloqueio"
        description="Eventos no período serão cancelados automaticamente e os promotores notificados (Observer F3.1 → F3.2)."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setCadastroAberto(false);
              }}
              disabled={cadastrar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onSubmit)}
              disabled={cadastrar.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {cadastrar.isPending && (
                <LoadingSpinner className="mr-2 text-marquee" />
              )}
              Bloquear
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
            hint="Mín. 10 caracteres. Será exibida nas notificações aos produtores afetados."
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
    </PageLayout>
  );
}
