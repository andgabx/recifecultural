"use client";

import { useEffect, useRef, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { ArrowLeft, Check, Clock, ShoppingCart, Tag, Ticket, Trash2, X } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useCancelarPreReserva,
  useComprarMultiplos,
  usePreviewCupom,
  useReservarAssento,
} from "@/hooks/useCheckout";
import { useEvento } from "@/hooks/useEventos";
import { useSetoresPorEspaco } from "@/hooks/useSetores";
import type { ApiError } from "@/lib/api";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { useRole } from "@/lib/role";
import { cn } from "@/lib/utils";
import type { AssentoResumo, SetorComAssentos } from "@/services/bff/setores";
import type { MetodoPagamento, StatusAssento, TipoIngresso, UUID } from "@/types/dominio";

// ─── Tipos locais ─────────────────────────────────────────────────────────────

type AssentoNoCarrinho = {
  assentoId: UUID;
  setorId: UUID;
  codigo: string;
  setorNome: string;
  tipo: TipoIngresso;
  preReservaId: UUID;
};

// ─── Constantes ───────────────────────────────────────────────────────────────

const TEMPO_SEG = 10 * 60;

// Tipos disponíveis no mapa — Social é incluído dinamicamente se o evento tiver precoSocial
const TIPOS_BASE: Array<{ value: TipoIngresso; label: string }> = [
  { value: "INTEIRA",       label: "Inteira" },
  { value: "MEIA_ENTRADA",  label: "Meia entrada" },
  { value: "SOCIAL",        label: "Social (subsidiado)" },
];

const METODOS: Array<{ value: MetodoPagamento; label: string }> = [
  { value: "PIX",           label: "PIX" },
  { value: "CARTAO_CREDITO", label: "Cartão de crédito" },
  { value: "CARTAO_DEBITO",  label: "Cartão de débito" },
];

const corAssento = (
  status: StatusAssento,
  noCarrinho: boolean,
): string => {
  if (noCarrinho)
    return "bg-vinho ring-2 ring-vinho ring-offset-1 scale-110 cursor-pointer text-white";
  switch (status) {
    case "LIVRE":         return "bg-emerald-500 hover:bg-emerald-400 cursor-pointer text-white";
    case "PRE_RESERVADO": return "bg-amber-400 cursor-not-allowed opacity-60 text-white";
    case "OCUPADO":       return "bg-rose-500 cursor-not-allowed opacity-60 text-white";
    case "BLOQUEADO":     return "bg-zinc-400 cursor-not-allowed opacity-50 text-white";
  }
};

const precoParaTipo = (
  tipo: TipoIngresso,
  precoInteira: number,
  precoMeia: number,
  precoSocial: number | null,
) => {
  if (tipo === "MEIA_ENTRADA") return precoMeia;
  if (tipo === "SOCIAL") return precoSocial ?? 0;
  return precoInteira;
};

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);

