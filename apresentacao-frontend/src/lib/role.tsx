"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

import type { Papel } from "@/lib/nav";

const STORAGE_KEY = "recife.papel";
const PAPEIS_VALIDOS: Papel[] = ["espectador", "produtor", "admin", "catraca"];

export const papelMeta: Record<
  Papel,
  { label: string; descricao: string; rotaHome: string; cor: string }
> = {
  espectador: {
    label: "Espectador",
    descricao: "Explora eventos, compra ingressos e participa de sorteios.",
    rotaHome: "/",
    cor: "text-laranja",
  },
  produtor: {
    label: "Produtor",
    descricao: "Cria e gerencia eventos culturais, vendas e patrocínios.",
    rotaHome: "/produtor",
    cor: "text-laranja-light",
  },
  admin: {
    label: "Gestor",
    descricao: "Aprova eventos, administra espaços, artistas e bloqueios.",
    rotaHome: "/gestor/aprovacoes",
    cor: "text-violeta-dark",
  },
  catraca: {
    label: "Catraca",
    descricao: "Opera a validação de QR Codes no portão de entrada.",
    rotaHome: "/catraca",
    cor: "text-emerald-400",
  },
};

type RoleContextValue = {
  papel: Papel;
  trocar: (novo: Papel) => void;
  pronto: boolean;
};

const RoleContext = createContext<RoleContextValue | null>(null);

export function RoleProvider({ children }: { children: ReactNode }) {
  const [papel, setPapelState] = useState<Papel>("espectador");
  const [pronto, setPronto] = useState(false);

  // Hidrata do localStorage após o mount (evita mismatch SSR/CSR)
  useEffect(() => {
    if (typeof window === "undefined") return;
    const salvo = window.localStorage.getItem(STORAGE_KEY) as Papel | null;
    if (salvo && PAPEIS_VALIDOS.includes(salvo)) {
      setPapelState(salvo);
    }
    setPronto(true);
  }, []);

  const trocar = useCallback((novo: Papel) => {
    setPapelState(novo);
    if (typeof window !== "undefined") {
      window.localStorage.setItem(STORAGE_KEY, novo);
    }
  }, []);

  return (
    <RoleContext.Provider value={{ papel, trocar, pronto }}>
      {children}
    </RoleContext.Provider>
  );
}

export function useRole() {
  const ctx = useContext(RoleContext);
  if (!ctx) {
    throw new Error("useRole deve ser usado dentro de <RoleProvider>.");
  }
  return ctx;
}

export { PAPEIS_VALIDOS };
