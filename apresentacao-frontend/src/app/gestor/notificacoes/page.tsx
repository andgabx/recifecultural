"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  AlertCircle,
  Bell,
  CheckCircle2,
  Gift,
  Inbox,
  Megaphone,
  ShieldAlert,
  Ticket,
  Users,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEvento } from "@/components/form/Seletores";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PageLayout } from "@/components/layout/PageLayout";
import { useEnviarBroadcast, useNotificacoes } from "@/hooks/useNotificacoes";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import type { UUID } from "@/types/dominio";

const DESTINOS = [
  {
    contexto: "TODOS_USUARIOS",
    label: "Todos os usuários",
    descricao: "Espectadores, produtores e gestores",
    icon: Users,
    precisaEvento: false,
  },
  {
    contexto: "TITULARES_INGRESSOS_EVENTO",
    label: "Participantes de um evento",
    descricao: "Quem comprou ingresso para o evento selecionado",
    icon: Ticket,
    precisaEvento: true,
  },
  {
    contexto: "PROMOTORES",
    label: "Produtores / Promotores",
    descricao: "Todos os produtores cadastrados",
    icon: Megaphone,
    precisaEvento: false,
  },
  {
    contexto: "ARTISTAS_EVENTO",
    label: "Artistas de um evento",
    descricao: "Artistas vinculados ao evento selecionado",
    icon: Bell,
    precisaEvento: true,
  },
] as const;

const schema = z
  .object({
    mensagem: z.string().min(5, "Mínimo de 5 caracteres").max(2000, "Máximo de 2000 caracteres"),
    contexto: z.string().min(1, "Selecione um destino"),
    idReferencia: z.string().optional().or(z.literal("")),
  })
  .refine(
    (v) => {
      const destino = DESTINOS.find((d) => d.contexto === v.contexto);
      if (destino?.precisaEvento) return !!v.idReferencia;
      return true;
    },
    { message: "Selecione um evento", path: ["idReferencia"] },
  );

type BroadcastForm = z.infer<typeof schema>;

const iconePorContexto: Record<string, React.ElementType> = {
  EVENTO_CANCELADO: AlertCircle,
  PARTICIPANTES_EVENTO_CANCELADO: AlertCircle,
  EVENTO_APROVADO: CheckCircle2,
  EVENTO_REPROVADO: ShieldAlert,
  SORTEIO_CANCELADO: Gift,
  SORTEIO_GANHADOR: Gift,
  SORTEIO_PROMOCAO: Gift,
  ACESSIBILIDADE_REMOVIDA: ShieldAlert,
  INGRESSO_CONFIRMADO: Ticket,
};

