import type { LucideIcon } from "lucide-react";
import {
  Accessibility,
  Armchair,
  Building2,
  BrainCircuit,
  CalendarOff,
  ClipboardCheck,
  DoorOpen,
  Drama,
  Gavel,
  Gift,
  Handshake,
  LayoutDashboard,
  Mic,
  Megaphone,
  ScrollText,
  Speaker,
  Tag,
  TicketCheck,
  UserCog,
  Wallet,
} from "lucide-react";

export type Papel = "espectador" | "produtor" | "admin" | "catraca";

export type ItemNav = {
  href: string;
  label: string;
  icon: LucideIcon;
};

export type GrupoNav = {
  titulo?: string;
  itens: ItemNav[];
};

export const navItens: Record<Papel, GrupoNav[]> = {
  espectador: [
    {
      itens: [
        { href: "/", label: "Explorar", icon: Drama },
        { href: "/meus-ingressos", label: "Meus Ingressos", icon: TicketCheck },
        { href: "/sorteios", label: "Sorteios", icon: Gift },
        { href: "/notificacoes", label: "Notificações", icon: Megaphone },
      ],
    },
  ],
  produtor: [
    {
      itens: [
        { href: "/produtor", label: "Dashboard", icon: LayoutDashboard },
        { href: "/produtor/eventos", label: "Eventos", icon: Drama },
        { href: "/produtor/artistas", label: "Artistas", icon: Drama },
        { href: "/produtor/financeiro", label: "Financeiro", icon: Wallet },
        { href: "/produtor/patrocinios", label: "Patrocínios", icon: Handshake },
        { href: "/produtor/sorteios", label: "Sorteios", icon: Gift },
      ],
    },
  ],
  admin: [
    {
      titulo: "Aprovação",
      itens: [
        { href: "/gestor/aprovacoes", label: "Fila de Eventos", icon: ClipboardCheck },
        { href: "/gestor/bloqueios", label: "Bloqueios", icon: CalendarOff },
      ],
    },
    {
      titulo: "Catálogo",
      itens: [
        { href: "/gestor/espacos", label: "Espaços", icon: Building2 },
        { href: "/gestor/setores", label: "Setores", icon: Armchair },
        { href: "/gestor/equipamentos", label: "Equipamentos", icon: Speaker },
        { href: "/gestor/produtores", label: "Produtores", icon: UserCog },
        { href: "/gestor/inteligencia", label: "Inteligência", icon: BrainCircuit }
      ],
    },
    {
      titulo: "Inclusão",
      itens: [
        { href: "/gestor/acessibilidade", label: "Acessibilidade", icon: Accessibility },
        { href: "/gestor/cupons", label: "Cupons", icon: Tag },
        { href: "/gestor/notificacoes", label: "Notificações", icon: Megaphone },
        { href: "/gestor/auditoria", label: "Auditoria", icon: ScrollText },
        { href: "/gestor/regras", label: "Regras", icon: Gavel },
      ],
    },
  ],
  catraca: [
    {
      itens: [{ href: "/catraca", label: "Catraca", icon: DoorOpen }],
    },
  ],
};
