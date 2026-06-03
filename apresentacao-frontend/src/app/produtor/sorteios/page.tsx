"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  CalendarClock,
  Gift,
  ListOrdered,
  Plus,
  Shuffle,
  XCircle,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorApresentacao, SeletorEvento } from "@/components/form/Seletores";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useApurarSorteio,
  useCancelarSorteio,
  useCriarSorteio,
  useInscricoesSorteio,
  useSorteiosPorEvento,
} from "@/hooks/useSorteios";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import { statusSorteioLabel, statusSorteioVariant } from "@/lib/statusMaps";
import type { SorteioResumo } from "@/services/bff/sorteios";
import type { StatusInscricao, StatusSorteio } from "@/types/dominio";

const novoSchema = z
  .object({
    eventoId: z.string().uuid("eventoId inválido"),
    apresentacaoId: z.string().uuid("apresentacaoId inválido"),
    vagas: z.coerce.number().int().positive("Vagas devem ser positivas"),
    prazoInscricao: z.string().min(1, "Informe o prazo de inscrição"),
    dataApresentacao: z.string().min(1, "Informe a data da apresentação"),
  })
  .refine(
    (v) =>
      new Date(v.prazoInscricao).getTime() <
      new Date(v.dataApresentacao).getTime(),
    {
      message: "O prazo deve ser anterior à data da apresentação",
      path: ["prazoInscricao"],
    },
  );
type NovoFormInput = z.input<typeof novoSchema>;
type NovoFormOutput = z.output<typeof novoSchema>;

const statusInscricaoVariant: Record<
  StatusInscricao,
  "success" | "frevo" | "secondary" | "destructive"
> = {
  GANHADOR: "success",
  SUPLENTE: "frevo",
  INSCRITO: "secondary",
  DESISTENTE: "destructive",
  CANCELADA: "destructive",
};

function progressoPrazo(prazoIso: string) {
  const agora = Date.now();
  const prazo = new Date(prazoIso).getTime();
  const diff = prazo - agora;
  if (diff <= 0) return 0;
  const sete_dias = 7 * 24 * 60 * 60 * 1000;
  return Math.max(0, Math.min(100, (diff / sete_dias) * 100));
}

