"use client";

import { useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
import { CalendarDays, QrCode, RefreshCcw, TicketCheck } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { QRCodeDisplay } from "@/components/domain/QRCodeDisplay";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { EmptyState } from "@/components/shared/EmptyState";
import { Modal } from "@/components/shared/Modal";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useEstrategiaReembolso,
  useReembolso,
  useTodosIngressos,
  type IngressoResumo,
} from "@/hooks/useCheckout";
import { useEventos } from "@/hooks/useEventos";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import type { StatusIngresso, UUID } from "@/types/dominio";

const statusVariant: Record<StatusIngresso, "success" | "secondary" | "destructive"> = {
  ATIVO: "success",
  UTILIZADO: "secondary",
  REEMBOLSADO: "destructive",
};

const statusLabel: Record<StatusIngresso, string> = {
  ATIVO: "Ativo",
  UTILIZADO: "Utilizado",
  REEMBOLSADO: "Reembolsado",
};

const tipoLabel: Record<string, string> = {
  INTEIRA: "Inteira",
  MEIA_ENTRADA: "Meia entrada",
  SOCIAL: "Social",
};

const formatarMoeda = (v: number | string | undefined) => {
  if (v == null) return "—";
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(Number(v));
};

export default function MeusIngressosPage() {
  const params = useSearchParams();
  const eventoIdInicial = params.get("eventoId") ?? "";

  const { data: todosIngressos, isLoading } = useTodosIngressos();
  const { data: todosEventos } = useEventos();

  const [eventoFiltro, setEventoFiltro] = useState<UUID>(eventoIdInicial);
  const [qrAberto, setQrAberto] = useState<IngressoResumo | null>(null);
  const [reembolsoAberto, setReembolsoAberto] = useState<IngressoResumo | null>(null);

  // IDs únicos de eventos com ingressos
  const eventosComIngresso = useMemo(() => {
    const ids = new Set((todosIngressos ?? []).map((i) => i.eventoId));
    return Array.from(ids);
  }, [todosIngressos]);

  // Mapa eventoId → título
  const tituloEvento = useMemo(() => {
    const m = new Map<string, string>();
    (todosEventos ?? []).forEach((e) => m.set(e.id, e.titulo));
    return m;
  }, [todosEventos]);

  // Ingressos filtrados pelo evento selecionado
  const ingressosFiltrados = useMemo(() => {
    if (!eventoFiltro) return todosIngressos ?? [];
    return (todosIngressos ?? []).filter((i) => i.eventoId === eventoFiltro);
  }, [todosIngressos, eventoFiltro]);

  const queryClient = useQueryClient();
  const estrategia = useEstrategiaReembolso(reembolsoAberto?.metodoPagamento);
  const reembolsar = useReembolso(eventoFiltro || undefined);

  async function confirmarReembolso() {
    if (!reembolsoAberto) return;
    const ingressoId = reembolsoAberto.id;
    try {
      // Update otimístico: muda status imediatamente sem esperar F5
      queryClient.setQueryData(
        ["ingressos", "todos"],
        (prev: IngressoResumo[] | undefined) =>
          prev?.map((i) =>
            i.id === ingressoId ? { ...i, status: "REEMBOLSADO" as const } : i,
          ) ?? prev,
      );
      setReembolsoAberto(null);

      await reembolsar.mutateAsync(ingressoId);
      toast.success("Reembolso solicitado!");
    } catch (error) {
      // Reverte em caso de erro
      queryClient.invalidateQueries({ queryKey: ["ingressos", "todos"] });
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PublicLayout>
      <section className="mx-auto max-w-5xl space-y-6 px-6 py-10">
        <header>
          <h1 className="font-display text-noite text-2xl font-semibold tracking-tight">
            Meus ingressos
          </h1>
          <p className="text-muted-foreground text-sm">
            Apresente o QR na entrada ou solicite reembolso escalonado conforme antecedência.
          </p>
        </header>

        {/* Dropdown só com eventos que o usuário comprou ingresso */}
        <Card className="p-5">
          <label htmlFor="eventoFiltro" className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide">
            Evento
          </label>
          {isLoading ? (
            <Skeleton className="h-10 w-full" />
          ) : eventosComIngresso.length === 0 ? (
            <p className="text-muted-foreground text-sm">
              Você ainda não tem ingressos adquiridos.
            </p>
          ) : (
            <Select
              id="eventoFiltro"
              value={eventoFiltro}
              onChange={(e) => setEventoFiltro(e.target.value)}
            >
              <option value="">Todos os eventos</option>
              {eventosComIngresso.map((id) => (
                <option key={id} value={id}>
                  {tituloEvento.get(id) ?? id.slice(0, 8) + "…"}
                </option>
              ))}
            </Select>
          )}
        </Card>

        {isLoading && (
          <div className="grid gap-3 sm:grid-cols-2">
            {Array.from({ length: 2 }).map((_, i) => (
              <Card key={i} className="space-y-2 p-5">
                <Skeleton className="h-4 w-1/2" />
                <Skeleton className="h-3 w-3/4" />
                <Skeleton className="h-3 w-2/3" />
              </Card>
            ))}
          </div>
        )}

        {!isLoading && ingressosFiltrados.length === 0 && eventosComIngresso.length > 0 && (
          <EmptyState
            icon={TicketCheck}
            title="Nenhum ingresso para este evento"
            description="Selecione outro evento ou compre um ingresso no catálogo."
          />
        )}

        {!isLoading && ingressosFiltrados.length === 0 && eventosComIngresso.length === 0 && (
          <EmptyState
            icon={TicketCheck}
            title="Você ainda não tem ingressos"
            description="Explore os eventos no catálogo e compre seu ingresso."
          />
        )}

        {ingressosFiltrados.length > 0 && (
          <div className="grid gap-3 sm:grid-cols-2">
            {ingressosFiltrados.map((ingresso) => (
              <Card
                key={ingresso.id}
                className="border-l-4 border-l-azul space-y-3 p-5"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-muted-foreground text-xs uppercase tracking-widest">
                      {tipoLabel[ingresso.tipo] ?? ingresso.tipo}
                      {ingresso.metodoPagamento ? ` · ${ingresso.metodoPagamento}` : ""}
                    </p>
                    <p className="font-display text-noite mt-0.5 text-lg font-semibold">
                      {formatarMoeda(ingresso.valorPago)}
                    </p>
                    <p className="text-muted-foreground text-xs truncate max-w-[180px]">
                      {tituloEvento.get(ingresso.eventoId) ?? ingresso.eventoId.slice(0, 8) + "…"}
                    </p>
                  </div>
                  <Badge variant={statusVariant[ingresso.status]}>
                    {statusLabel[ingresso.status]}
                  </Badge>
                </div>

                <div className="text-muted-foreground space-y-1 text-xs">
                  <p className="flex items-center gap-1">
                    <CalendarDays className="text-laranja h-3 w-3" />
                    {formatarDataHora(ingresso.dataHoraApresentacao)}
                  </p>
                  <p>Comprado em: {formatarDataHora(ingresso.dataCompra)}</p>
                </div>

                <div className="flex gap-2 pt-2">
                  {ingresso.status !== "REEMBOLSADO" && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setQrAberto(ingresso)}
                      className="flex-1"
                    >
                      <QrCode className="mr-1 h-3.5 w-3.5" />
                      Ver QR
                    </Button>
                  )}
                  {ingresso.status === "ATIVO" && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setReembolsoAberto(ingresso)}
                      className="border-destructive/40 text-destructive hover:bg-destructive/10 flex-1"
                    >
                      <RefreshCcw className="mr-1 h-3.5 w-3.5" />
                      Reembolsar
                    </Button>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}
      </section>

      {/* Modal QR */}
      <Modal
        open={!!qrAberto}
        onClose={() => setQrAberto(null)}
        title="Ingresso"
        description="Apresente este QR na catraca do evento."
      >
        {qrAberto && (
          <QRCodeDisplay
            codigo={qrAberto.codigoQr}
            eventoNome={`Apresentação ${formatarDataHora(qrAberto.dataHoraApresentacao)}`}
          />
        )}
      </Modal>

      {/* Confirm reembolso */}
      <ConfirmDialog
        open={!!reembolsoAberto}
        onClose={() => setReembolsoAberto(null)}
        onConfirm={confirmarReembolso}
        title="Solicitar reembolso"
        confirmLabel="Solicitar"
        cancelLabel="Voltar"
        dangerous
        loading={reembolsar.isPending}
        description={
          reembolsoAberto && (
            <div className="space-y-3 text-sm">
              <p>
                Você está prestes a solicitar reembolso do ingresso{" "}
                <span className="font-mono text-xs">{reembolsoAberto.id.slice(0, 8)}</span>{" "}
                no valor de <strong>{formatarMoeda(reembolsoAberto.valorPago)}</strong>.
              </p>
              <Card className="bg-nevoa-muted border-laranja/30 p-3 text-xs">
                <p className="text-muted-foreground uppercase tracking-widest">
                  Política para {reembolsoAberto.metodoPagamento}
                </p>
                {estrategia.isLoading ? (
                  <Skeleton className="mt-2 h-3 w-full" />
                ) : estrategia.data ? (
                  <>
                    <p className="text-noite mt-1 font-medium">Prazo: {estrategia.data.prazo}</p>
                    <p className="text-muted-foreground mt-0.5">{estrategia.data.descricao}</p>
                  </>
                ) : null}
              </Card>
            </div>
          )
        }
      />
    </PublicLayout>
  );
}