const formatarTempo = (seg: number) => {
  const m = Math.floor(seg / 60).toString().padStart(2, "0");
  const s = (seg % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
};

// ─── Componente principal ─────────────────────────────────────────────────────

export default function SelecionarAssentoPage() {
  const router = useRouter();
  const params = useSearchParams();
  const { papel } = useRole();
  const usuario = IDENTIDADES_MOCK[papel];

  const eventoId    = params.get("eventoId") ?? "";
  const dataHoraIso = params.get("dataHoraApresentacao") ?? "";

  const { data: evento } = useEvento(eventoId || undefined);
  const { data: setores, isLoading } = useSetoresPorEspaco(
    evento?.localId || undefined,
  );

  const precoInteira = evento?.precoInteira ? Number(evento.precoInteira) : 100;
  const precoMeia    = evento?.precoMeia    ? Number(evento.precoMeia)    : 50;
  const precoSocial  = evento?.precoSocial  ? Number(evento.precoSocial)  : null;
  const capacidade   = 500;

  // Social só aparece se o evento tiver preço social definido (via subsídio)
  const tiposDisponiveis = TIPOS_BASE.filter(
    (t) => t.value !== "SOCIAL" || precoSocial !== null,
  );

  const [carrinho, setCarrinho] = useState<AssentoNoCarrinho[]>([]);
  const [timer, setTimer] = useState(TEMPO_SEG);
  const [metodo, setMetodo] = useState<MetodoPagamento>("PIX");
  const [compraConfirmada, setCompraConfirmada] = useState<{
    ids: string[];
    metodo: MetodoPagamento;
    total: number;
  } | null>(null);

  // Estado do cupom
  const [codigoCupom, setCodigoCupom] = useState("");
  const [cpfComprador, setCpfComprador] = useState("");
  const [cupomAplicado, setCupomAplicado] = useState<{
    codigo: string;
    cpf: string;
    desconto: number;
    tipo: string;
    configuracao: number;
  } | null>(null);

  const reservar        = useReservarAssento();
  const cancelarReserva = useCancelarPreReserva();
  const comprar         = useComprarMultiplos();
  const previewCupom    = usePreviewCupom();

  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Inicia/reinicia timer quando o carrinho muda de vazio para preenchido
  useEffect(() => {
    if (carrinho.length === 0) {
      clearInterval(timerRef.current!);
      setTimer(TEMPO_SEG);
      return;
    }
    if (timerRef.current) return; // já rodando
    timerRef.current = setInterval(() => {
      setTimer((t) => {
        if (t <= 1) {
          clearInterval(timerRef.current!);
          timerRef.current = null;
          toast.error("Tempo esgotado. Os assentos foram liberados.");
          // cancela todas as pré-reservas
          setCarrinho((prev) => {
            prev.forEach((a) => cancelarReserva.mutate(a.preReservaId));
            return [];
          });
          setTimer(TEMPO_SEG);
          return TEMPO_SEG;
        }
        return t - 1;
      });
    }, 1000);
    return () => {
      clearInterval(timerRef.current!);
      timerRef.current = null;
    };
  }, [carrinho.length > 0]); // eslint-disable-line

  // IDs já no carrinho para colorir
  const idsNoCarrinho = new Set(carrinho.map((a) => a.assentoId));

  async function handleClicarAssento(
    assento: AssentoResumo,
    setor: SetorComAssentos,
  ) {
    // Se já está no carrinho → remover
    if (idsNoCarrinho.has(assento.id)) {
      const item = carrinho.find((a) => a.assentoId === assento.id)!;
      await cancelarReserva.mutateAsync(item.preReservaId).catch(() => {});
      setCarrinho((prev) => prev.filter((a) => a.assentoId !== assento.id));
      return;
    }

    if (assento.status !== "LIVRE") return;

    try {
      const resp = await reservar.mutateAsync({
        setorId:   setor.id,
        assentoId: assento.id,
        usuarioId: usuario.id,
      });
      setCarrinho((prev) => [
        ...prev,
        {
          assentoId:    assento.id,
          setorId:      setor.id,
          codigo:       assento.codigo,
          setorNome:    setor.nome,
          tipo:         "INTEIRA",
          preReservaId: resp.id,
        },
      ]);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  function mudarTipo(assentoId: UUID, tipo: TipoIngresso) {
    setCarrinho((prev) =>
      prev.map((a) => (a.assentoId === assentoId ? { ...a, tipo } : a)),
    );
  }

  async function removerDoCarrinho(item: AssentoNoCarrinho) {
    await cancelarReserva.mutateAsync(item.preReservaId).catch(() => {});
    setCarrinho((prev) => prev.filter((a) => a.assentoId !== item.assentoId));
  }

  async function validarCupom() {
    if (!codigoCupom.trim() || !cpfComprador.trim()) {
      toast.error("Informe o código do cupom e o CPF.");
      return;
    }
    if (totalSemDesconto === 0) {
      toast.error("Adicione assentos antes de aplicar o cupom.");
      return;
    }
    try {
      const resp = await previewCupom.mutateAsync({
        codigo: codigoCupom.trim().toUpperCase(),
        cpf: cpfComprador.trim(),
        valor: totalSemDesconto,
        categoria: evento?.categoria ?? "",
      });
      setCupomAplicado({
        codigo: codigoCupom.trim().toUpperCase(),
        cpf: cpfComprador.trim(),
        desconto: resp.descontoCalculado,
        tipo: resp.tipoDesconto,
        configuracao: resp.configuracaoDesconto,
      });
      toast.success(
        resp.tipoDesconto === "PERCENTUAL"
          ? `Cupom aplicado: ${resp.configuracaoDesconto}% de desconto (${formatarMoeda(resp.descontoCalculado)})`
          : `Cupom aplicado: ${formatarMoeda(resp.descontoCalculado)} de desconto`,
      );
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function finalizarCompra() {
    if (carrinho.length === 0) return;
    if (!eventoId || !dataHoraIso) {
      toast.error("Dados do evento ausentes.");
      return;
    }
    try {
      const resp = await comprar.mutateAsync({
        eventoId,
        dataHoraApresentacao: new Date(dataHoraIso).toISOString(),
        metodoPagamento: metodo,
        capacidadeMaxima: capacidade,
        itens: carrinho.map((a) => ({
          preReservaId: a.preReservaId,
          assentoId:    a.assentoId,
          tipo:         a.tipo,
          valor:        precoParaTipo(a.tipo, precoInteira, precoMeia, precoSocial),
        })),
        ...(cupomAplicado
          ? {
              codigoCupom:    cupomAplicado.codigo,
              cpfComprador:   cupomAplicado.cpf,
              categoriaEvento: evento?.categoria ?? "",
            }
          : {}),
      });
      clearInterval(timerRef.current!);
      timerRef.current = null;
      setCompraConfirmada({ ids: resp.ids, metodo, total });
      setCarrinho([]);
      setTimer(TEMPO_SEG);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const totalSemDesconto = carrinho.reduce(
    (acc, a) => acc + precoParaTipo(a.tipo, precoInteira, precoMeia, precoSocial),
    0,
  );
  const desconto = cupomAplicado?.desconto ?? 0;
  const total = Math.max(0, totalSemDesconto - desconto);

  const temSetores = setores && setores.length > 0;

  // ── Tela de confirmação pós-compra ─────────────────────────────────────────
  if (compraConfirmada) {
    const isPix = compraConfirmada.metodo === "PIX";
    return (
      <PublicLayout>
        <div className="mx-auto max-w-lg space-y-6 px-4 py-16 text-center">
          <div className="bg-emerald-100 mx-auto flex h-16 w-16 items-center justify-center rounded-full">
            <Ticket className="h-8 w-8 text-emerald-600" />
          </div>
          <div>
            <h1 className="font-display text-palco text-2xl font-bold">
              Pagamento aprovado!
            </h1>
            <p className="text-muted-foreground mt-1 text-sm">
              {compraConfirmada.ids.length} ingresso{compraConfirmada.ids.length > 1 ? "s" : ""} confirmado{compraConfirmada.ids.length > 1 ? "s" : ""} —{" "}
              {formatarMoeda(compraConfirmada.total)}
            </p>
          </div>

          {isPix ? (
            <Card className="space-y-4 p-6 text-left">
              <p className="text-palco font-semibold">Pagamento via PIX</p>
              {/* QR fictício — em produção viria do gateway */}
              <div className="bg-marquee-muted flex aspect-square w-full max-w-[180px] mx-auto items-center justify-center rounded-xl border border-dashed text-center text-xs text-muted-foreground">
                QR Code PIX<br />(simulado)
              </div>
              <div className="space-y-1">
                <p className="text-muted-foreground text-xs uppercase tracking-wide">Copia e cola</p>
                <div className="bg-marquee-muted rounded-md p-2 font-mono text-xs break-all">
                  00020126580014BR.GOV.BCB.PIX01364D98F3E2-DEMO-RECIFE-CULTURAL-{compraConfirmada.ids[0]?.slice(0, 8).toUpperCase()}
                </div>
                <p className="text-muted-foreground text-[10px]">
                  ⚠️ Ambiente de demonstração — pagamento já aprovado automaticamente pelo gateway simulado.
                </p>
              </div>
            </Card>
          ) : (
            <Card className="space-y-2 p-6 text-left">
              <p className="text-palco font-semibold">
                {compraConfirmada.metodo === "CARTAO_CREDITO" ? "Cartão de crédito" : "Cartão de débito"}
              </p>
              <p className="text-muted-foreground text-sm">
                Pagamento processado e aprovado pelo gateway.
              </p>
              <p className="text-muted-foreground text-[10px]">
                ⚠️ Ambiente de demonstração — em produção seria integrado com Stripe ou Mercado Pago.
              </p>
            </Card>
          )}

          <Card className="p-4 text-left space-y-1">
            <p className="text-muted-foreground text-xs uppercase tracking-wide">Seus ingressos</p>
            {compraConfirmada.ids.map((id) => (
              <p key={id} className="font-mono text-xs text-palco">#{id.slice(0, 8).toUpperCase()}</p>
            ))}
          </Card>

          <Button
            onClick={() => router.push(`/meus-ingressos?eventoId=${eventoId}`)}
            className="bg-vinho hover:bg-vinho-light text-marquee w-full"
          >
            Ver meus ingressos
          </Button>
        </div>
      </PublicLayout>
    );
  }

  return (
    <PublicLayout>
      <div className="mx-auto max-w-7xl px-4 py-8">
        {/* Header */}
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <Link
              href={eventoId ? `/eventos/${eventoId}` : "/"}
              className="text-muted-foreground hover:text-vinho mb-1 inline-flex items-center gap-1 text-sm transition-colors"
            >
              <ArrowLeft className="h-3.5 w-3.5" />
              Voltar ao evento
            </Link>
            <h1 className="font-display text-palco text-2xl font-semibold">
              Escolha seus assentos
            </h1>
            {evento && (
              <p className="text-muted-foreground text-sm">{evento.titulo}</p>
            )}
          </div>

          {/* Timer (só aparece quando há itens no carrinho) */}
          {carrinho.length > 0 && (
            <div className="flex items-center gap-2 rounded-full border border-amber-300 bg-amber-50 px-4 py-2">
              <Clock className="h-4 w-4 text-amber-600" />
              <span
                className={cn(
                  "font-mono text-sm font-bold",
                  timer < 60 ? "text-destructive" : "text-amber-700",
                )}
              >
                {formatarTempo(timer)}
              </span>
              <span className="text-muted-foreground text-xs">restantes</span>
            </div>
          )}
        </div>

        <div className="flex gap-6 items-start">
          {/* ── Mapa de assentos ── */}
          <div className="flex-1 min-w-0 space-y-4">
            {/* Legenda */}
            <div className="flex flex-wrap items-center gap-4 text-xs">
              {[
                { cor: "bg-emerald-500",   label: "Disponível" },
                { cor: "bg-vinho",         label: "Selecionado" },
                { cor: "bg-amber-400",     label: "Pré-reservado" },
                { cor: "bg-rose-500",      label: "Ocupado" },
                { cor: "bg-zinc-400",      label: "Bloqueado" },
              ].map(({ cor, label }) => (
                <span key={label} className="flex items-center gap-1.5">
                  <span className={cn("h-3.5 w-3.5 rounded-sm", cor)} />
                  {label}
                </span>
              ))}
            </div>

            {isLoading && (
              <div className="space-y-4">
                {[1, 2].map((i) => (
                  <Skeleton key={i} className="h-40 w-full" />
                ))}
              </div>
            )}

            {!isLoading && !temSetores && (
              <EmptyState
                icon={Ticket}
                title="Nenhum setor configurado"
                description="O espaço deste evento ainda não tem planta de assentos."
                action={
                  <Button
                    variant="outline"
                    onClick={() =>
                      router.push(
                        `/checkout?eventoId=${eventoId}&dataHoraApresentacao=${dataHoraIso}`,
                      )
                    }
                  >
                    Comprar sem escolher assento
                  </Button>
                }
              />
            )}

            {temSetores &&
              setores.map((setor) => {
                const fileiras = Array.from(
                  new Set(setor.assentos.map((a) => a.fileira)),
                ).sort();

                return (
                  <Card key={setor.id} className="space-y-4 overflow-x-auto p-5">
                    <div className="flex items-center justify-between gap-2">
                      <div>
                        <p className="font-display text-palco font-semibold">
                          {setor.nome}
                        </p>
                        <p className="text-muted-foreground text-xs">
                          {setor.tipoSetor} ·{" "}
                          {setor.assentos.filter((a) => a.status === "LIVRE").length}{" "}
                          livres
                        </p>
                      </div>
                      <Badge variant="outline">{setor.tipoSetor}</Badge>
                    </div>

                    <div className="bg-muted-foreground/10 rounded-md py-1 text-center text-[10px] uppercase tracking-widest text-muted-foreground">
                      ▲ Palco / Frente ▲
                    </div>

                    <div className="min-w-max space-y-1">
                      {fileiras.map((fileira) => {
                        const assentosFileira = setor.assentos
                          .filter((a) => a.fileira === fileira)
                          .sort((a, b) => a.numero - b.numero);

                        return (
                          <div key={fileira} className="flex items-center gap-1">
                            <span className="text-muted-foreground w-5 shrink-0 text-center font-mono text-[10px]">
                              {fileira}
                            </span>
                            {assentosFileira.map((assento) => {
                              const noCarrinho = idsNoCarrinho.has(assento.id);
                              const carregando =
                                reservar.isPending && !noCarrinho;

                              return (
                                <button
                                  key={assento.id}
                                  type="button"
                                  title={`${assento.codigo} · ${assento.status}`}
                                  disabled={
                                    (assento.status !== "LIVRE" && !noCarrinho) ||
                                    carregando
                                  }
                                  onClick={() =>
                                    handleClicarAssento(assento, setor)
                                  }
                                  className={cn(
                                    "flex h-6 w-6 items-center justify-center rounded-sm text-[9px] font-bold transition-all",
                                    corAssento(assento.status, noCarrinho),
                                  )}
                                >
                                  {assento.numero}
                                </button>
                              );
                            })}
                          </div>
                        );
                      })}
                    </div>
                  </Card>
                );
              })}

            {reservar.isPending && (
              <p className="text-muted-foreground flex items-center gap-2 text-sm">
                <LoadingSpinner />
                Reservando assento…
              </p>
            )}
          </div>

          {/* ── Carrinho lateral ── */}
          <div className="w-72 shrink-0 space-y-4 sticky top-6">
            <Card className="space-y-4 p-5">
              <div className="flex items-center gap-2">
                <ShoppingCart className="text-vinho h-4 w-4" />
                <h2 className="font-display text-palco font-semibold">
                  Meus ingressos
                </h2>
                {carrinho.length > 0 && (
                  <Badge variant="frevo" className="ml-auto">
                    {carrinho.length}
                  </Badge>
                )}
              </div>

              {carrinho.length === 0 ? (
                <p className="text-muted-foreground text-center text-sm py-4">
                  Clique em um assento verde para selecioná-lo.
                </p>
              ) : (
                <ul className="space-y-3">
                  {carrinho.map((item) => (
                    <li
                      key={item.assentoId}
                      className="border-border space-y-2 rounded-lg border p-3"
                    >
                      <div className="flex items-center justify-between gap-1">
                        <div>
                          <p className="text-palco text-sm font-semibold">
                            {item.codigo}
                          </p>
                          <p className="text-muted-foreground text-xs">
                            {item.setorNome}
                          </p>
                        </div>
                        <button
                          type="button"
                          onClick={() => removerDoCarrinho(item)}
                          className="text-destructive hover:bg-destructive/10 rounded p-1"
                          aria-label="Remover"
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                        </button>
                      </div>

                      {/* Seleção de tipo */}
                      <Select
                        value={item.tipo}
                        onChange={(e) =>
                          mudarTipo(item.assentoId, e.target.value as TipoIngresso)
                        }
                      >
                        {tiposDisponiveis.map((t) => {
                          if (t.value === "MEIA_ENTRADA" && !precoMeia)
                            return null;
                          const preco =
                            t.value === "INTEIRA"      ? precoInteira :
                            t.value === "MEIA_ENTRADA" ? precoMeia :
                            t.value === "SOCIAL"       ? precoSocial  : 0;
                          return (
                            <option key={t.value} value={t.value}>
                              {t.label} — {preco != null ? formatarMoeda(preco) : "Gratuito"}
                            </option>
                          );
                        })}
                      </Select>
                    </li>
                  ))}
                </ul>
              )}

              {/* Cupom de desconto */}
              {carrinho.length > 0 && (
                <div className="border-border border-t pt-3 space-y-2">
                  <label className="text-muted-foreground text-xs uppercase tracking-wide flex items-center gap-1">
                    <Tag className="h-3 w-3" /> Cupom de desconto
                  </label>
                  {cupomAplicado ? (
                    <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-2 flex items-center justify-between gap-2">
                      <div>
                        <p className="text-emerald-800 text-xs font-semibold font-mono">
                          {cupomAplicado.codigo}
                        </p>
                        <p className="text-emerald-700 text-xs">
                          {cupomAplicado.tipo === "PERCENTUAL"
                            ? `${cupomAplicado.configuracao}% de desconto`
                            : `${formatarMoeda(cupomAplicado.configuracao)} de desconto`}
                          {" — "}{formatarMoeda(cupomAplicado.desconto)} aplicados
                        </p>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          setCupomAplicado(null);
                          setCodigoCupom("");
                        }}
                        className="text-emerald-600 hover:text-destructive p-1"
                        aria-label="Remover cupom"
                      >
                        <X className="h-3.5 w-3.5" />
                      </button>
                    </div>
                  ) : (
                    <div className="space-y-1.5">
                      <input
                        type="text"
                        placeholder="FREVO20"
                        value={codigoCupom}
                        onChange={(e) => setCodigoCupom(e.target.value.toUpperCase())}
                        className="border-border bg-marquee-card text-foreground placeholder:text-muted-foreground h-9 w-full rounded-lg border px-3 text-sm font-mono uppercase focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-vinho/30"
                      />
                      <input
                        type="text"
                        placeholder="CPF (11 dígitos)"
                        value={cpfComprador}
                        onChange={(e) => setCpfComprador(e.target.value.replace(/\D/g, "").slice(0, 11))}
                        className="border-border bg-marquee-card text-foreground placeholder:text-muted-foreground h-9 w-full rounded-lg border px-3 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-vinho/30"
                      />
                      <Button
                        type="button"
                        size="sm"
                        variant="outline"
                        onClick={validarCupom}
                        disabled={previewCupom.isPending || !codigoCupom || !cpfComprador}
                        className="border-ouro text-ouro-dark hover:bg-ouro hover:text-marquee w-full"
                      >
                        {previewCupom.isPending ? (
                          <LoadingSpinner className="mr-1 text-ouro" />
                        ) : (
                          <Check className="mr-1 h-3.5 w-3.5" />
                        )}
                        Validar cupom
                      </Button>
                    </div>
                  )}
                </div>
              )}

              {/* Método de pagamento */}
              {carrinho.length > 0 && (
                <>
                  <div className="border-border border-t pt-3 space-y-2">
                    <label className="text-muted-foreground text-xs uppercase tracking-wide">
                      Pagamento
                    </label>
                    <Select
                      value={metodo}
                      onChange={(e) => setMetodo(e.target.value as MetodoPagamento)}
                    >
                      {METODOS.map((m) => (
                        <option key={m.value} value={m.value}>
                          {m.label}
                        </option>
                      ))}
                    </Select>
                  </div>

                  <div className="border-border border-t pt-3 space-y-1">
                    <div className="flex items-baseline justify-between">
                      <span className="text-muted-foreground text-xs uppercase tracking-wide">
                        Subtotal
                      </span>
                      <span className="text-palco text-sm">
                        {formatarMoeda(totalSemDesconto)}
                      </span>
                    </div>
                    {cupomAplicado && (
                      <div className="flex items-baseline justify-between">
                        <span className="text-emerald-600 text-xs">Desconto cupom</span>
                        <span className="text-emerald-600 text-sm font-medium">
                          − {formatarMoeda(cupomAplicado.desconto)}
                        </span>
                      </div>
                    )}
                    <div className="flex items-baseline justify-between pt-1 border-t border-border">
                      <span className="text-muted-foreground text-xs uppercase tracking-wide">
                        Total
                      </span>
                      <span className="font-display text-vinho text-xl font-bold">
                        {formatarMoeda(total)}
                      </span>
                    </div>
                  </div>

                  <Button
                    onClick={finalizarCompra}
                    disabled={comprar.isPending}
                    className="bg-vinho hover:bg-vinho-light text-marquee w-full"
                  >
                    {comprar.isPending ? (
                      <LoadingSpinner className="mr-2 text-marquee" />
                    ) : (
                      <Ticket className="mr-2 h-4 w-4" />
                    )}
                    Confirmar {carrinho.length}{" "}
                    {carrinho.length === 1 ? "ingresso" : "ingressos"}
                  </Button>
                </>
              )}
            </Card>
          </div>
        </div>
      </div>
    </PublicLayout>
  );
}
