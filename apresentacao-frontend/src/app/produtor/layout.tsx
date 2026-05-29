import type { ReactNode } from "react";

import { AppShell } from "@/components/layout/AppShell";

export default function ProdutorLayout({ children }: { children: ReactNode }) {
  return (
    <AppShell papel="produtor" titulo="Painel do produtor">
      {children}
    </AppShell>
  );
}
