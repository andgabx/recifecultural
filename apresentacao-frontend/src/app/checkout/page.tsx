"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ArrowLeft, Check, Sofa, ShoppingBag, Tag } from "lucide-react";
import { toast } from "sonner";
import { z } from "zod";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/form/FormField";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PublicLayout } from "@/components/layout/PublicLayout";
import {
  useComprar,
  useComprarComCupom,
  useComprarComPreReserva,
} from "@/hooks/useCheckout";
import { useAplicarCupom } from "@/hooks/useCupons";
import { useEspacos } from "@/hooks/useEspacos";
import { useEvento } from "@/hooks/useEventos";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import type { MetodoPagamento } from "@/types/dominio";

// Enum backend: INTEIRA | MEIA_ENTRADA | SOCIAL
const TIPOS = [
  { value: "INTEIRA",     label: "Inteira" },
  { value: "MEIA_ENTRADA", label: "Meia entrada" },
  { value: "SOCIAL",      label: "Social" },
] as const;

type TipoBackend = typeof TIPOS[number]["value"];

const schema = z.object({
  tipo: z.enum(["INTEIRA", "MEIA_ENTRADA", "SOCIAL"]),
  metodoPagamento: z.enum(["PIX", "CARTAO_CREDITO", "CARTAO_DEBITO"]),
  cpfComprador: z
    .string()
    .regex(/^\d{11}$/, "CPF deve ter 11 dígitos")
    .optional()
    .or(z.literal("")),
  codigoCupom: z.string().optional().or(z.literal("")),
});

