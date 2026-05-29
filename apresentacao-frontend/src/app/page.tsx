"use client";

import { useMemo, useState } from "react";
import { Accessibility, CalendarSearch, Filter, X } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { EventCard } from "@/components/domain/EventCard";
import { PublicLayout } from "@/components/layout/PublicLayout";
import { useRecursosConfirmadosPorEvento } from "@/hooks/useAcessibilidade";
import { useEventos } from "@/hooks/useEventos";
import { cn } from "@/lib/utils";

export default function HomePage() {
  const { data: eventos, isLoading, isError, refetch } = useEventos({
    status: "APROVADO",
  });
  const { mapa: recursosPorEvento } = useRecursosConfirmadosPorEvento();

  const [busca, setBusca] = useState("");
  const [categoria, setCategoria] = useState<string>("");
  const [dataDe, setDataDe] = useState("");
  const [dataAte, setDataAte] = useState("");
  const [apenasAcessivel, setApenasAcessivel] = useState(false);

  const categorias = useMemo(() => {
    const set = new Set<string>();
    (eventos ?? []).forEach((e) => e.categoria && set.add(e.categoria));
    return Array.from(set).sort();
  }, [eventos]);

  const filtrados = useMemo(() => {
    const buscaNorm = busca.trim().toLowerCase();
    const inicio = dataDe ? new Date(`${dataDe}T00:00:00`).getTime() : null;
    const fim = dataAte ? new Date(`${dataAte}T23:59:59`).getTime() : null;
    return (eventos ?? []).filter((e) => {
      if (categoria && e.categoria !== categoria) return false;
      if (apenasAcessivel && !recursosPorEvento.has(e.id)) return false;
      if (buscaNorm) {
        const titulo = e.titulo.toLowerCase();
        const desc = (e.descricaoCurta ?? "").toLowerCase();
        if (!titulo.includes(buscaNorm) && !desc.includes(buscaNorm)) {
          return false;
        }
      }
      if (e.periodoInicio) {
        const dataEvento = new Date(e.periodoInicio).getTime();
        if (inicio && dataEvento < inicio) return false;
        if (fim && dataEvento > fim) return false;
      } else if (inicio || fim) {
        return false;
      }
      return true;
    });
  }, [eventos, busca, categoria, dataDe, dataAte, apenasAcessivel, recursosPorEvento]);

  const temFiltro =
    busca.trim() !== "" ||
    categoria !== "" ||
    dataDe !== "" ||
    dataAte !== "" ||
    apenasAcessivel;

  function limparFiltros() {
    setBusca("");
    setCategoria("");
    setDataDe("");
    setDataAte("");
    setApenasAcessivel(false);
  }

  return (
    <PublicLayout>
      {/* Hero */}
      <section className="bg-palco text-marquee relative overflow-hidden">
        <div className="from-palco via-palco to-vinho-dark/40 absolute inset-0 bg-gradient-to-br" />
        <div className="relative mx-auto max-w-7xl space-y-5 px-6 py-20">
          <p className="text-frevo font-mono text-xs uppercase tracking-[0.25em]">
            Teatros e palcos · Recife — PE
          </p>
          <h1 className="font-display max-w-3xl text-4xl font-bold leading-tight md:text-5xl">
            Do veludo do palco ao amarelo do{" "}
            <span className="text-ouro">frevo</span>
          </h1>
          <p className="text-marquee/80 max-w-xl text-base md:text-lg">
            Compre ingressos, participe de sorteios e descubra os próximos
            espetáculos culturais da cidade.
          </p>
        </div>
      </section>

      {/* Grade de eventos */}
      <section className="mx-auto max-w-7xl space-y-6 px-6 py-12">
        <header className="flex items-end justify-between gap-4">
          <div>
            <h2 className="font-display text-palco text-2xl font-semibold tracking-tight">
              Em cartaz
            </h2>
            <p className="text-muted-foreground mt-1 text-sm">
              {eventos
                ? temFiltro
                  ? `${filtrados.length} de ${eventos.length} ${eventos.length === 1 ? "evento" : "eventos"}`
                  : `${eventos.length} eventos disponíveis`
                : "Carregando catálogo…"}
            </p>
          </div>
        </header>

        {/* Filtros */}
        <Card className="grid gap-3 p-4 md:grid-cols-[2fr_1fr_1fr_1fr_auto] md:items-end">
          <div>
            <label
              htmlFor="busca"
              className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide"
            >
              Buscar
            </label>
            <Input
              id="busca"
              placeholder="Nome do evento ou descrição…"
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
            />
          </div>
          <div>
            <label
              htmlFor="categoria"
              className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide"
            >
              Categoria
            </label>
            <Select
              id="categoria"
              value={categoria}
              onChange={(e) => setCategoria(e.target.value)}
            >
              <option value="">Todas</option>
              {categorias.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </Select>
          </div>
          <div>
            <label
              htmlFor="dataDe"
              className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide"
            >
              De
            </label>
            <Input
              id="dataDe"
              type="date"
              value={dataDe}
              onChange={(e) => setDataDe(e.target.value)}
            />
          </div>
          <div>
            <label
              htmlFor="dataAte"
              className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide"
            >
              Até
            </label>
            <Input
              id="dataAte"
              type="date"
              value={dataAte}
              onChange={(e) => setDataAte(e.target.value)}
            />
          </div>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={limparFiltros}
            disabled={!temFiltro}
            className="md:h-10"
          >
            <X className="mr-1 h-3.5 w-3.5" />
            Limpar
          </Button>
        </Card>

        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={() => setApenasAcessivel((v) => !v)}
            className={cn(
              "inline-flex items-center gap-1.5 rounded-full border px-3 py-1.5 text-xs font-medium transition-colors",
              apenasAcessivel
                ? "bg-vinho text-marquee border-vinho shadow-sm"
                : "border-border text-muted-foreground hover:border-vinho/40 hover:text-vinho",
            )}
            aria-pressed={apenasAcessivel}
          >
            <Accessibility className="h-3.5 w-3.5" />
            Apenas com acessibilidade
          </button>
          {temFiltro && (
            <div className="flex flex-wrap items-center gap-2 text-xs">
              <span className="text-muted-foreground flex items-center gap-1">
                <Filter className="h-3 w-3" /> Filtros ativos:
              </span>
              {busca && <Badge variant="secondary">"{busca}"</Badge>}
              {categoria && <Badge variant="secondary">{categoria}</Badge>}
              {dataDe && <Badge variant="secondary">De {dataDe}</Badge>}
              {dataAte && <Badge variant="secondary">Até {dataAte}</Badge>}
              {apenasAcessivel && (
                <Badge variant="secondary">Com acessibilidade</Badge>
              )}
            </div>
          )}
        </div>

        {isLoading && <GradeSkeleton />}

        {isError && (
          <Card className="border-destructive/30 bg-destructive/5 p-6">
            <p className="text-destructive text-sm">
              Não foi possível carregar os eventos. Verifique se o backend está
              rodando em <code className="font-mono">localhost:8080</code>.
            </p>
            <Button
              variant="outline"
              size="sm"
              onClick={() => refetch()}
              className="mt-3"
            >
              Tentar novamente
            </Button>
          </Card>
        )}

        {eventos && eventos.length === 0 && (
          <Card className="flex flex-col items-center gap-3 p-12 text-center">
            <CalendarSearch className="text-ouro/50 h-12 w-12" />
            <h3 className="font-display text-palco text-lg">
              Ainda sem eventos no catálogo
            </h3>
            <p className="text-muted-foreground max-w-sm text-sm">
              Quando produtores submeterem eventos e o gestor aprovar, eles
              aparecerão aqui.
            </p>
          </Card>
        )}

        {eventos && eventos.length > 0 && filtrados.length === 0 && (
          <Card className="flex flex-col items-center gap-3 p-12 text-center">
            <CalendarSearch className="text-ouro/50 h-12 w-12" />
            <h3 className="font-display text-palco text-lg">
              Nenhum evento corresponde aos filtros
            </h3>
            <p className="text-muted-foreground max-w-sm text-sm">
              Ajuste as datas, a categoria ou a busca para ver mais resultados.
            </p>
            <Button variant="outline" size="sm" onClick={limparFiltros}>
              Limpar filtros
            </Button>
          </Card>
        )}

        {filtrados.length > 0 && (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {filtrados.map((evento) => {
              const recursos = recursosPorEvento.get(evento.id) ?? [];
              const tipos = Array.from(new Set(recursos.map((r) => r.tipo)));
              return (
                <EventCard
                  key={evento.id}
                  evento={evento}
                  tiposAcessibilidade={tipos}
                />
              );
            })}
          </div>
        )}
      </section>
    </PublicLayout>
  );
}

function GradeSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {Array.from({ length: 4 }).map((_, i) => (
        <Card key={i} className="overflow-hidden p-0">
          <Skeleton className="aspect-[4/3] w-full rounded-none" />
          <div className="space-y-2 p-5">
            <Skeleton className="h-4 w-3/4" />
            <Skeleton className="h-3 w-1/2" />
            <Skeleton className="h-3 w-2/3" />
          </div>
        </Card>
      ))}
    </div>
  );
}
