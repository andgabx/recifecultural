"use client";

import { createPortal } from "react-dom";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  CircleCheck,
  Plus,
  Speaker,
  Trash2,
  Wrench,
} from "lucide-react";
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
import { useEventos } from "@/hooks/useEventos";
import type { ApiError } from "@/lib/api";
import { cn } from "@/lib/utils";
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
  "default" | "success" | "violeta" | "secondary" | "destructive" | "outline"
> = {
  DISPONIVEL: "success",
  ALOCADO: "violeta",
  EM_MANUTENCAO: "secondary",
};

const cadastroSchema = z.object({
  nome: z
    .string()
    .min(2, "Informe o nome do equipamento")
    .max(80, "Nome muito longo"),
});
type CadastroForm = z.infer<typeof cadastroSchema>;

const DIAS_SEMANA = ["D", "S", "T", "Q", "Q", "S", "S"];

const MESES = [
  "janeiro", "fevereiro", "março", "abril", "maio", "junho",
  "julho", "agosto", "setembro", "outubro", "novembro", "dezembro",
];

function statusEfetivo(e: EquipamentoResumo): StatusEquipamento {
  if (e.status === "ALOCADO" && e.alocacaoInicio && e.alocacaoFim) {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const inicio = new Date(e.alocacaoInicio + "T00:00:00");
    const fim = new Date(e.alocacaoFim + "T00:00:00");
    if (hoje < inicio || hoje > fim) return "DISPONIVEL";
  }
  return e.status;
}