export default function GestorNotificacoesPage() {
  const gestor = IDENTIDADES_MOCK.admin;
  const { data: recebidas, isLoading } = useNotificacoes(gestor.id);
  const enviar = useEnviarBroadcast();

  const form = useForm<BroadcastForm>({
    resolver: zodResolver(schema),
    defaultValues: { mensagem: "", contexto: "TODOS_USUARIOS", idReferencia: "" },
  });

  const contextoSelecionado = form.watch("contexto");
  const destinoAtual = DESTINOS.find((d) => d.contexto === contextoSelecionado);

  async function onSubmit(values: BroadcastForm) {
    try {
      await enviar.mutateAsync({
        mensagem: values.mensagem,
        contexto: values.contexto,
        idReferencia: values.idReferencia || undefined,
      });
      toast.success("Notificação enviada.");
      form.reset({ mensagem: "", contexto: "TODOS_USUARIOS", idReferencia: "" });
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Notificações"
      subtitulo="Envie comunicados e acompanhe as notificações recebidas."
    >
      <div className="space-y-8">
        {/* Formulário de envio */}
        <Card className="space-y-5 p-6">
          <h2 className="font-display text-palco text-lg font-semibold">Enviar comunicado</h2>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {/* Destino */}
            <FormField
              label="Destinatários"
              htmlFor="contexto"
              error={form.formState.errors.contexto?.message}
              required
            >
              <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                {DESTINOS.map((d) => {
                  const Icon = d.icon;
                  const selected = contextoSelecionado === d.contexto;
                  return (
                    <button
                      key={d.contexto}
                      type="button"
                      onClick={() => {
                        form.setValue("contexto", d.contexto);
                        form.setValue("idReferencia", "");
                      }}
                      className={`flex items-start gap-3 rounded-lg border p-3 text-left transition-colors ${
                        selected
                          ? "border-vinho bg-vinho/5"
                          : "border-border hover:bg-marquee-muted"
                      }`}
                    >
                      <Icon className={`mt-0.5 h-4 w-4 shrink-0 ${selected ? "text-vinho" : "text-muted-foreground"}`} />
                      <div>
                        <p className={`text-sm font-medium ${selected ? "text-vinho" : "text-palco"}`}>
                          {d.label}
                        </p>
                        <p className="text-xs text-muted-foreground">{d.descricao}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </FormField>

            {/* Seletor de evento (condicional) */}
            {destinoAtual?.precisaEvento && (
              <FormField
                label="Evento"
                htmlFor="idReferencia"
                error={form.formState.errors.idReferencia?.message}
                required
              >
                <SeletorEvento
                  id="idReferencia"
                  value={(form.watch("idReferencia") as UUID) ?? ""}
                  onChange={(v) => form.setValue("idReferencia", v)}
                  status="APROVADO"
                />
              </FormField>
            )}

            {/* Mensagem */}
            <FormField
              label="Mensagem"
              htmlFor="mensagem"
              error={form.formState.errors.mensagem?.message}
              hint={`${form.watch("mensagem")?.length ?? 0} / 2000`}
              required
            >
              <textarea
                id="mensagem"
                rows={4}
                {...form.register("mensagem")}
                placeholder="Informe o comunicado que será enviado aos destinatários…"
                className="border-border bg-marquee-card placeholder:text-muted-foreground focus-visible:border-vinho focus-visible:ring-vinho/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
              />
            </FormField>

            <div className="flex justify-end">
              <Button
                type="submit"
                disabled={enviar.isPending}
                className="bg-vinho hover:bg-vinho-light text-marquee"
              >
                {enviar.isPending && <LoadingSpinner className="mr-2 text-marquee" />}
                Enviar notificação
              </Button>
            </div>
          </form>
        </Card>

        {/* Notificações recebidas pelo gestor */}
        <section className="space-y-3">
          <h2 className="font-display text-palco text-lg font-semibold">Recebidas</h2>

          {isLoading && (
            <div className="space-y-2">
              {Array.from({ length: 3 }).map((_, i) => (
                <Skeleton key={i} className="h-14 w-full" />
              ))}
            </div>
          )}

          {!isLoading && (!recebidas || recebidas.length === 0) && (
            <EmptyState
              icon={Inbox}
              title="Nenhuma notificação"
              description="As notificações enviadas ao gestor aparecerão aqui."
            />
          )}

          {recebidas && recebidas.length > 0 && (
            <ul className="space-y-2">
              {recebidas.map((n) => {
                const Icon = iconePorContexto[n.contexto] ?? Bell;
                return (
                  <li
                    key={n.id}
                    className={`flex items-start gap-3 rounded-lg border p-4 ${
                      !n.foiLida ? "bg-vinho/5 border-l-vinho border-l-2" : ""
                    }`}
                  >
                    <span className="mt-0.5 flex h-9 w-9 items-center justify-center rounded-lg bg-marquee-muted">
                      <Icon className="h-4 w-4 text-vinho" />
                    </span>
                    <div className="min-w-0 flex-1 space-y-1">
                      <p className="text-palco text-sm">{n.mensagem}</p>
                      <p className="text-muted-foreground flex items-center gap-2 text-xs">
                        <span className="font-mono uppercase tracking-widest text-[10px]">
                          {n.contexto}
                        </span>
                        <span>·</span>
                        <span>{formatarDataHora(n.dataCriacao)}</span>
                      </p>
                    </div>
                    {!n.foiLida && (
                      <Badge variant="frevo" className="shrink-0 text-[10px]">Nova</Badge>
                    )}
                  </li>
                );
              })}
            </ul>
          )}
        </section>
      </div>
    </PageLayout>
  );
}
