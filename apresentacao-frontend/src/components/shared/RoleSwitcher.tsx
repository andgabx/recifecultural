"use client";

import { AnimatePresence, motion } from "motion/react";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import {
  Check,
  ChevronDown,
  DoorOpen,
  Drama,
  Gavel,
  UserCircle2,
  type LucideIcon,
} from "lucide-react";

import { PAPEIS_VALIDOS, papelMeta, useRole } from "@/lib/role";
import type { Papel } from "@/lib/nav";
import { cn } from "@/lib/utils";

const icones: Record<Papel, LucideIcon> = {
  espectador: UserCircle2,
  produtor: Drama,
  admin: Gavel,
  catraca: DoorOpen,
};

type RoleSwitcherProps = {
  /**
   * "dark": para uso em fundo escuro (Sidebar/header do palco)
   * "light": para uso em fundo claro (TopBar do AppShell)
   */
  variant?: "dark" | "light";
  alinhamento?: "left" | "right";
};

export function RoleSwitcher({
  variant = "dark",
  alinhamento = "right",
}: RoleSwitcherProps) {
  const { papel, trocar, pronto } = useRole();
  const router = useRouter();
  const [aberto, setAberto] = useState(false);
  const ref = useRef<HTMLDivElement | null>(null);

  // Fecha ao clicar fora
  useEffect(() => {
    function handleClickFora(event: MouseEvent) {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setAberto(false);
      }
    }
    if (aberto) {
      document.addEventListener("mousedown", handleClickFora);
      return () => document.removeEventListener("mousedown", handleClickFora);
    }
  }, [aberto]);

  // Fecha com ESC
  useEffect(() => {
    if (!aberto) return;
    function handleEsc(event: KeyboardEvent) {
      if (event.key === "Escape") setAberto(false);
    }
    document.addEventListener("keydown", handleEsc);
    return () => document.removeEventListener("keydown", handleEsc);
  }, [aberto]);

  function selecionar(novo: Papel) {
    setAberto(false);
    if (novo === papel) return;
    trocar(novo);
    router.push(papelMeta[novo].rotaHome);
  }

  if (!pronto) {
    // Evita flash com papel diferente do salvo
    return (
      <div
        className={cn(
          "h-9 w-36 rounded-full",
          variant === "dark"
            ? "bg-palco-surface/40 animate-pulse"
            : "bg-marquee-muted animate-pulse",
        )}
      />
    );
  }

  const Icon = icones[papel];
  const meta = papelMeta[papel];

  const triggerClasses =
    variant === "dark"
      ? "bg-palco-surface/60 hover:bg-palco-surface border-palco-surface text-marquee"
      : "bg-marquee-card hover:bg-marquee-muted border-border text-palco";

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setAberto((v) => !v)}
        aria-haspopup="listbox"
        aria-expanded={aberto}
        className={cn(
          "border flex items-center gap-2 rounded-full px-3 py-1.5 text-sm font-medium transition-colors",
          triggerClasses,
        )}
      >
        <span
          className={cn(
            "flex items-center justify-center rounded-full p-1",
            "bg-vinho/15",
          )}
        >
          <Icon className={cn("h-3.5 w-3.5", meta.cor)} />
        </span>
        <span className="leading-none">
          <span
            className={cn(
              "block text-[10px] uppercase tracking-widest opacity-70",
            )}
          >
            Atuando como
          </span>
          <span className="block text-xs font-semibold leading-tight">
            {meta.label}
          </span>
        </span>
        <ChevronDown
          className={cn(
            "h-3.5 w-3.5 transition-transform",
            aberto && "rotate-180",
          )}
        />
      </button>

      <AnimatePresence>
        {aberto && (
          <motion.div
            initial={{ opacity: 0, y: -6, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -6, scale: 0.97 }}
            transition={{ duration: 0.14 }}
            role="listbox"
            className={cn(
              "bg-marquee-card border-border absolute z-50 mt-2 w-72 overflow-hidden rounded-xl border shadow-raised",
              alinhamento === "right" ? "right-0" : "left-0",
            )}
          >
            <div className="border-border bg-marquee-muted/60 border-b px-4 py-2">
              <p className="text-muted-foreground text-[10px] uppercase tracking-widest">
                Modo demonstração
              </p>
              <p className="text-palco text-xs">
                Sem autenticação real — escolha o papel para ver a UI dele.
              </p>
            </div>
            <ul className="p-1">
              {PAPEIS_VALIDOS.map((p) => {
                const PIcon = icones[p];
                const ativo = p === papel;
                const m = papelMeta[p];
                return (
                  <li key={p}>
                    <button
                      type="button"
                      role="option"
                      aria-selected={ativo}
                      onClick={() => selecionar(p)}
                      className={cn(
                        "flex w-full items-start gap-3 rounded-lg px-3 py-2.5 text-left transition-colors",
                        ativo
                          ? "bg-vinho/10 text-palco"
                          : "hover:bg-marquee-muted text-palco/85",
                      )}
                    >
                      <span
                        className={cn(
                          "mt-0.5 flex items-center justify-center rounded-md p-1.5",
                          ativo ? "bg-vinho text-marquee" : "bg-marquee-muted",
                        )}
                      >
                        <PIcon
                          className={cn(
                            "h-3.5 w-3.5",
                            !ativo && m.cor,
                          )}
                        />
                      </span>
                      <span className="flex-1">
                        <span className="text-palco flex items-center gap-1.5 text-sm font-semibold">
                          {m.label}
                          {ativo && <Check className="text-vinho h-3.5 w-3.5" />}
                        </span>
                        <span className="text-muted-foreground mt-0.5 block text-[11px] leading-snug">
                          {m.descricao}
                        </span>
                      </span>
                    </button>
                  </li>
                );
              })}
            </ul>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