export default function SorteiosProdutorPage() {
  const [eventoId, setEventoId] = useState("");
  const [eventoConsultado, setEventoConsultado] = useState<string | undefined>(
    undefined,
  );
  const [novoAberto, setNovoAberto] = useState(false);
  const [apurar, setApurar] = useState<SorteioResumo | null>(null);
  const [cancelar, setCancelar] = useState<SorteioResumo | null>(null);
  const [verInscricoes, setVerInscricoes] = useState<SorteioResumo | null>(null);

  const { data, isLoading, isError } = useSorteiosPorEvento(eventoConsultado);
  const criar = useCriarSorteio(eventoConsultado);
  const apurarMutation = useApurarSorteio(eventoConsultado);
  const cancelarMutation = useCancelarSorteio(eventoConsultado);

  const form = useForm<NovoFormInput, unknown, NovoFormOutput>({
    resolver: zodResolver(novoSchema),
    defaultValues: {
      eventoId: "",
      apresentacaoId: "",
      vagas: 50,
      prazoInscricao: "",
      dataApresentacao: "",
    },
  });

  function consultar() {
    if (eventoId) setEventoConsultado(eventoId);
  }

  async function onCriar(values: NovoFormOutput) {
    try {
      await criar.mutateAsync({
        ...values,
        prazoInscricao: new Date(values.prazoInscricao).toISOString(),
        dataApresentacao: new Date(values.dataApresentacao).toISOString(),
      });
      toast.success("Sorteio criado");
      form.reset();
      setNovoAberto(false);
      if (!eventoConsultado) setEventoConsultado(values.eventoId);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onApurar() {
    if (!apurar) return;
    try {
      await apurarMutation.mutateAsync(apurar.id);
      toast.success(`Sorteio apurado! ${apurar.vagas} ganhadores definidos.`);
      setApurar(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onCancelar() {
    if (!cancelar) return;
    try {
      await cancelarMutation.mutateAsync(cancelar.id);
      toast.success("Sorteio cancelado");
      setCancelar(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Sorteios"
      subtitulo="Apuração aleatória de ingressos para apresentações com lotação limitada."
      acoes={
        <Button
          onClick={() => {
            if (eventoConsultado) form.setValue("eventoId", eventoConsultado);
            setNovoAberto(true);
          }}
          className="bg-vinho hover:bg-vinho-light text-marquee"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo sorteio
        </Button>
      }
    >
      <Card className="space-y-3 p-5">
        <FormField
          label="Evento"
          htmlFor="eventoIdConsulta"
          hint="Selecione o evento para listar os sorteios."
        >
          <SeletorEvento
            id="eventoIdConsulta"
            value={eventoId}
            onChange={(v) => {
              setEventoId(v);
              if (v) setEventoConsultado(v);
            }}
          />
        </FormField>
      </Card>

      {isLoading && eventoConsultado && (
        <div className="space-y-2">
          {Array.from({ length: 2 }).map((_, i) => (
            <Skeleton key={i} className="h-24 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Gift}
          title="Falha ao carregar sorteios"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && data.length === 0 && eventoConsultado && (
        <EmptyState
          icon={Gift}
          title="Nenhum sorteio para este evento"
          description="Crie um sorteio para distribuir ingressos por apuração aleatória."
        />
      )}

      {data && data.length > 0 && (
        <div className="grid gap-3 sm:grid-cols-2">
          {data.map((s) => {
            const progresso = progressoPrazo(s.prazoInscricao);
            return (
              <Card
                key={s.id}
                className={
                  s.status === "INSCRICOES_ABERTAS"
                    ? "border-t-frevo space-y-3 border-t-4 p-5"
                    : "border-t-vinho space-y-3 border-t-4 p-5"
                }
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-muted-foreground text-xs uppercase tracking-widest">
                      Apresentação{" "}
                      {s.dataApresentacao
                        ? formatarDataHora(s.dataApresentacao)
                        : s.apresentacaoId
                          ? `${s.apresentacaoId.slice(0, 8)}…`
                          : "—"}
                    </p>
                    <p className="font-display text-palco mt-1 text-base font-semibold">
                      {s.vagas} {s.vagas === 1 ? "vaga" : "vagas"}
                    </p>
                  </div>
                  <Badge variant={statusSorteioVariant[s.status]}>
                    {statusSorteioLabel[s.status]}
                  </Badge>
                </div>
                <div className="text-muted-foreground space-y-1 text-xs">
                  <p className="flex items-center gap-1.5">
                    <CalendarClock className="text-ouro h-3 w-3" />
                    Prazo: {formatarDataHora(s.prazoInscricao)}
                  </p>
                  <p className="flex items-center gap-1.5">
                    <CalendarClock className="text-ouro h-3 w-3" />
                    Apresentação: {formatarDataHora(s.dataApresentacao)}
                  </p>
                </div>
                {s.status === "INSCRICOES_ABERTAS" && (
                  <div className="space-y-1">
                    <div className="bg-marquee-muted h-1.5 overflow-hidden rounded-full">
                      <div
                        className="bg-ouro h-full transition-all"
                        style={{ width: `${progresso}%` }}
                      />
                    </div>
                    <p className="text-muted-foreground text-[10px]">
                      {progresso > 0 ? "Inscrições abertas" : "Prazo encerrado"}
                    </p>
                  </div>
                )}
                <div className="flex flex-wrap gap-2 pt-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setVerInscricoes(s)}
                  >
                    <ListOrdered className="mr-1 h-3 w-3" />
                    Inscrições
                  </Button>
                  {s.status === "INSCRICOES_ABERTAS" && (
                    <>
                      <Button
                        size="sm"
                        onClick={() => setApurar(s)}
                        className="bg-frevo text-palco hover:bg-frevo-dark font-semibold"
                      >
                        <Shuffle className="mr-1 h-3 w-3" />
                        Apurar
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => setCancelar(s)}
                        className="border-destructive/40 text-destructive hover:bg-destructive/10"
                      >
                        <XCircle className="mr-1 h-3 w-3" />
                        Cancelar
                      </Button>
                    </>
                  )}
                </div>
              </Card>
            );
          })}
        </div>
      )}

      <Modal
        open={novoAberto}
        onClose={() => {
          form.reset();
          setNovoAberto(false);
        }}
        title="Novo sorteio"
        description="Crie um sorteio para uma apresentação específica do evento."
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
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {criar.isPending && <LoadingSpinner className="mr-2 text-marquee" />}
              Criar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Evento"
            htmlFor="eventoIdNovo"
            error={form.formState.errors.eventoId?.message}
            required
          >
            <SeletorEvento
              id="eventoIdNovo"
              value={form.watch("eventoId") ?? ""}
              onChange={(v) => form.setValue("eventoId", v)}
            />
          </FormField>
          <FormField
            label="Apresentação"
            htmlFor="apresentacaoId"
            error={form.formState.errors.apresentacaoId?.message}
            hint={
              form.watch("eventoId")
                ? "Mostra as datas programadas no evento."
                : "Selecione um evento para listar as apresentações."
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
            label="Número de vagas"
            htmlFor="vagas"
            error={form.formState.errors.vagas?.message}
            required
          >
            <Input
              id="vagas"
              type="number"
              min={1}
              {...form.register("vagas")}
            />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Prazo de inscrição"
              htmlFor="prazoInscricao"
              error={form.formState.errors.prazoInscricao?.message}
              required
            >
              <Input
                id="prazoInscricao"
                type="datetime-local"
                {...form.register("prazoInscricao")}
              />
            </FormField>
            <FormField
              label="Data da apresentação"
              htmlFor="dataApresentacao"
              error={form.formState.errors.dataApresentacao?.message}
              required
            >
              <Input
                id="dataApresentacao"
                type="datetime-local"
                {...form.register("dataApresentacao")}
              />
            </FormField>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!apurar}
        onClose={() => setApurar(null)}
        onConfirm={onApurar}
        title="Apurar sorteio"
        confirmLabel="Realizar apuração"
        loading={apurarMutation.isPending}
        description={
          apurar && (
            <p className="text-sm">
              Apuração aleatória de <strong>{apurar.vagas} ganhadores</strong> e
              os demais como suplentes. Esta ação é definitiva.
            </p>
          )
        }
      />

      <ConfirmDialog
        open={!!cancelar}
        onClose={() => setCancelar(null)}
        onConfirm={onCancelar}
        title="Cancelar sorteio"
        confirmLabel="Cancelar sorteio"
        cancelLabel="Voltar"
        dangerous
        loading={cancelarMutation.isPending}
        description={
          <p className="text-sm">
            Todos os inscritos serão marcados como CANCELADA. Não dá pra desfazer.
          </p>
        }
      />

      <InscricoesModal
        sorteio={verInscricoes}
        onClose={() => setVerInscricoes(null)}
      />
    </PageLayout>
  );
}

function InscricoesModal({
  sorteio,
  onClose,
}: {
  sorteio: SorteioResumo | null;
  onClose: () => void;
}) {
  const { data, isLoading } = useInscricoesSorteio(sorteio?.id);

  return (
    <Modal
      open={!!sorteio}
      onClose={onClose}
      title="Inscrições por prioridade"
      description="Inscrições ordenadas por prioridade (Ganhador → Suplente → Inscrito)."
      className="max-w-xl"
    >
      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-10 w-full" />
          ))}
        </div>
      )}
      {data && data.length === 0 && (
        <p className="text-muted-foreground text-sm">
          Nenhum espectador se inscreveu ainda.
        </p>
      )}
      {data && data.length > 0 && (
        <ol className="space-y-2 text-sm">
          {data.map((i, index) => (
            <li
              key={i.espectadorId}
              className="border-border flex items-center justify-between gap-3 rounded-lg border px-3 py-2"
            >
              <div className="flex items-center gap-2 min-w-0">
                <span className="text-muted-foreground font-mono text-xs">
                  {String(index + 1).padStart(2, "0")}
                </span>
                <span className="text-palco truncate font-mono text-xs">
                  {i.espectadorId}
                </span>
              </div>
              <Badge variant={statusInscricaoVariant[i.status]}>
                {i.status}
              </Badge>
            </li>
          ))}
        </ol>
      )}
    </Modal>
  );
}
