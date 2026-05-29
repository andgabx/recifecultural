import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

export function PageLayout({
  titulo,
  subtitulo,
  acoes,
  children,
  className,
}: {
  titulo: string;
  subtitulo?: string;
  acoes?: ReactNode;
  children: ReactNode;
  className?: string;
}) {
  return (
    <div className={cn("mx-auto max-w-7xl space-y-6 p-6", className)}>
      <header className="flex items-end justify-between gap-4 border-b border-border pb-4">
        <div>
          <h1 className="font-display text-palco text-2xl font-semibold tracking-tight">
            {titulo}
          </h1>
          {subtitulo && (
            <p className="text-muted-foreground mt-1 text-sm">{subtitulo}</p>
          )}
        </div>
        {acoes && <div className="flex items-center gap-2">{acoes}</div>}
      </header>
      {children}
    </div>
  );
}
