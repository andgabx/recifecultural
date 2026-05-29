/*
 * Identidades mock para uso enquanto não há autenticação real.
 * UUIDs determinísticos para que `/produtor/eventos` sempre mostre os eventos
 * do mesmo "Promotor Demo", e a central de notificações sempre veja o mesmo
 * usuário do papel ativo.
 */

import type { Papel } from "@/lib/nav";

export const IDENTIDADES_MOCK: Record<Papel, { id: string; nome: string }> = {
  espectador: {
    id: "00000000-0000-0000-0000-000000000001",
    nome: "Espectador Demo",
  },
  produtor: {
    id: "00000000-0000-0000-0000-000000000002",
    nome: "Produtor Demo",
  },
  admin: {
    id: "00000000-0000-0000-0000-000000000003",
    nome: "Gestor Demo",
  },
  catraca: {
    id: "00000000-0000-0000-0000-000000000004",
    nome: "Catraca Demo",
  },
};

export function identidadePara(papel: Papel) {
  return IDENTIDADES_MOCK[papel];
}
