import type { ReactNode } from "react";

import { Sidebar } from "@/components/layout/Sidebar";
import { TopBar } from "@/components/layout/TopBar";
import type { Papel } from "@/lib/nav";

export function AppShell({
  papel,
  titulo,
  children,
}: {
  papel: Papel;
  titulo?: string;
  children: ReactNode;
}) {
  return (
    <div className="bg-marquee text-foreground flex min-h-screen">
      <Sidebar papel={papel} />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopBar titulo={titulo} />
        <main className="flex-1 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}
