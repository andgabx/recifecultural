"use client";

import Link from "next/link";
import { Bell } from "lucide-react";

import { RoleSwitcher } from "@/components/shared/RoleSwitcher";
import { useNotificacoes } from "@/hooks/useNotificacoes";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { navItens } from "@/lib/nav";
import { useRole } from "@/lib/role";

export function TopBar({ titulo }: { titulo?: string }) {
  const { papel } = useRole();
  const usuario = IDENTIDADES_MOCK[papel];
  const { data } = useNotificacoes(usuario.id, true);
  const naoLidas = data?.length ?? 0;

  const notificacoesHref =
    navItens[papel]
      .flatMap((g) => g.itens)
      .find((i) => i.label === "Notificações")?.href ?? "/notificacoes";

  return (
    <header className="bg-marquee-card border-border sticky top-0 z-20 flex h-16 items-center justify-between gap-4 border-b px-6">
      <div className="flex min-w-0 items-center gap-3">
        {titulo && (
          <h1 className="font-display text-palco truncate text-lg font-semibold tracking-tight">
            {titulo}
          </h1>
        )}
      </div>
      <div className="flex items-center gap-3">
        <Link
          href={notificacoesHref}
          className="text-muted-foreground hover:text-vinho hover:bg-vinho/5 relative rounded-full p-2 transition-colors"
          aria-label="Notificações"
        >
          <Bell className="h-5 w-5" />
          {naoLidas > 0 && (
            <span className="bg-vinho text-marquee absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full px-1 text-[10px] font-semibold">
              {naoLidas}
            </span>
          )}
        </Link>
        <RoleSwitcher variant="light" />
      </div>
    </header>
  );
}
