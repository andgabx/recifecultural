"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { ArrowLeft, Check, ShoppingBag, Tag } from "lucide-react";
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
  useAplicarCupom,
  useComprar,
  useComprarComCupom,
} from "@/hooks/useCheckout";
import type { ApiError } from "@/lib/api";
import type { MetodoPagamento, TipoIngresso } from "@/types/dominio";

const schema = z.object({
  eventoId: z.string().uuid("Id de evento invalido"),
  dataHoraApresentacao: z.string().min(1, "Informe a data/hora"),
  tipo: z.enum(["INTEIRA", "MEIA", "SOCIAL"]),
  valor: z.coerce.number().positive("Valor deve ser maior que zero"),
  metodoPagamento: z.enum(["PIX", "CARTAO_CREDITO", "CARTAO_DEBITO"]),
  capacidadeMaxima: z.coerce
    .number()
    .int()
    .positive("Capacidade deve ser positiva"),
  cpfComprador: z
    .string()
    .regex(/^\d{11}$/, "CPF deve ter 11 digitos")
    .optional()
    .or(z.literal("")),
  categoriaEvento: z.string().optional().or(z.literal("")),
  codigoCupom: z.string().optional().or(z.literal("")),
});

type CheckoutFormInput = z.input<typeof schema>;
type CheckoutFormOutput = z.output<typeof schema>;