type CheckoutForm = z.infer<typeof schema>;

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);
export default function CheckoutPage() {
  const router = useRouter();
  const params = useSearchParams();
  const [descontoAplicado, setDescontoAplicado] = useState<number | null>(null);

  const eventoId     = params.get("eventoId") ?? "";
  const dataHoraIso  = params.get("dataHoraApresentacao") ?? "";
  const preReservaId = params.get("preReservaId") ?? "";
  const assentoId    = params.get("assentoId") ?? "";
  const assentoCodigo = params.get("assentoCodigo") ?? "";

  const temPreReserva = Boolean(preReservaId && assentoId);

  const { data: evento } = useEvento(eventoId || undefined);
  const { data: espacos } = useEspacos();

  const capacidadeMaxima = espacos?.find((e) => e.id === evento?.localId)
    ?.capacidadeMaxima ?? 500;

  const precoInteira = evento?.precoInteira ? Number(evento.precoInteira) : 0;
  const precoMeia    = evento?.precoMeia    ? Number(evento.precoMeia)    : 0;
  const categoria    = evento?.categoria    ?? "";

  const form = useForm<CheckoutForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      tipo: "INTEIRA",
      metodoPagamento: "PIX",
      cpfComprador: "",
      codigoCupom: "",
    },
  });

  const tipoSelecionado = form.watch("tipo") as TipoBackend;

  // valor que vai para o backend (antes de desconto)
  const valorBase =
    tipoSelecionado === "INTEIRA"      ? precoInteira :
    tipoSelecionado === "MEIA_ENTRADA" ? precoMeia    :
    tipoSelecionado === "SOCIAL"       ? 0            : 0;

  const valorFinal =
    descontoAplicado != null ? Math.max(valorBase - descontoAplicado, 0) : valorBase;

  // limpa desconto quando muda o tipo
  useEffect(() => { setDescontoAplicado(null); }, [tipoSelecionado]);

  const aplicarCupomMutation = useAplicarCupom();
  const comprar              = useComprar();
  const comprarComCupom      = useComprarComCupom();
  const comprarComPreReserva = useComprarComPreReserva();

  async function aplicarCupomHandler() {
    const codigo = form.getValues("codigoCupom")?.trim();
    const cpf    = form.getValues("cpfComprador")?.trim();

    if (!codigo || !cpf) {
      toast.error("Preencha o código do cupom e o CPF.");
      return;
    }
    if (!categoria) {
      toast.error("O evento não tem categoria definida — cupom não pode ser validado.");
      return;
    }
    try {
      const resultado = await aplicarCupomMutation.mutateAsync({
        codigoCupom: codigo,
        cpfComprador: cpf,
        categoriaEvento: categoria,
        valorPedido: valorBase,
      });
      if (resultado.aplicavel) {
        setDescontoAplicado(resultado.descontoAplicado);
        toast.success(`Cupom aplicado: ${formatarMoeda(resultado.descontoAplicado)} de desconto`);
      } else {
        setDescontoAplicado(null);
        toast.warning(resultado.motivo ?? "Cupom não aplicável");
      }
    } catch (error) {
      setDescontoAplicado(null);
      toast.error((error as ApiError).message);
    }
  }

  async function onSubmit(values: CheckoutForm) {
    if (!eventoId || !dataHoraIso) {
      toast.error("Dados do evento ausentes. Volte ao detalhe do evento.");
      return;
    }
    try {
      const dataIso = new Date(dataHoraIso).toISOString();
      const basePayload = {
        eventoId,
        dataHoraApresentacao: dataIso,
        tipo: values.tipo,
        valor: valorFinal,
        metodoPagamento: values.metodoPagamento as MetodoPagamento,
        capacidadeMaxima,
      };

      let resposta;
      if (temPreReserva) {
        // Compra confirmando pré-reserva de assento
        resposta = await comprarComPreReserva.mutateAsync({
          ...basePayload,
          preReservaId,
          assentoId,
        });
      } else if (values.codigoCupom && values.cpfComprador && descontoAplicado != null) {
        resposta = await comprarComCupom.mutateAsync({
          ...basePayload,
          codigoCupom: values.codigoCupom,
          cpfComprador: values.cpfComprador,
          categoriaEvento: categoria,
        });
      } else {
        resposta = await comprar.mutateAsync(basePayload);
      }

      toast.success(`Ingresso #${resposta.id.slice(0, 8)} confirmado!`);
      router.push(`/meus-ingressos?eventoId=${eventoId}`);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const submetendo = comprar.isPending || comprarComCupom.isPending || comprarComPreReserva.isPending;

  return (
    <PublicLayout>
      <section className="mx-auto max-w-5xl space-y-6 px-6 py-10">
        <Link
          href={eventoId ? `/eventos/${eventoId}` : "/"}
          className="text-muted-foreground hover:text-vinho inline-flex items-center gap-1 text-sm transition-colors"
        >
          <ArrowLeft className="h-3.5 w-3.5" />
          Voltar ao evento
        </Link>

        <header>
          <h1 className="font-display text-palco text-2xl font-semibold tracking-tight">
            Checkout
          </h1>
          {evento && (
            <p className="text-muted-foreground text-sm">{evento.titulo}</p>
          )}
        </header>

        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="grid gap-6 lg:grid-cols-[1fr_360px]"
        >
          {/* Coluna esquerda */}
          <div className="space-y-4">
            {/* Resumo do evento (read-only) */}
            <Card className="space-y-3 p-6">
              <h2 className="font-display text-palco text-lg font-semibold">
                Apresentação
              </h2>
              <div className="text-muted-foreground grid gap-1 text-sm">
                <p>
                  <span className="text-palco font-medium">Data: </span>
                  {dataHoraIso ? formatarDataHora(dataHoraIso) : "—"}
                </p>
                <p>
                  <span className="text-palco font-medium">Evento: </span>
                  {evento?.titulo ?? "Carregando…"}
                </p>
              </div>
            </Card>

            {/* Card de assento reservado (quando vem da seleção de mapa) */}
            {temPreReserva && (
              <Card className="border-ouro/40 bg-ouro/5 flex items-center gap-3 p-4">
                <Sofa className="text-ouro h-5 w-5 shrink-0" />
                <div>
                  <p className="text-palco text-sm font-semibold">
                    Assento {assentoCodigo || assentoId.slice(0, 8)} reservado
                  </p>
                  <p className="text-muted-foreground text-xs">
                    Pré-reserva ativa — finalize a compra antes do tempo expirar.
                  </p>
                </div>
              </Card>
            )}

            {/* Seleção de tipo */}
            <Card className="space-y-5 p-6">
              <h2 className="font-display text-palco text-lg font-semibold">
                Ingresso
              </h2>

              <FormField label="Tipo de ingresso" htmlFor="tipo" required>
                <Select id="tipo" {...form.register("tipo")}>
                  {TIPOS.map((t) => {
                    // esconde meia se o evento não tem preço meia
                    if (t.value === "MEIA_ENTRADA" && !precoMeia) return null;
                    return (
                      <option key={t.value} value={t.value}>
                        {t.label}{" "}
                        {t.value === "INTEIRA" && precoInteira
                          ? `— ${formatarMoeda(precoInteira)}`
                          : t.value === "MEIA_ENTRADA" && precoMeia
                          ? `— ${formatarMoeda(precoMeia)}`
                          : t.value === "SOCIAL"
                          ? "— gratuito (sujeito a comprovação)"
                          : ""}
                      </option>
                    );
                  })}
                </Select>
              </FormField>

              <FormField label="Método de pagamento" htmlFor="metodoPagamento" required>
                <Select id="metodoPagamento" {...form.register("metodoPagamento")}>
                  <option value="PIX">PIX</option>
                  <option value="CARTAO_CREDITO">Cartão de crédito</option>
                  <option value="CARTAO_DEBITO">Cartão de débito</option>
                </Select>
              </FormField>
            </Card>
          </div>

          {/* Coluna direita */}
          <aside className="space-y-5">
            {/* Cupom */}
            <Card className="border-dashed border-ouro/40 space-y-3 p-5">
              <div className="flex items-center gap-2">
                <Tag className="text-ouro h-4 w-4" />
                <h3 className="font-display text-palco text-sm font-semibold">
                  Cupom de desconto
                </h3>
              </div>
              <FormField label="Código" htmlFor="codigoCupom">
                <Input
                  id="codigoCupom"
                  placeholder="FREVO20"
                  className="uppercase"
                  {...form.register("codigoCupom")}
                />
              </FormField>
              <FormField
                label="CPF (sem pontos)"
                htmlFor="cpfComprador"
                error={form.formState.errors.cpfComprador?.message}
              >
                <Input
                  id="cpfComprador"
                  placeholder="00000000000"
                  maxLength={11}
                  {...form.register("cpfComprador")}
                />
              </FormField>
              <Button
                type="button"
                variant="outline"
                size="sm"
                disabled={aplicarCupomMutation.isPending}
                onClick={aplicarCupomHandler}
                className="border-ouro text-ouro-dark hover:bg-ouro hover:text-marquee w-full"
              >
                {aplicarCupomMutation.isPending && (
                  <LoadingSpinner className="mr-2 text-ouro" />
                )}
                {aplicarCupomMutation.isPending ? "Validando…" : "Validar cupom"}
              </Button>
              {descontoAplicado != null && (
                <Badge variant="accent" className="w-full justify-center">
                  <Check className="mr-1 h-3 w-3" />
                  Desconto: {formatarMoeda(descontoAplicado)}
                </Badge>
              )}
            </Card>

            {/* Total e finalizar */}
            <Card className="bg-marquee-muted border-ouro/30 space-y-4 p-5">
              <p className="text-muted-foreground text-xs uppercase tracking-widest">
                Total
              </p>
              <div>
                {descontoAplicado != null && descontoAplicado > 0 && (
                  <p className="text-muted-foreground line-through text-sm">
                    {formatarMoeda(valorBase)}
                  </p>
                )}
                <p className="font-display text-vinho text-3xl font-bold">
                  {formatarMoeda(valorFinal)}
                </p>
              </div>
              <Button
                type="submit"
                disabled={submetendo}
                className="bg-vinho hover:bg-vinho-light text-marquee shadow-stage w-full"
              >
                {submetendo && <LoadingSpinner className="mr-2 text-marquee" />}
                <ShoppingBag className="mr-2 h-4 w-4" />
                Finalizar compra
              </Button>
              <p className="text-muted-foreground text-center text-[10px]">
                Ao finalizar, você concorda com a política de reembolso escalonado.
              </p>
            </Card>
          </aside>
        </form>
      </section>
    </PublicLayout>
  );
}
