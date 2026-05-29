"use client";

import {
  Accessibility,
  AudioLines,
  BookOpenCheck,
  Captions,
  HandHelping,
  ParkingMeter,
  type LucideIcon,
} from "lucide-react";

import type { TipoRecursoAcessibilidade } from "@/types/dominio";

type Detalhe = {
  label: string;
  descricao: string;
  icon: LucideIcon;
};

export const DETALHES_ACESSIBILIDADE: Record<TipoRecursoAcessibilidade, Detalhe> = {
  LIBRAS: {
    label: "Intérprete de Libras",
    descricao:
      "Apresentação contará com intérprete em Língua Brasileira de Sinais visível ao público.",
    icon: HandHelping,
  },
  AUDIODESCRICAO: {
    label: "Audiodescrição",
    descricao:
      "Narração de imagens e cenas via fone, para pessoas com deficiência visual.",
    icon: AudioLines,
  },
  LEGENDA_DESCRITIVA: {
    label: "Legenda descritiva",
    descricao:
      "Diálogos e sons relevantes legendados em tempo real para o público surdo.",
    icon: Captions,
  },
  ESPACO_PCD: {
    label: "Espaço para PCD",
    descricao:
      "Lugares reservados para cadeirantes e acompanhantes com acessos sem barreiras.",
    icon: ParkingMeter,
  },
  PROGRAMA_BRAILE: {
    label: "Programa em braile",
    descricao:
      "Material impresso em braile disponível na bilheteria do evento.",
    icon: BookOpenCheck,
  },
};

const FALLBACK: Detalhe = {
  label: "Acessibilidade",
  descricao: "Recurso de acessibilidade confirmado.",
  icon: Accessibility,
};

export function detalheAcessibilidade(tipo: string): Detalhe {
  return DETALHES_ACESSIBILIDADE[tipo as TipoRecursoAcessibilidade] ?? FALLBACK;
}
