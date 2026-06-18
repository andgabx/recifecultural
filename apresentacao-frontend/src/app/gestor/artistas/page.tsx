"use client";

import { PageLayout } from "@/components/layout/PageLayout";
import { DataTable } from "@/components/shared/DataTable";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useArtistas, Artista } from "@/hooks/useArtistas";
import { Ban, Power } from "lucide-react";
import Link from "next/link";

// ---------------------------------------------------------------------------
// Page: /gestor/artistas
// Visão consolidada de todos os artistas cadastrados na plataforma.
// O gestor pode inativar/reativar qualquer artista e navegar até o produtor.
// ---------------------------------------------------------------------------
export default function ArtistasGestorPage() {
  const { artistas, isLoading, inativarArtista, reativarArtista } = useArtistas();

  const handleInativar = async (artista: Artista) => {
    if (confirm(`Confirma a inativação de "${artista.nome}"?`)) {
      try {
        await inativarArtista(artista.id);
      } catch {
        alert("Erro ao inativar artista.");
      }
    }
  };

  const handleReativar = async (artista: Artista) => {
    if (confirm(`Confirma a reativação de "${artista.nome}"?`)) {
      try {
        await reativarArtista(artista.id);
      } catch {
        alert("Erro ao reativar artista.");
      }
    }
  };

  const columns = [
    {
      header: "Nome",
      accessor: "nome" as keyof Artista,
      cell: (row: Artista) => <span className="font-medium">{row.nome}</span>,
    },
    {
      header: "CPF / CNPJ",
      accessor: "cpfCnpj" as keyof Artista,
      cell: (row: Artista) => (
        <span className="font-mono text-sm">{row.cpfCnpj}</span>
      ),
    },
    {
      header: "Produtor",
      accessor: "produtorNome" as keyof Artista,
      cell: (row: Artista) =>
        row.produtorId ? (
          <Link
            href={`/gestor/produtores/${row.produtorId}/artistas`}
            className="text-sm underline underline-offset-2 text-muted-foreground hover:text-foreground transition-colors"
          >
            {row.produtorNome ?? row.produtorId}
          </Link>
        ) : (
          <span className="text-muted-foreground text-sm">—</span>
        ),
    },
    {
      header: "Status",
      accessor: "status" as keyof Artista,
      cell: (row: Artista) => (
        <Badge variant={row.status === "ATIVO" ? "default" : "secondary"}>
          {row.status}
        </Badge>
      ),
    },
    {
      header: "Ações",
      accessor: "id" as keyof Artista,
      cell: (row: Artista) => (
        <div className="flex gap-2 items-center">
          {row.status === "ATIVO" && (
            <Button
              variant="destructive"
              size="sm"
              title="Inativar artista"
              onClick={() => handleInativar(row)}
            >
              <Ban className="w-4 h-4" />
            </Button>
          )}
          {row.status === "INATIVO" && (
            <Button
              variant="outline"
              size="sm"
              title="Reativar artista"
              onClick={() => handleReativar(row)}
            >
              <Power className="w-4 h-4 text-green-500" />
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Artistas"
      subtitulo="Visão consolidada de todos os artistas cadastrados na plataforma."
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={artistas ?? []}
        empty="Nenhum artista encontrado na plataforma."
      />
    </PageLayout>
  );
}