function MiniCalendario({
  inicio,
  fim,
  onClose,
  anchorEl,
}: {
  inicio: string;
  fim: string;
  onClose: () => void;
  anchorEl: HTMLElement | null;
}) {
  const inicioDate = new Date(inicio + "T00:00:00");
  const fimDate = new Date(fim + "T00:00:00");

  const [mesAtual, setMesAtual] = useState(
    () => new Date(inicioDate.getFullYear(), inicioDate.getMonth(), 1),
  );

  const ref = useRef<HTMLDivElement>(null);

  const [pos, setPos] = useState<{ top: number; left: number } | null>(null);

  useEffect(() => {
    if (anchorEl) {
      const rect = anchorEl.getBoundingClientRect();
      setPos({
        top: rect.bottom + 4 + window.scrollY,
        left: Math.max(rect.right - 240 + window.scrollX, 8),
      });
    }
  }, [anchorEl]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      const target = e.target as Node;
      const dentroCalendario = ref.current?.contains(target);
      const dentroAnchor = anchorEl?.contains(target);
      if (!dentroCalendario && !dentroAnchor) {
        onClose();
      }
    }
    const id = setTimeout(() => {
      document.addEventListener("mousedown", handleClickOutside);
    }, 0);
    return () => {
      clearTimeout(id);
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose, anchorEl]);

  const ano = mesAtual.getFullYear();
  const mes = mesAtual.getMonth();
  const primeiroDia = new Date(ano, mes, 1).getDay();
  const diasNoMes = new Date(ano, mes + 1, 0).getDate();

  function isAlocado(dia: number) {
    const data = new Date(ano, mes, dia);
    return data >= inicioDate && data <= fimDate;
  }

  function isHoje(dia: number) {
    const hoje = new Date();
    return (
      dia === hoje.getDate() &&
      mes === hoje.getMonth() &&
      ano === hoje.getFullYear()
    );
  }

  if (!pos) return null;

  return createPortal(
    <div
      ref={ref}
      style={{ top: pos.top, left: pos.left }}
      className="fixed z-50 w-60 rounded-xl border bg-nevoa-card p-3 shadow-lg"
    >
      <div className="mb-2 flex items-center justify-between">
        <button
          type="button"
          onClick={() => setMesAtual(new Date(ano, mes - 1, 1))}
          className="rounded p-0.5 hover:bg-nevoa-muted"
        >
          <ChevronLeft className="h-3.5 w-3.5" />
        </button>
        <span className="text-xs font-semibold capitalize">
          {MESES[mes]} {ano}
        </span>
        <button
          type="button"
          onClick={() => setMesAtual(new Date(ano, mes + 1, 1))}
          className="rounded p-0.5 hover:bg-nevoa-muted"
        >
          <ChevronRight className="h-3.5 w-3.5" />
        </button>
      </div>

      <div className="mb-1 grid grid-cols-7 gap-0.5">
        {DIAS_SEMANA.map((d, i) => (
          <div
            key={i}
            className="text-muted-foreground text-center text-[10px]"
          >
            {d}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-7 gap-0.5">
        {Array.from({ length: primeiroDia }).map((_, i) => (
          <div key={"v" + i} />
        ))}
        {Array.from({ length: diasNoMes }).map((_, i) => {
          const dia = i + 1;
          const alocado = isAlocado(dia);
          const hoje = isHoje(dia);
          return (
            <div
              key={dia}
              className={cn(
                "flex h-6 w-6 items-center justify-center rounded-full text-[11px]",
                alocado && "bg-violeta-dark font-semibold text-white",
                !alocado && hoje && "ring-1 ring-azul font-semibold",
                !alocado && !hoje && "text-foreground",
              )}
            >
              {dia}
            </div>
          );
        })}
      </div>

      <div className="mt-2 flex items-center gap-1.5 border-t pt-2">
        <div className="h-2.5 w-2.5 rounded-full bg-violeta-dark" />
        <span className="text-muted-foreground text-[10px]">Alocado</span>
      </div>
    </div>,
    document.body,
  );
}

function CalendarioAlocacao({
  inicio,
  fim,
}: {
  inicio: string;
  fim: string;
}) {
  const [aberto, setAberto] = useState(false);
  const btnRef = useRef<HTMLButtonElement>(null);

  return (
    <>
      <Button
        ref={btnRef}
        size="icon-sm"
        variant="ghost"
        onClick={() => setAberto((v) => !v)}
        aria-label="Ver calendário de alocação"
        className="text-violeta-dark hover:bg-violeta"
      >
        <CalendarDays className="h-3.5 w-3.5" />
      </Button>
      {aberto && (
        <MiniCalendario
          inicio={inicio}
          fim={fim}
          onClose={() => setAberto(false)}
          anchorEl={btnRef.current}
        />
      )}
    </>
  );
}

export default function EquipamentosPage() {
  const [espacoId, setEspacoId] = useState<string>("");
  const { data, isLoading, isError } = useEquipamentosPorEspaco(
    espacoId || undefined,
  );
  const adquirir = useAdquirirEquipamento(espacoId || undefined);
  const manutencao = useMarcarManutencao(espacoId || undefined);
  const liberar = useLiberarEquipamento(espacoId || undefined);
  const remover = useRemoverEquipamento(espacoId || undefined);

  const { data: eventos } = useEventos();
  const tituloEvento = (id: string | null | undefined) => {
    if (!id) return null;
    return eventos?.find((e) => e.id === id)?.titulo ?? id.slice(0, 8) + "…";
  };

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
          <Speaker className="text-azul h-4 w-4" />
          <span className="font-medium">{e.nome}</span>
        </div>
      ),
    },
    {
      header: "Status",
      cell: (e) => {
        const s = statusEfetivo(e);
        return <Badge variant={statusVariant[s]}>{statusLabel[s]}</Badge>;
      },
    },
    {
      header: "Evento alocado",
      cell: (e) =>
        e.eventoAlocadoId ? (
          <span className="text-xs">{tituloEvento(e.eventoAlocadoId)}</span>
        ) : (
          <span className="text-muted-foreground text-xs">—</span>
        ),
    },
    {
      header: "",
      width: "1%",
      cell: (e) => (
        <div className="flex items-center justify-end gap-1">
          {e.status === "ALOCADO" && e.alocacaoInicio && e.alocacaoFim && (
            <CalendarioAlocacao inicio={e.alocacaoInicio} fim={e.alocacaoFim} />
          )}
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
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => setParaRemover(e)}
            aria-label="Remover"
            className="text-destructive hover:bg-destructive/10"
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
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
          className="bg-azul hover:bg-azul-light text-nevoa"
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
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {adquirir.isPending && (
                <LoadingSpinner className="mr-2 text-nevoa" />
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
            <div className="space-y-2">
              {paraRemover.status === "ALOCADO" && (
                <p className="text-destructive font-medium">
                  Este equipamento está alocado a um evento. O produtor será notificado.
                </p>
              )}
              <p>
                O equipamento{" "}
                <span className="font-semibold">{paraRemover.nome}</span> será
                removido do inventário do espaço. Esta ação não pode ser desfeita.
              </p>
            </div>
          )
        }
        confirmLabel="Remover"
        dangerous
        loading={remover.isPending}
      />
    </PageLayout>
  );
}
