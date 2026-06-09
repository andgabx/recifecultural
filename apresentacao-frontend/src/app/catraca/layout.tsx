import type { ReactNode } from "react";

export default function CatracaLayout({ children }: { children: ReactNode }) {
  // Layout dedicado: fullscreen dark, sem AppShell/PublicLayout.
  // O CatracaPage controla todo o viewport.
  return (
    <div className="bg-noite text-nevoa flex min-h-screen flex-col">
      {children}
    </div>
  );
}
