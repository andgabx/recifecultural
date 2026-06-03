"use client";

import { ComboboxAsync } from "@/components/form/ComboboxAsync";
import { formatarDataHora } from "@/lib/format";
import { artistasService } from "@/services/bff/artistas";
import { espacosService } from "@/services/bff/espacos";
import { eventosService } from "@/services/bff/eventos";
import { produtoresService } from "@/services/bff/produtores";
import type { StatusEvento, UUID } from "@/types/dominio";

type Base = {
  id?: string;
  value: string;
  onChange: (value: string) => void;
  className?: string;
};

export function SeletorEspaco(props: Base) {
  return (
    <ComboboxAsync
      {...props}
      queryKey={["espacos"]}
      queryFn={espacosService.listar}
      toOption={(e) => ({
        value: e.id,
        label: e.nome,
        hint: `${e.capacidadeMaxima} lugares · ${e.status}`,
      })}
      placeholder="Selecione um espaço"
      emptyMessage="Nenhum espaço cadastrado"
    />
  );
}

export function SeletorProdutor(props: Base) {
  return (
    <ComboboxAsync
      {...props}
      queryKey={["produtores"]}
      queryFn={produtoresService.listar}
      toOption={(p) => ({
        value: p.id,
        label: p.nomeFantasia,
        hint: p.status,
      })}
      placeholder="Selecione um produtor"
      emptyMessage="Nenhum produtor cadastrado"
    />
  );
}

export function SeletorArtista(props: Base) {
  return (
    <ComboboxAsync
      {...props}
      queryKey={["artistas"]}
      queryFn={artistasService.listar}
      toOption={(a) => ({
        value: a.id,
        label: a.nome,
        hint: a.status,
      })}
      placeholder="Selecione um artista"
      emptyMessage="Nenhum artista cadastrado"
    />
  );
}

export function SeletorEvento(
  props: Base & { promotorId?: UUID; status?: StatusEvento | StatusEvento[] },
) {
  const { promotorId, status, ...rest } = props;
  const statusFiltro = status
    ? Array.isArray(status)
      ? status
      : [status]
    : undefined;
  return (
    <ComboboxAsync
      {...rest}
      queryKey={
        promotorId
          ? ["eventos", "produtor", promotorId, { status: statusFiltro }]
          : ["eventos", { status: statusFiltro }]
      }
      queryFn={async () => {
        const eventos = promotorId
          ? await eventosService.listarPorProdutor(promotorId)
          : await eventosService.listar();
        if (!statusFiltro) return eventos;
        return eventos.filter((e) => statusFiltro.includes(e.status));
      }}
      toOption={(e) => ({
        value: e.id,
        label: e.titulo,
        hint: e.status,
      })}
      placeholder="Selecione um evento"
      emptyMessage={
        statusFiltro?.includes("APROVADO")
          ? "Nenhum evento aprovado disponível"
          : "Nenhum evento disponível"
      }
    />
  );
}

/**
 * Seleciona uma das datas de apresentação de um evento.
 * O `value` exposto é o `apresentacaoId` determinístico (gerado pelo BFF a partir
 * de eventoId + dataIso), de modo que sorteios e acessibilidade marcam a
 * mesma apresentação consistentemente sem o usuário digitar UUID.
 */
export function SeletorApresentacao(
  props: Base & { eventoId: UUID | undefined },
) {
  const { eventoId, ...rest } = props;
  return (
    <ComboboxAsync
      {...rest}
      enabled={Boolean(eventoId)}
      queryKey={["evento", eventoId ?? "vazio", "apresentacoes"]}
      queryFn={async () => {
        if (!eventoId) return [];
        const evento = await eventosService.buscar(eventoId);
        return evento.apresentacoes ?? [];
      }}
      toOption={(a) => ({
        value: a.id,
        label: formatarDataHora(a.dataHora),
        hint: a.id.slice(0, 8) + "…",
      })}
      placeholder={
        eventoId
          ? "Selecione uma apresentação"
          : "Escolha um evento primeiro"
      }
      emptyMessage="Este evento ainda não tem datas de apresentação programadas"
    />
  );
}
