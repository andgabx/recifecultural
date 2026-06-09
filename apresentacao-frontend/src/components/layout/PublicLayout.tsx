"use client";

import Link from "next/link";
import type { ReactNode } from "react";
import {
  Bell,
  Drama,
  Gavel,
  LayoutDashboard,
  TicketCheck,
  type LucideIcon,
} from "lucide-react";

import { RoleSwitcher } from "@/components/shared/RoleSwitcher";
import { Separator } from "@/components/ui/separator";
import { useNotificacoes } from "@/hooks/useNotificacoes";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import type { Papel } from "@/lib/nav";
import { useRole } from "@/lib/role";

type LinkContextual = { href: string; label: string; icon?: LucideIcon };

const linksPorPapel: Record<Papel, LinkContextual[]> = {
  espectador: [
    { href: "/", label: "Explorar" },
    { href: "/sorteios", label: "Sorteios" },
    { href: "/meus-ingressos", label: "Meus ingressos" },
    { href: "/notificacoes", label: "Notificações" },
  ],
  produtor: [
    { href: "/", label: "Explorar" },
    { href: "/produtor", label: "Painel do produtor", icon: LayoutDashboard },
  ],
  admin: [
    { href: "/", label: "Explorar" },
    { href: "/gestor/aprovacoes", label: "Administração", icon: Gavel },
  ],
  catraca: [
    { href: "/", label: "Explorar" },
    { href: "/catraca", label: "Catraca", icon: TicketCheck },
  ],
};

export function PublicLayout({ children }: { children: ReactNode }) {
  const { papel } = useRole();
  const links = linksPorPapel[papel];
  const linkPainel = links.find((l) => l.icon);
  const PainelIcon = linkPainel?.icon ?? Drama;

  const usuario = IDENTIDADES_MOCK[papel];
  const { data: notificacoesNaoLidas } = useNotificacoes(
    papel === "espectador" ? usuario.id : undefined,
    true,
  );
  const naoLidas = notificacoesNaoLidas?.length ?? 0;

  return (
    <div className="bg-nevoa text-foreground flex min-h-screen flex-col">
      <header className="bg-noite text-nevoa border-b border-noite-surface sticky top-0 z-30">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-4 px-6">
          <Link href="/" className="flex items-center gap-2">
            <span className="font-display text-xl font-bold text-laranja">
              Recife
            </span>
            <span className="font-display text-xl font-light text-nevoa/85">
              Cultural
            </span>
          </Link>

          <nav className="hidden items-center gap-6 text-sm md:flex">
            {links
              .filter((l) => !l.icon)
              .map((l) => (
                <Link
                  key={l.href}
                  href={l.href}
                  className="text-nevoa/75 hover:text-nevoa transition-colors"
                >
                  {l.label}
                </Link>
              ))}
            {linkPainel && (
              <>
                <Separator
                  orientation="vertical"
                  className="h-5 bg-noite-surface"
                />
                <Link
                  href={linkPainel.href}
                  className="text-laranja hover:text-laranja-light inline-flex items-center gap-1.5 transition-colors"
                >
                  <PainelIcon className="h-3.5 w-3.5" />
                  {linkPainel.label}
                </Link>
              </>
            )}
          </nav>

          <RoleSwitcher variant="dark" />
          {papel === "espectador" && (
            <Link
              href="/notificacoes"
              className="text-nevoa/75 hover:text-nevoa relative rounded-full p-1 transition-colors"
              aria-label="Notificações"
            >
              <Bell className="h-5 w-5" />
              {naoLidas > 0 && (
                <span className="bg-laranja text-white absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full px-1 text-[10px] font-semibold">
                  {naoLidas}
                </span>
              )}
            </Link>
          )}
        </div>
      </header>

      <main className="flex-1">{children}</main>

      <footer className="bg-noite-surface text-white/60 mt-12">
        <div className="mx-auto max-w-7xl px-6 py-8 text-sm">
          <p>
            <span className="font-display text-nevoa">Recife Cultural</span>{" "}
            — Eventos e cultura popular de Pernambuco em um só lugar.
          </p>
          <p className="mt-1 text-xs text-nevoa/40">
            © {new Date().getFullYear()} CESAR · Projeto acadêmico
          </p>
        </div>
      </footer>
    </div>
  );
}
