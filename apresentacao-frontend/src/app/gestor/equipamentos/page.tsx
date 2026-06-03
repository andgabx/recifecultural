"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { CircleCheck, Plus, Speaker, Trash2, Wrench } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEspaco } from "@/components/form/Seletores";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useAdquirirEquipamento,
  useEquipamentosPorEspaco,
  useLiberarEquipamento,
  useMarcarManutencao,
  useRemoverEquipamento,
} from "@/hooks/useEquipamentos";
import type { ApiError } from "@/lib/api";
import type {
  EquipamentoResumo,
  StatusEquipamento,
} from "@/services/bff/equipamentos";

const statusLabel: Record<StatusEquipamento, string> = {
  DISPONIVEL: "Disponível",
  ALOCADO: "Alocado",
  EM_MANUTENCAO: "Em manutenção",
};

const statusVariant: Record<
  StatusEquipamento,
  "default" | "success" | "frevo" | "secondary" | "destructive" | "outline"
> = {
  DISPONIVEL: "success",
  ALOCADO: "frevo",
  EM_MANUTENCAO: "secondary",
};

const cadastroSchema = z.object({
  nome: z
    .string()
    .min(2, "Informe o nome do equipamento")
    .max(80, "Nome muito longo"),
});
type CadastroForm = z.infer<typeof cadastroSchema>;

export default function EquipamentosPage() {
  const [espacoId, setEspacoId] = useState<string>("");
  const { data, isLoading, isError } = useEquipamentosPorEspaco(
    espacoId || undefined,
  );
  const adquirir = useAdquirirEquipamento(espacoId || undefined);
  const manutencao = useMarcarManutencao(espacoId || undefined);
  const liberar = useLiberarEquipamento(espacoId || undefined);
  const remover = useRemoverEquipamento(espacoId || undefined);

  const [cadastroAberto, setCadastroAberto] = useState(false);
  const [paraRemover, setParaRemover] = useState<EquipamentoResumo | null>(
    null,
  );

  const form = useForm<CadastroForm>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: { nome: "" },
  });

  async function onAdquirir(values: CadastroForm) {
    if (!espacoId) {
      toast.error("Selecione um espaço primeiro");
      return;
    }
    try {
      await adquirir.mutateAsync({ espacoId, nome: values.nome });
      toast.success(`Equipamento "${values.nome}" cadastrado`);
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function handleManutencao(e: EquipamentoResumo) {
    try {
      await manutencao.mutateAsync(e.id);
      toast.success(
        e.status === "ALOCADO"
          ? "Equipamento em manutenção. Evento alocado foi notificado."
          : "Equipamento em manutenção.",
      );
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function handleLiberar(e: EquipamentoResumo) {
    try {
      await liberar.mutateAsync(e.id);
      toast.success("Equipamento disponível novamente.");
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function confirmarRemocao() {
    if (!paraRemover) return;
    try {
      await remover.mutateAsync(paraRemover.id);
      toast.success("Equipamento removido");
      setParaRemover(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const colunas: Coluna<EquipamentoResumo>[] = [
    {
      header: "Equipamento",
      cell: (e) => (
        <div className="flex items-center gap-2">
          <Speaker className="text-vinho h-4 w-4" />
          <span className="font-medium">{e.nome}</span>
        </div>
      ),
    },
    {
      header: "Status",
      cell: (e) => (
        <Badge variant={statusVariant[e.status]}>{statusLabel[e.status]}</Badge>
      ),
    },
    {
      header: "Evento alocado",
      cell: (e) =>
        e.eventoAlocadoId ? (
          <span className="font-mono text-xs">
            {e.eventoAlocadoId.slice(0, 8)}…
          </span>
        ) : (
          <span className="text-muted-foreground text-xs">—</span>
        ),
    },
    {
      header: "",
      width: "1%",
      cell: (e) => (
        <div className="flex items-center justify-end gap-1">
          {e.status !== "EM_MANUTENCAO" && (
            <Button
              size="icon-sm"
              variant="ghost"
              onClick={() => handleManutencao(e)}
              aria-label="Enviar para manutenção"
              className="text-amber-700 hover:bg-amber-100"
              disabled={manutencao.isPending}
            >
              <Wrench className="h-3.5 w-3.5" />
            </Button>
          )}
          {e.status !== "DISPONIVEL" && (
            <Button
              size="icon-sm"
              variant="ghost"
              onClick={() => handleLiberar(e)}
              aria-label="Liberar equipamento"
              className="text-emerald-700 hover:bg-emerald-100"
              disabled={liberar.isPending}
            >
              <CircleCheck className="h-3.5 w-3.5" />
            </Button>
          )}
          {e.status !== "ALOCADO" && (
            <Button
              size="icon-sm"
              variant="ghost"
              onClick={() => setParaRemover(e)}
              aria-label="Remover"
              className="text-destructive hover:bg-destructive/10"
            >
              <Trash2 className="h-3.5 w-3.5" />
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Equipamentos"
      subtitulo="Inventário por espaço. Manutenção em equipamento alocado notifica o evento."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          disabled={!espacoId}
          className="bg-vinho hover:bg-vinho-light text-marquee"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo equipamento
        </Button>
      }
    >
      <div className="mb-4 max-w-md">
        <FormField label="Espaço" htmlFor="espacoFiltro">
          <SeletorEspaco
            id="espacoFiltro"
            value={espacoId}
            onChange={setEspacoId}
          />
        </FormField>
      </div>

      {!espacoId && (
        <EmptyState
          icon={Speaker}
          title="Selecione um espaço"
          description="O inventário de equipamentos é organizado por espaço físico."
        />
      )}

      {espacoId && isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      )}

      {espacoId && isError && (
        <EmptyState
          icon={Speaker}
          title="Falha ao carregar equipamentos"
          description="Verifique se o backend está rodando."
        />
      )}

      {espacoId && data && (
        <DataTable
          data={data}
          rowKey={(e) => e.id}
          columns={colunas}
          empty={
            <EmptyState
              icon={Speaker}
              title="Nenhum equipamento cadastrado"
              description="Adquira equipamentos para que este espaço possa atender riders técnicos."
              action={
                <Button onClick={() => setCadastroAberto(true)} variant="outline">
                  Novo equipamento
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
        title="Novo equipamento"
        description="Equipamentos ficam vinculados ao espaço selecionado e iniciam DISPONÍVEIS."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setCadastroAberto(false);
              }}
              disabled={adquirir.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onAdquirir)}
              disabled={adquirir.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {adquirir.isPending && (
                <LoadingSpinner className="mr-2 text-marquee" />
              )}
              Adquirir
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Nome"
            htmlFor="nome"
            error={form.formState.errors.nome?.message}
            hint="Ex: Mesa de som Yamaha QL5, Microfone Shure SM58…"
            required
          >
            <Input
              id="nome"
              placeholder="Mesa de som Yamaha QL5"
              {...form.register("nome")}
            />
          </FormField>
        </form>
      </Modal>

      <ConfirmDialog
        open={paraRemover !== null}
        onClose={() => setParaRemover(null)}
        onConfirm={confirmarRemocao}
        title="Remover equipamento?"
        description={
          paraRemover && (
            <p>
              O equipamento{" "}
              <span className="font-semibold">{paraRemover.nome}</span> será
              removido do inventário do espaço. Esta ação não pode ser desfeita.
            </p>
          )
        }
        confirmLabel="Remover"
        dangerous
        loading={remover.isPending}
      />
    </PageLayout>
  );
}
