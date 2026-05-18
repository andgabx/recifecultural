"use client";

import Link from "next/link";
import type { ReactNode } from "react";
import {
  Drama,
  Gavel,
  LayoutDashboard,
  TicketCheck,
  type LucideIcon,
} from "lucide-react";

import { RoleSwitcher } from "@/components/shared/RoleSwitcher";
import { Separator } from "@/components/ui/separator";
import type { Papel } from "@/lib/nav";
import { useRole } from "@/lib/role";

type LinkContextual = { href: string; label: string; icon?: LucideIcon };

const linksPorPapel: Record<Papel, LinkContextual[]> = {
  espectador: [
    { href: "/", label: "Explorar" },
    { href: "/sorteios", label: "Sorteios" },
    { href: "/meus-ingressos", label: "Meus ingressos" },
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

  return (
    <div className="bg-marquee text-foreground flex min-h-screen flex-col">
      <header className="bg-palco text-marquee border-b border-palco-surface sticky top-0 z-30">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-4 px-6">
          <Link href="/" className="flex items-center gap-2">
            <span className="font-display text-xl font-bold text-ouro">
              Recife
            </span>
            <span className="font-display text-xl font-light text-marquee/85">
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
                  className="text-marquee/75 hover:text-marquee transition-colors"
                >
                  {l.label}
                </Link>
              ))}
            {linkPainel && (
              <>
                <Separator
                  orientation="vertical"
                  className="h-5 bg-palco-surface"
                />
                <Link
                  href={linkPainel.href}
                  className="text-ouro hover:text-ouro-light inline-flex items-center gap-1.5 transition-colors"
                >
                  <PainelIcon className="h-3.5 w-3.5" />
                  {linkPainel.label}
                </Link>
              </>
            )}
          </nav>

          <RoleSwitcher variant="dark" />
        </div>
      </header>

      <main className="flex-1">{children}</main>

      <footer className="bg-vinho-dark text-marquee/60 mt-12">
        <div className="mx-auto max-w-7xl px-6 py-8 text-sm">
          <p>
            <span className="font-display text-marquee">Recife Cultural</span>{" "}
            — Teatros e palcos de Pernambuco em um só lugar.
          </p>
          <p className="mt-1 text-xs text-marquee/40">
            © {new Date().getFullYear()} CESAR · Projeto acadêmico
          </p>
        </div>
      </footer>
    </div>
  );
}
