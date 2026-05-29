"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";
import { motion } from "motion/react";

import {
  Sidebar as SidebarPrimitive,
  SidebarBody,
  SidebarLink,
  useSidebar,
  type SidebarLinkDef,
} from "@/components/ui/sidebar";
import { cn } from "@/lib/utils";
import { navItens, type Papel } from "@/lib/nav";

export function Sidebar({ papel }: { papel: Papel }) {
  const [open, setOpen] = useState(false);
  return (
    <SidebarPrimitive open={open} setOpen={setOpen} animate>
      <SidebarBody className="justify-between gap-4">
        <Conteudo papel={papel} />
      </SidebarBody>
    </SidebarPrimitive>
  );
}

function Conteudo({ papel }: { papel: Papel }) {
  const pathname = usePathname();
  const grupos = navItens[papel];

  return (
    <div className="flex h-full flex-1 flex-col">
      <Logo />
      <nav className="mt-8 flex flex-1 flex-col gap-6 overflow-y-auto pr-1">
        {grupos.map((grupo, gIndex) => (
          <div key={grupo.titulo ?? gIndex} className="space-y-1">
            {grupo.titulo && <GrupoTitulo titulo={grupo.titulo} />}
            {grupo.itens.map((item) => {
              const Icone = item.icon;
              const ativo =
                pathname === item.href ||
                (item.href !== "/" && pathname.startsWith(item.href));
              const link: SidebarLinkDef = {
                label: item.label,
                href: item.href,
                icon: <Icone className="h-4 w-4 shrink-0" />,
              };
              return <SidebarLink key={item.href} link={link} active={ativo} />;
            })}
          </div>
        ))}
      </nav>
      <Rodape papel={papel} />
    </div>
  );
}

function Logo() {
  const { open, animate } = useSidebar();
  return (
    <Link
      href="/"
      className="relative z-20 flex items-center gap-2 py-1 text-sm font-normal"
    >
      <span className="bg-vinho text-marquee flex h-7 w-7 shrink-0 items-center justify-center rounded-md">
        <span className="font-display text-xs font-bold leading-none">RC</span>
      </span>
      <motion.span
        animate={{
          display: animate ? (open ? "inline-block" : "none") : "inline-block",
          opacity: animate ? (open ? 1 : 0) : 1,
        }}
        className="font-display text-marquee whitespace-pre text-sm font-medium"
      >
        Recife Cultural
      </motion.span>
    </Link>
  );
}

function GrupoTitulo({ titulo }: { titulo: string }) {
  const { open, animate } = useSidebar();
  return (
    <motion.p
      animate={{
        opacity: animate ? (open ? 1 : 0) : 1,
        height: animate ? (open ? "auto" : 0) : "auto",
      }}
      className={cn(
        "text-marquee/35 overflow-hidden px-3 pb-1 text-[10px] font-semibold uppercase tracking-[0.2em]",
      )}
    >
      {titulo}
    </motion.p>
  );
}

function Rodape({ papel }: { papel: Papel }) {
  const { open, animate } = useSidebar();
  return (
    <motion.div
      animate={{
        opacity: animate ? (open ? 1 : 0) : 1,
        height: animate ? (open ? "auto" : 0) : "auto",
      }}
      className="border-palco-surface text-marquee/45 mt-4 overflow-hidden border-t pt-3 text-[11px] leading-snug"
    >
      <span className="text-ouro/80 font-mono uppercase tracking-widest">
        {papel}
      </span>
      <p className="mt-1">Visão sem autenticação (dev)</p>
    </motion.div>
  );
}
