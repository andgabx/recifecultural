"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useMemo } from "react";
import { Accessibility, ArrowLeft, CalendarDays, Gift, MapPin, ShoppingBag } from "lucide-react";
import { motion } from "motion/react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { ComentariosSection } from "@/components/domain/ComentariosSection";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PublicLayout } from "@/components/layout/PublicLayout";
import { useRecursosAtivosPorEvento } from "@/hooks/useAcessibilidade";
import { useEspacos } from "@/hooks/useEspacos";
import { useEvento } from "@/hooks/useEventos";
import { useInscreverNoSorteio, useSorteiosPorEvento } from "@/hooks/useSorteios";
import { detalheAcessibilidade } from "@/lib/acessibilidade";
import type { ApiError } from "@/lib/api";
import { formatarDataHora } from "@/lib/format";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { pageVariants } from "@/lib/motion";
import { useRole } from "@/lib/role";
import { cn } from "@/lib/utils";

const formatarMoeda = (valor: string | number) =>
  new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(Number(valor));

export default function EventoDetalhePage() {
  const params = useParams<{ id: string }>();
  const { papel } = useRole();
  const usuario = IDENTIDADES_MOCK[papel];

  const { data: evento, isLoading, isError } = useEvento(params.id);
  const { data: espacos } = useEspacos();
  const { data: recursosAcessibilidade } = useRecursosAtivosPorEvento(params.id);
  const { data: sorteios } = useSorteiosPorEvento(
    evento?.status === "APROVADO" ? params.id : undefined,
  );
  const inscrever = useInscreverNoSorteio(usuario.id);

  const sorteiosAbertos = useMemo(
    () => (sorteios ?? []).filter((s) => s.status === "INSCRICOES_ABERTAS"),
    [sorteios],
  );

  const nomeLocal = useMemo(() => {
    if (!evento?.localId || !espacos) return null;
    return espacos.find((e) => e.id === evento.localId)?.nome ?? null;
  }, [evento?.localId, espacos]);

  const tiposAcessibilidade = useMemo(() => {
    const set = new Set<string>();
    (recursosAcessibilidade ?? []).forEach((r) => set.add(r.tipo));
    return Array.from(set);
  }, [recursosAcessibilidade]);

  return (
    <PublicLayout>
      <motion.div
        variants={pageVariants}
        initial="initial"
        animate="animate"
        exit="exit"
      >
        {/* Hero */}
        <section className="bg-noite text-nevoa relative overflow-hidden">
          <div className="from-azul-dark via-noite to-noite absolute inset-0 bg-gradient-to-br" />
          <div className="from-noite/80 absolute inset-0 bg-gradient-to-t to-transparent" />
          <div className="relative mx-auto max-w-5xl space-y-4 px-6 py-16">
            <Link
              href="/"
              className="text-nevoa/60 hover:text-nevoa inline-flex items-center gap-1 text-sm transition-colors"
            >
              <ArrowLeft className="h-3.5 w-3.5" />
              Voltar à grade
            </Link>
            {isLoading ? (
              <Skeleton className="bg-nevoa/20 h-12 w-2/3" />
            ) : evento ? (
              <>
                <h1 className="font-display max-w-3xl text-3xl font-bold leading-tight md:text-4xl">
                  {evento.titulo}
                </h1>
                {evento.descricaoCurta && (
                  <p className="text-nevoa/80 max-w-2xl text-base">
                    {evento.descricaoCurta}
                  </p>
                )}
              </>
            ) : null}
          </div>
        </section>

        {/* Conteudo */}
        <section className="mx-auto mt-8 max-w-5xl px-6 pb-12">
          {isLoading && (
            <Card className="space-y-3 p-8">
              <Skeleton className="h-5 w-1/3" />
              <Skeleton className="h-3 w-3/4" />
              <Skeleton className="h-3 w-2/3" />
            </Card>
          )}

          {isError && (
            <EmptyState
              icon={ArrowLeft}
              title="Evento não encontrado"
              description="Verifique o link ou volte ao catálogo principal."
              action={
                <Link
                  href="/"
                  className={cn(buttonVariants({ variant: "outline" }))}
                >
                  Voltar ao catálogo
                </Link>
              }
            />
          )}

          {evento && evento.status !== "APROVADO" && (
            <EmptyState
              icon={ArrowLeft}
              title="Evento ainda não disponível"
              description="Este evento está com status diferente de APROVADO. O catálogo público só lista eventos aprovados pelo gestor."
              action={
                <Link
                  href="/"
                  className={cn(buttonVariants({ variant: "outline" }))}
                >
                  Voltar ao catálogo
                </Link>
              }
            />
          )}

          {evento && evento.status === "APROVADO" && (
            <Card className="grid gap-8 p-8 md:grid-cols-[1fr_320px] md:gap-10 md:p-10">
              <div className="space-y-8">
                <div className="space-y-3">
                  <h2 className="font-display text-noite text-xl font-semibold">
                    Sobre o espetáculo
                  </h2>
                  <p className="text-muted-foreground whitespace-pre-line text-sm leading-relaxed">
                    {evento.descricaoLonga ??
                      evento.descricaoCurta ??
                      "Descrição completa em breve."}
                  </p>
                </div>

                <div className="grid grid-cols-1 gap-3 text-sm sm:grid-cols-2">
                  <Info
                    icon={CalendarDays}
                    label="Início"
                    valor={formatarDataHora(evento.periodoInicio)}
                  />
                  <Info
                    icon={CalendarDays}
                    label="Fim"
                    valor={formatarDataHora(evento.periodoFim)}
                  />
                  {evento.localId && (
                    <Info
                      icon={MapPin}
                      label="Local"
                      valor={
                        nomeLocal ?? (
                          <Skeleton className="h-4 w-32" />
                        )
                      }
                    />
                  )}
                </div>

                {tiposAcessibilidade.length > 0 && (
                  <div className="space-y-3">
                    <div className="flex items-center justify-between gap-3">
                      <h3 className="font-display text-noite flex items-center gap-2 text-base font-semibold">
                        <Accessibility className="text-azul h-4 w-4" />
                        Acessibilidade confirmada
                      </h3>
                      <Link
                        href={`/acessibilidade/${evento.id}`}
                        className="text-azul hover:text-azul-light text-xs font-medium transition-colors"
                      >
                        Ver detalhes →
                      </Link>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      {tiposAcessibilidade.map((tipo) => {
                        const d = detalheAcessibilidade(tipo);
                        const Icone = d.icon;
                        return (
                          <span
                            key={tipo}
                            title={d.descricao}
                            className="bg-azul/10 text-azul border-azul/20 inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs"
                          >
                            <Icone className="h-3.5 w-3.5" />
                            {d.label}
                          </span>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>

              {/* Box compra */}
              <aside className="bg-nevoa-muted border-laranja/20 space-y-5 self-start rounded-xl border p-6">
                <div className="space-y-3">
                  <p className="text-muted-foreground text-xs uppercase tracking-widest">
                    Valores do ingresso
                  </p>
                  {evento.precoInteira != null ? (
                    <div className="space-y-2">
                      <PrecoLinha
                        label="Inteira"
                        valor={formatarMoeda(evento.precoInteira)}
                        destaque
                      />
                      {evento.precoMeia != null && (
                        <PrecoLinha
                          label="Meia entrada"
                          valor={formatarMoeda(evento.precoMeia)}
                        />
                      )}
                    </div>
                  ) : (
                    <p className="font-display text-azul text-2xl font-bold">
                      Sob consulta
                    </p>
                  )}
                </div>

                <Link
                  href={{
                    pathname: "/checkout/selecionar-assento",
                    query: {
                      eventoId: evento.id,
                      dataHoraApresentacao:
                        evento.apresentacoes?.[0]?.dataHora ??
                        evento.periodoInicio ??
                        new Date().toISOString(),
                    },
                  }}
                  className={cn(
                    buttonVariants({ variant: "default", size: "lg" }),
                    "bg-azul hover:bg-azul-light text-nevoa shadow-stage w-full",
                    evento.status !== "APROVADO" && "pointer-events-none opacity-50",
                  )}
                  aria-disabled={evento.status !== "APROVADO"}
                >
                  <ShoppingBag className="mr-2 h-4 w-4" />
                  Escolher assento
                </Link>
                {evento.status !== "APROVADO" && (
                  <p className="text-muted-foreground text-center text-xs">
                    Compra liberada apenas para eventos aprovados.
                  </p>
                )}
                {tiposAcessibilidade.length === 0 && (
                  <Link
                    href={`/acessibilidade/${evento.id}`}
                    className={cn(
                      buttonVariants({ variant: "outline", size: "sm" }),
                      "w-full",
                    )}
                  >
                    <Accessibility className="mr-1 h-3.5 w-3.5" />
                    Acessibilidade
                  </Link>
                )}
              </aside>
            </Card>
          )}
        </section>

        {evento && evento.status === "APROVADO" && sorteiosAbertos.length > 0 && (
          <section className="mx-auto max-w-5xl px-6 pb-4">
            <div className="space-y-3">
              <h2 className="font-display text-noite flex items-center gap-2 text-xl font-semibold">
                <Gift className="text-violeta h-5 w-5" />
                Sorteios abertos
              </h2>
              <p className="text-muted-foreground text-sm">
                Participe gratuitamente. Ganhadores são definidos por apuração aleatória após o prazo de inscrição.
              </p>
              <div className="grid gap-3 sm:grid-cols-2">
                {sorteiosAbertos.map((s) => (
                  <Card key={s.id} className="border-t-violeta space-y-3 border-t-4 p-5">
                    <div>
                      <p className="font-display text-noite text-lg font-semibold">
                        {s.vagas} {s.vagas === 1 ? "vaga" : "vagas"}
                      </p>
                      <p className="text-muted-foreground text-xs">
                        Apresentação: {s.dataApresentacao
                          ? formatarDataHora(s.dataApresentacao)
                          : "—"}
                      </p>
                      <p className="text-muted-foreground text-xs">
                        Prazo: {formatarDataHora(s.prazoInscricao)}
                      </p>
                    </div>
                    <Button
                      size="sm"
                      onClick={async () => {
                        try {
                          await inscrever.mutateAsync({
                            sorteioId: s.id,
                            espectadorId: usuario.id,
                          });
                          toast.success(
                            "Inscrição realizada! Acompanhe o resultado em Meus Sorteios.",
                          );
                        } catch (error) {
                          toast.error((error as ApiError).message);
                        }
                      }}
                      disabled={inscrever.isPending}
                      className="bg-violeta text-noite hover:bg-violeta-dark w-full font-semibold"
                    >
                      {inscrever.isPending ? (
                        <LoadingSpinner className="mr-2 text-noite" />
                      ) : (
                        <Gift className="mr-2 h-4 w-4" />
                      )}
                      Quero participar
                    </Button>
                    <p className="text-muted-foreground text-center text-[10px]">
                      Gratuito · Resultado por sorteio aleatório
                    </p>
                  </Card>
                ))}
              </div>
            </div>
          </section>
        )}

        {evento && evento.status === "APROVADO" && (
          <section className="mx-auto max-w-5xl px-6 pb-16">
            <ComentariosSection eventoId={evento.id} />
          </section>
        )}
      </motion.div>
    </PublicLayout>
  );
}

function Info({
  icon: Icon,
  label,
  valor,
}: {
  icon: typeof CalendarDays;
  label: string;
  valor: React.ReactNode;
}) {
  return (
    <div className="border-border space-y-1 rounded-lg border p-3">
      <p className="text-muted-foreground flex items-center gap-1 text-xs uppercase tracking-wide">
        <Icon className="text-laranja h-3 w-3" />
        {label}
      </p>
      <p className="text-noite text-sm font-medium">{valor}</p>
    </div>
  );
}

function PrecoLinha({
  label,
  valor,
  destaque,
}: {
  label: string;
  valor: string;
  destaque?: boolean;
}) {
  return (
    <div className="flex items-baseline justify-between gap-3">
      <span
        className={cn(
          "text-xs uppercase tracking-wide",
          destaque ? "text-noite font-semibold" : "text-muted-foreground",
        )}
      >
        {label}
      </span>
      <span
        className={cn(
          "font-display font-bold",
          destaque ? "text-azul text-2xl" : "text-laranja-dark text-base",
        )}
      >
        {valor}
      </span>
    </div>
  );
}
