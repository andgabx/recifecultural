"use client";

import { useMemo, useState } from "react";
import { ArrowRight, Plus, ScrollText, Trash2 } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { PageLayout } from "@/components/layout/PageLayout";
import { useAuditoria } from "@/hooks/useAuditoria";
import type { AcaoAuditoria, RegistroAuditoria } from "@/services/bff/auditoria";

const acaoLabel: Record<AcaoAuditoria, string> = {
  CRIADO: "Criado",
  TRANSICAO_STATUS: "Transição",
  REMOVIDO: "Removido",
};

const acaoVariant: Record<
  AcaoAuditoria,
  "default" | "success" | "frevo" | "secondary" | "destructive" | "outline"
> = {
  CRIADO: "success",
  TRANSICAO_STATUS: "frevo",
  REMOVIDO: "destructive",
};

const acaoIcone: Record<AcaoAuditoria, typeof Plus> = {
  CRIADO: Plus,
  TRANSICAO_STATUS: ArrowRight,
  REMOVIDO: Trash2,
};

function formatarMomento(iso: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  }).format(new Date(iso));
}

export default function AuditoriaPage() {
  const [filtroAcao, setFiltroAcao] = useState<"" | AcaoAuditoria>("");
  const { data, isLoading, isError } = useAuditoria({ limite: 200 });

  const registros = useMemo(() => {
    const lista = data ?? [];
    return filtroAcao ? lista.filter((r) => r.acao === filtroAcao) : lista;
  }, [data, filtroAcao]);

  return (
    <PageLayout
      titulo="Auditoria"
      subtitulo="Trilha de criações, transições e remoções de eventos. Gravada pelo Decorator EventoRepositorioComAuditoria (Par 4)."
    >
      <div className="flex flex-wrap items-end gap-3">
        <div>
          <label className="text-muted-foreground mb-1 block text-xs uppercase tracking-wide">
            Ação
          </label>
          <Select
            value={filtroAcao}
            onChange={(e) => setFiltroAcao(e.target.value as "" | AcaoAuditoria)}
            className="w-48"
          >
            <option value="">Todas</option>
            <option value="CRIADO">Criado</option>
            <option value="TRANSICAO_STATUS">Transição</option>
            <option value="REMOVIDO">Removido</option>
          </Select>
        </div>
        <p className="text-muted-foreground text-xs">
          {data ? `${registros.length} registro(s)` : "Carregando…"}
        </p>
      </div>

      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 5 }).map((_, i) => (
            <Skeleton key={i} className="h-16 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={ScrollText}
          title="Falha ao carregar a auditoria"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && registros.length === 0 && (
        <EmptyState
          icon={ScrollText}
          title="Nenhum registro de auditoria"
          description={
            filtroAcao
              ? "Nenhum evento gerou registros desse tipo ainda. Tente outro filtro."
              : "Conforme produtores criam eventos e o gestor aprova/reprova/cancela, os registros aparecem aqui em tempo real."
          }
        />
      )}

      {registros.length > 0 && (
        <ol className="space-y-2">
          {registros.map((r) => (
            <LinhaRegistro key={r.id} registro={r} />
          ))}
        </ol>
      )}
    </PageLayout>
  );
}

function LinhaRegistro({ registro }: { registro: RegistroAuditoria }) {
  const Icone = acaoIcone[registro.acao];
  return (
    <Card className="flex items-start gap-3 p-4">
      <span
        className={
          registro.acao === "REMOVIDO"
            ? "bg-destructive/10 text-destructive flex h-9 w-9 shrink-0 items-center justify-center rounded-full"
            : registro.acao === "CRIADO"
              ? "bg-emerald-100 text-emerald-700 flex h-9 w-9 shrink-0 items-center justify-center rounded-full"
              : "bg-frevo/15 text-frevo-dark flex h-9 w-9 shrink-0 items-center justify-center rounded-full"
        }
      >
        <Icone className="h-4 w-4" />
      </span>
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <Badge variant={acaoVariant[registro.acao]}>
            {acaoLabel[registro.acao]}
          </Badge>
          <Badge variant="outline" className="font-mono text-[10px] uppercase">
            {registro.entidade}
          </Badge>
          <span className="font-mono text-muted-foreground text-[10px]">
            {registro.entidadeId.slice(0, 8)}…
          </span>
          <span className="text-muted-foreground/80 ml-auto text-[10px]">
            {formatarMomento(registro.momento)}
          </span>
        </div>
        {registro.descricao && (
          <p className="text-palco mt-1.5 text-sm">{registro.descricao}</p>
        )}
        {registro.acao === "TRANSICAO_STATUS" && (
          <p className="text-muted-foreground mt-1 flex items-center gap-1 text-xs">
            <span className="rounded bg-marquee-muted px-1.5 py-0.5 font-mono">
              {registro.statusAnterior ?? "—"}
            </span>
            <ArrowRight className="h-3 w-3" />
            <span className="text-vinho rounded bg-vinho/10 px-1.5 py-0.5 font-mono font-semibold">
              {registro.statusNovo}
            </span>
          </p>
        )}
      </div>
    </Card>
  );
}
