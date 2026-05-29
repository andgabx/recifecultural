"use client";

import Link from "next/link";
import { Accessibility, ArrowRight, CalendarDays, MapPin } from "lucide-react";
import { motion } from "motion/react";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { detalheAcessibilidade } from "@/lib/acessibilidade";
import { springConfig } from "@/lib/motion";
import type { EventoResumo } from "@/services/bff/eventos";
import type { StatusEvento } from "@/types/dominio";

const statusVariant: Record<
  StatusEvento,
  "default" | "secondary" | "accent" | "frevo" | "success" | "destructive" | "outline"
> = {
  RASCUNHO: "secondary",
  EM_ANALISE: "frevo",
  APROVADO: "success",
  REPROVADO: "destructive",
  CANCELADO: "destructive",
  FINALIZADO: "outline",
};

const statusLabel: Record<StatusEvento, string> = {
  RASCUNHO: "Rascunho",
  EM_ANALISE: "Em análise",
  APROVADO: "Aprovado",
  REPROVADO: "Reprovado",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

function formatarPeriodo(inicio?: string, fim?: string) {
  if (!inicio) return "Datas a confirmar";
  const fmt = new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
  const inicioFmt = fmt.format(new Date(inicio));
  if (!fim || fim === inicio) return inicioFmt;
  return `${inicioFmt} → ${fmt.format(new Date(fim))}`;
}

export function EventCard({
  evento,
  tiposAcessibilidade,
}: {
  evento: EventoResumo;
  tiposAcessibilidade?: string[];
}) {
  return (
    <motion.div
      whileHover={{ y: -6 }}
      transition={springConfig}
      className="h-full"
    >
      <Card className="h-full overflow-hidden p-0">
        <div className="from-vinho-dark via-vinho to-capibaribe relative aspect-[4/3] overflow-hidden bg-gradient-to-br">
          <div className="absolute inset-0 opacity-20 [background-image:repeating-linear-gradient(45deg,#fff_0,#fff_1px,transparent_1px,transparent_8px)]" />
          <div className="from-palco/70 via-palco/20 absolute inset-x-0 bottom-0 h-1/2 bg-gradient-to-t to-transparent" />
          <Badge
            variant={statusVariant[evento.status]}
            className="absolute left-3 top-3"
          >
            {statusLabel[evento.status]}
          </Badge>
          {tiposAcessibilidade && tiposAcessibilidade.length > 0 && (
            <span
              title={`Acessibilidade: ${tiposAcessibilidade.map((t) => detalheAcessibilidade(t).label).join(", ")}`}
              className="bg-marquee/95 text-vinho absolute right-3 top-3 inline-flex items-center gap-1 rounded-full px-2 py-1 text-[10px] font-semibold shadow-sm"
            >
              <Accessibility className="h-3 w-3" />
              {tiposAcessibilidade.length}
            </span>
          )}
          <div className="absolute bottom-3 left-4 right-4">
            <h3 className="font-display text-marquee text-xl font-semibold leading-tight drop-shadow-md">
              {evento.titulo}
            </h3>
          </div>
        </div>
        <CardContent className="flex flex-1 flex-col gap-3 p-5">
          {evento.descricaoCurta && (
            <p className="text-muted-foreground line-clamp-2 text-sm">
              {evento.descricaoCurta}
            </p>
          )}
          <div className="text-muted-foreground space-y-1 text-xs">
            <div className="flex items-center gap-1.5">
              <CalendarDays className="text-ouro h-3.5 w-3.5" />
              <span>{formatarPeriodo(evento.periodoInicio, evento.periodoFim)}</span>
            </div>
            {evento.localId && (
              <div className="flex items-center gap-1.5">
                <MapPin className="text-ouro h-3.5 w-3.5" />
                <span className="font-mono text-[10px]">
                  {evento.localId.slice(0, 8)}…
                </span>
              </div>
            )}
          </div>
          {tiposAcessibilidade && tiposAcessibilidade.length > 0 && (
            <div className="flex flex-wrap gap-1">
              {tiposAcessibilidade.slice(0, 3).map((tipo) => {
                const Icone = detalheAcessibilidade(tipo).icon;
                return (
                  <span
                    key={tipo}
                    title={detalheAcessibilidade(tipo).label}
                    className="bg-vinho/10 text-vinho rounded-full p-1"
                  >
                    <Icone className="h-3 w-3" />
                  </span>
                );
              })}
              {tiposAcessibilidade.length > 3 && (
                <span className="text-muted-foreground text-[10px] font-medium">
                  +{tiposAcessibilidade.length - 3}
                </span>
              )}
            </div>
          )}
          <Link
            href={`/eventos/${evento.id}`}
            className="text-vinho hover:text-vinho-light mt-auto inline-flex items-center gap-1 text-sm font-medium transition-colors"
          >
            Ver detalhes
            <ArrowRight className="h-3.5 w-3.5" />
          </Link>
        </CardContent>
      </Card>
    </motion.div>
  );
}
