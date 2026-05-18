"use client";

import { useSearchParams } from "next/navigation";
import { useState } from "react";
import { CalendarDays, QrCode, RefreshCcw, TicketCheck } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEvento } from "@/components/form/Seletores";
import { QRCodeDisplay } from "@/components/domain/QRCodeDisplay";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { EmptyState } from "@/components/shared/EmptyState";
import { Modal } from "@/components/shared/Modal";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useEstrategiaReembolso,
  useIngressosPorEvento,
  useReembolso,
  type IngressoResumo,
} from "@/hooks/useCheckout";
import type { ApiError } from "@/lib/api";
import type { StatusIngresso, UUID } from "@/types/dominio";

const statusVariant: Record<
  StatusIngresso,
  "success" | "secondary" | "destructive"
> = {
  ATIVO: "success",
  UTILIZADO: "secondary",
  REEMBOLSADO: "destructive",
};

const statusLabel: Record<StatusIngresso, string> = {
  ATIVO: "Ativo",
  UTILIZADO: "Utilizado",
  REEMBOLSADO: "Reembolsado",
};

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);

const formatarData = (iso: string) =>
  new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(iso));

export default function MeusIngressosPage() {
  const params = useSearchParams();
  const [eventoIdInput, setEventoIdInput] = useState(
    params.get("eventoId") ?? "",
  );
  const [eventoConsulta, setEventoConsulta] = useState<UUID | undefined>(
    params.get("eventoId") ?? undefined,
  );
  const [qrAberto, setQrAberto] = useState<IngressoResumo | null>(null);
  const [reembolsoAberto, setReembolsoAberto] = useState<IngressoResumo | null>(
    null,
  );

  const { data: ingressos, isLoading, refetch } = useIngressosPorEvento(eventoConsulta);
  const estrategia = useEstrategiaReembolso(reembolsoAberto?.metodoPagamento);
  const reembolsar = useReembolso(eventoConsulta);

  function consultar() {
    if (!eventoIdInput) return;
    setEventoConsulta(eventoIdInput);
  }

  async function confirmarReembolso() {
    if (!reembolsoAberto) return;
    try {
      await reembolsar.mutateAsync(reembolsoAberto.id);
      toast.success("Reembolso solicitado!");
      setReembolsoAberto(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PublicLayout>
      <section className="mx-auto max-w-5xl space-y-6 px-6 py-10">
        <header>
          <h1 className="font-display text-palco text-2xl font-semibold tracking-tight">
            Meus ingressos
          </h1>
          <p className="text-muted-foreground text-sm">
            Veja seus ingressos por evento. Apresente o QR na entrada ou
            solicite reembolso escalonado conforme antecedência.
          </p>
        </header>

        {/* Filtro por evento (BFF retorna ingressos por eventoId) */}
        <Card className="space-y-3 p-5">
          <FormField
            label="Evento"
            htmlFor="eventoIdConsulta"
            hint="Selecione o evento para ver os ingressos adquiridos."
          >
            <SeletorEvento
              id="eventoIdConsulta"
              value={eventoIdInput}
              onChange={(v) => {
                setEventoIdInput(v);
                if (v) setEventoConsulta(v);
              }}
            />
          </FormField>
        </Card>

        {isLoading && eventoConsulta && (
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

        {ingressos && ingressos.length === 0 && (
          <EmptyState
            icon={TicketCheck}
            title="Nenhum ingresso para este evento"
            description="Compre ingressos no catálogo para vê-los aqui."
          />
        )}

        {ingressos && ingressos.length > 0 && (
          <div className="grid gap-3 sm:grid-cols-2">
            {ingressos.map((ingresso) => (
              <Card
                key={ingresso.id}
                className="border-l-4 border-l-vinho space-y-3 p-5"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-muted-foreground text-xs uppercase tracking-widest">
                      {ingresso.tipo} · {ingresso.metodoPagamento}
                    </p>
                    <p className="font-display text-palco mt-0.5 text-lg font-semibold">
                      {formatarMoeda(ingresso.valorPago)}
                    </p>
                  </div>
                  <Badge variant={statusVariant[ingresso.status]}>
                    {statusLabel[ingresso.status]}
                  </Badge>
                </div>
                <div className="text-muted-foreground space-y-1 text-xs">
                  <p className="flex items-center gap-1">
                    <CalendarDays className="text-ouro h-3 w-3" />
                    {formatarData(ingresso.dataHoraApresentacao)}
                  </p>
                  <p className="font-mono">
                    Compra: {formatarData(ingresso.dataCompra)}
                  </p>
                </div>
                <div className="flex gap-2 pt-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setQrAberto(ingresso)}
                    className="flex-1"
                  >
                    <QrCode className="mr-1 h-3.5 w-3.5" />
                    Ver QR
                  </Button>
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
            eventoNome={`Apresentação ${formatarData(qrAberto.dataHoraApresentacao)}`}
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
                <span className="font-mono text-xs">
                  {reembolsoAberto.id.slice(0, 8)}
                </span>{" "}
                no valor de{" "}
                <strong>{formatarMoeda(reembolsoAberto.valorPago)}</strong>.
              </p>
              <Card className="bg-marquee-muted border-ouro/30 p-3 text-xs">
                <p className="text-muted-foreground uppercase tracking-widest">
                  Política para {reembolsoAberto.metodoPagamento}
                </p>
                {estrategia.isLoading ? (
                  <Skeleton className="mt-2 h-3 w-full" />
                ) : estrategia.data ? (
                  <>
                    <p className="text-palco mt-1 font-medium">
                      Prazo: {estrategia.data.prazo}
                    </p>
                    <p className="text-muted-foreground mt-0.5">
                      {estrategia.data.descricao}
                    </p>
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
