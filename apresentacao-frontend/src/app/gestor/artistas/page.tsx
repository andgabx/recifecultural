"use client";

import { useEffect } from "react";

export default function ArtistasGestorRedirect() {
  useEffect(() => {
    // A página de artistas foi movida para o painel do produtor
    window.location.replace("/gestor");
  }, []);

  return (
    <div className="p-8 text-center text-muted-foreground">
      Redirecionando... A página de Artistas foi movida para o painel do Produtor.
    </div>
  );
}