"use client";

import { motion } from "motion/react";
import { useMemo } from "react";

import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import type { AssentoResumo } from "@/services/bff/setores";
import type { StatusAssento } from "@/types/dominio";

const statusCor: Record<StatusAssento, string> = {
  LIVRE: "bg-marquee/20 hover:bg-marquee/30",
  PRE_RESERVADO: "bg-vinho/60 hover:bg-vinho/70",
  OCUPADO: "bg-vinho hover:bg-vinho-light",
  BLOQUEADO: "bg-border/30 hover:bg-border/40",
};

const statusLabel: Record<StatusAssento, string> = {
  LIVRE: "Livres",
  PRE_RESERVADO: "Pré-reservados",
  OCUPADO: "Ocupados",
  BLOQUEADO: "Bloqueados",
};

type AssentoGridProps = {
  assentos: AssentoResumo[];
  fileirasHorizontais: number;
  assentosPorFileiraVertical: number;
  onAssentoClick?: (assento: AssentoResumo) => void;
  className?: string;
};

const itemVariant = {
  initial: { opacity: 0, scale: 0.6 },
  animate: { opacity: 1, scale: 1 },
};

export function AssentoGrid({
  assentos,
  fileirasHorizontais,
  onAssentoClick,
  className,
}: AssentoGridProps) {
  // Agrupa assentos por fileira (letras A, B, C…)
  const fileiras = useMemo(() => {
    const mapa = new Map<string, AssentoResumo[]>();
    for (const a of assentos) {
      const lista = mapa.get(a.fileira) ?? [];
      lista.push(a);
      mapa.set(a.fileira, lista);
    }
    return Array.from(mapa.entries())
      .map(
        ([letra, lista]) =>
          [letra, [...lista].sort((x, y) => x.numero - y.numero)] as const,
      )
      .sort(([a], [b]) => a.localeCompare(b));
  }, [assentos]);

  // Contagem por status para a legenda
  const contagem = useMemo(() => {
    return assentos.reduce<Record<StatusAssento, number>>(
      (acc, a) => {
        acc[a.status] = (acc[a.status] ?? 0) + 1;
        return acc;
      },
      { LIVRE: 0, PRE_RESERVADO: 0, OCUPADO: 0, BLOQUEADO: 0 },
    );
  }, [assentos]);

  return (
    <div
      className={cn(
        "bg-palco-surface relative space-y-4 rounded-2xl p-6",
        className,
      )}
    >
      {/* Palco */}
      <div className="flex flex-col items-center">
        <div className="bg-vinho/40 text-marquee/60 w-1/2 rounded-t-lg py-2 text-center font-mono text-xs uppercase tracking-[0.4em]">
          Palco
        </div>
        <div className="bg-vinho/20 h-1 w-3/4 rounded-full" />
      </div>

      {/* Fileiras */}
      <motion.div
        initial="initial"
        animate="animate"
        transition={{ staggerChildren: 0.005 }}
        className="space-y-1.5"
      >
        {fileiras.map(([letra, lista]) => (
          <div key={letra} className="flex items-center gap-2">
            <span className="text-marquee/40 w-5 shrink-0 text-center font-mono text-xs uppercase">
              {letra}
            </span>
            <div className="flex flex-1 flex-wrap items-center justify-center gap-1">
              {lista.map((a) => (
                <motion.button
                  key={a.id}
                  type="button"
                  variants={itemVariant}
                  whileHover={{ scale: 1.2 }}
                  whileTap={{ scale: 0.9 }}
                  onClick={() => onAssentoClick?.(a)}
                  disabled={!onAssentoClick}
                  title={`${a.codigo} · ${a.status}${a.motivoIndisponibilidade ? ` (${a.motivoIndisponibilidade})` : ""}`}
                  className={cn(
                    "h-5 w-5 shrink-0 rounded-sm transition-colors",
                    statusCor[a.status],
                    !onAssentoClick && "cursor-default",
                  )}
                />
              ))}
            </div>
            <span className="text-marquee/30 w-5 shrink-0 text-center font-mono text-xs uppercase">
              {letra}
            </span>
          </div>
        ))}
      </motion.div>

      {/* Legenda */}
      <div className="border-marquee/10 flex flex-wrap items-center gap-3 border-t pt-4 text-xs">
        {(Object.keys(statusCor) as StatusAssento[]).map((s) => (
          <span key={s} className="flex items-center gap-1.5">
            <span className={cn("inline-block h-3 w-3 rounded-sm", statusCor[s])} />
            <span className="text-marquee/60">
              {statusLabel[s]}
              {contagem[s] > 0 && (
                <span className="text-marquee/90 ml-1 font-mono">
                  {contagem[s]}
                </span>
              )}
            </span>
          </span>
        ))}
        <span className="ml-auto">
          <Badge variant="outline" className="text-marquee/70 border-marquee/20">
            {fileirasHorizontais} fileiras × {assentos.length / Math.max(fileirasHorizontais, 1)} colunas
          </Badge>
        </span>
      </div>
    </div>
  );
}