const formatarMoeda = (v: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(v);

export default function CheckoutPage() {
  const router = useRouter();
  const params = useSearchParams();
  const [descontoAplicado, setDescontoAplicado] = useState<number | null>(null);

  const form = useForm<CheckoutFormInput, unknown, CheckoutFormOutput>({
    resolver: zodResolver(schema),
    defaultValues: {
      eventoId: params.get("eventoId") ?? "",
      dataHoraApresentacao: params.get("dataHoraApresentacao")?.slice(0, 16) ?? "",
      tipo: "INTEIRA",
      valor: Number(params.get("precoInteira") ?? 50),
      metodoPagamento: "PIX",
      capacidadeMaxima: 200,
      cpfComprador: "",
      categoriaEvento: "",
      codigoCupom: "",
    },
  });

  const aplicarCupom = useAplicarCupom();
  const comprar = useComprar();
  const comprarComCupom = useComprarComCupom();

  const valor = Number(form.watch("valor")) || 0;
  const valorFinal =
    descontoAplicado != null ? Math.max(valor - descontoAplicado, 1) : valor;

  async function aplicarCupomHandler() {
    const codigoCupom = form.getValues("codigoCupom")?.trim();
    const cpfComprador = form.getValues("cpfComprador")?.trim();
    const categoriaEvento = form.getValues("categoriaEvento")?.trim();

    if (!codigoCupom || !cpfComprador || !categoriaEvento) {
      toast.error(
        "Preencha cupom, CPF e categoria do evento para validar o desconto.",
      );
      return;
    }
    try {
      const resultado = await aplicarCupom.mutateAsync({
        codigoCupom,
        cpfComprador,
        categoriaEvento,
        valorPedido: valor,
      });
      if (resultado.aplicavel) {
        setDescontoAplicado(resultado.descontoAplicado);
        toast.success(
          `Cupom aplicado: ${formatarMoeda(resultado.descontoAplicado)} de desconto`,
        );
      } else {
        setDescontoAplicado(null);
        toast.warning(resultado.motivo ?? "Cupom nao aplicavel");
      }
    } catch (error) {
      const err = error as ApiError;
      setDescontoAplicado(null);
      toast.error(err.message);
    }
  }

  async function onSubmit(values: CheckoutFormOutput) {
    try {
      const dataIso = new Date(values.dataHoraApresentacao).toISOString();
      const payload = {
        eventoId: values.eventoId,
        dataHoraApresentacao: dataIso,
        tipo: values.tipo as TipoIngresso,
        valor: valorFinal,
        metodoPagamento: values.metodoPagamento as MetodoPagamento,
        capacidadeMaxima: values.capacidadeMaxima,
      };

      let resposta;
      if (
        values.codigoCupom &&
        values.cpfComprador &&
        values.categoriaEvento &&
        descontoAplicado != null
      ) {
        resposta = await comprarComCupom.mutateAsync({
          ...payload,
          codigoCupom: values.codigoCupom,
          cpfComprador: values.cpfComprador,
          categoriaEvento: values.categoriaEvento,
        });
      } else {
        resposta = await comprar.mutateAsync(payload);
      }
      toast.success(`Ingresso #${resposta.id.slice(0, 8)} confirmado!`);
      router.push(`/meus-ingressos?eventoId=${values.eventoId}`);
    } catch (error) {
      const err = error as ApiError;
      toast.error(err.message);
    }
  }

  const submetendo = comprar.isPending || comprarComCupom.isPending;

  return (
    <PublicLayout>
      <section className="mx-auto max-w-5xl space-y-6 px-6 py-10">
        <Link
          href="/"
          className="text-muted-foreground hover:text-vinho inline-flex items-center gap-1 text-sm transition-colors"
        >
          <ArrowLeft className="h-3.5 w-3.5" />
          Continuar explorando
        </Link>

        <header>
          <h1 className="font-display text-palco text-2xl font-semibold tracking-tight">
            Checkout
          </h1>
          <p className="text-muted-foreground text-sm">
            Confirme os dados, aplique um cupom (opcional) e finalize a compra.
          </p>
        </header>

        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="grid gap-6 lg:grid-cols-[1fr_360px]"
        >
          <Card className="space-y-5 p-6">
            <h2 className="font-display text-palco text-lg font-semibold">
              Dados do ingresso
            </h2>
            <FormField
              label="Id do evento"
              htmlFor="eventoId"
              error={form.formState.errors.eventoId?.message}
              required
            >
              <Input id="eventoId" {...form.register("eventoId")} />
            </FormField>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Data/hora da apresentação"
                htmlFor="dataHoraApresentacao"
                error={form.formState.errors.dataHoraApresentacao?.message}
                required
              >
                <Input
                  id="dataHoraApresentacao"
                  type="datetime-local"
                  {...form.register("dataHoraApresentacao")}
                />
              </FormField>
              <FormField
                label="Capacidade máxima"
                htmlFor="capacidadeMaxima"
                hint="Disponível no detalhe do espaço"
                error={form.formState.errors.capacidadeMaxima?.message}
                required
              >
                <Input
                  id="capacidadeMaxima"
                  type="number"
                  min={1}
                  {...form.register("capacidadeMaxima")}
                />
              </FormField>
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField label="Tipo" htmlFor="tipo" required>
                <Select id="tipo" {...form.register("tipo")}>
                  <option value="INTEIRA">Inteira</option>
                  <option value="MEIA">Meia</option>
                  <option value="SOCIAL">Social</option>
                </Select>
              </FormField>
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
            </div>
            <FormField
              label="Método de pagamento"
              htmlFor="metodoPagamento"
              required
            >
              <Select id="metodoPagamento" {...form.register("metodoPagamento")}>
                <option value="PIX">PIX (imediato)</option>
                <option value="CARTAO_CREDITO">Cartão de crédito</option>
                <option value="CARTAO_DEBITO">Cartão de débito</option>
              </Select>
            </FormField>
          </Card>

          <aside className="space-y-5">
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
                  {...form.register("codigoCupom")}
                />
              </FormField>
              <FormField label="CPF (sem pontos)" htmlFor="cpfComprador">
                <Input
                  id="cpfComprador"
                  placeholder="00000000000"
                  {...form.register("cpfComprador")}
                />
              </FormField>
              <FormField label="Categoria do evento" htmlFor="categoriaEvento">
                <Input
                  id="categoriaEvento"
                  placeholder="TEATRO"
                  {...form.register("categoriaEvento")}
                />
              </FormField>
              <Button
                type="button"
                variant="outline"
                size="sm"
                disabled={aplicarCupom.isPending}
                onClick={aplicarCupomHandler}
                className="border-ouro text-ouro-dark hover:bg-ouro hover:text-marquee w-full"
              >
                {aplicarCupom.isPending && <LoadingSpinner className="mr-2 text-ouro" />}
                {aplicarCupom.isPending ? "Validando" : "Validar cupom"}
              </Button>
              {descontoAplicado != null && (
                <Badge variant="accent" className="w-full justify-center">
                  <Check className="mr-1 h-3 w-3" />
                  Desconto: {formatarMoeda(descontoAplicado)}
                </Badge>
              )}
            </Card>

            <Card className="bg-marquee-muted border-ouro/30 space-y-3 p-5">
              <p className="text-muted-foreground text-xs uppercase tracking-widest">
                Total
              </p>
              <p className="font-display text-vinho text-3xl font-bold">
                {formatarMoeda(valorFinal)}
              </p>
              {descontoAplicado != null && descontoAplicado > 0 && (
                <p className="text-muted-foreground line-through text-xs">
                  {formatarMoeda(valor)}
                </p>
              )}
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
                Ao finalizar, voce concorda com a política de reembolso
                escalonado.
              </p>
            </Card>
          </aside>
        </form>
      </section>
    </PublicLayout>
  );
}
