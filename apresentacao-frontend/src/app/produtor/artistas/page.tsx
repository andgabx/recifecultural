"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { PageLayout } from "@/components/layout/PageLayout";
import { DataTable } from "@/components/shared/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Modal } from "@/components/shared/Modal";
import { Badge } from "@/components/ui/badge";
import { useArtistas, Artista } from "@/hooks/useArtistas";
import { Ban, Plus, Power } from "lucide-react";

// ---------------------------------------------------------------------------
// Page: /produtor/[produtorId]/artistas
// produtorId vem da URL — sem dependência de sessão ou contexto global.
// ---------------------------------------------------------------------------
export default function ArtistasProdutorPage() {
  const params = useParams();
  const produtorId = params?.produtorId as string;

  const { artistas, isLoading, createArtista, inativarArtista, reativarArtista } =
    useArtistas(produtorId);

  const [isCadastroOpen, setIsCadastroOpen] = useState(false);
  const [loadingForm, setLoadingForm] = useState(false);
  const [formData, setFormData] = useState({ nome: "", riderItens: "" });

  const handleCadastro = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoadingForm(true);
    try {
      await createArtista({
        produtorId,
        nome: formData.nome,
        riderItens: formData.riderItens
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setFormData({ nome: "", riderItens: "" });
      setIsCadastroOpen(false);
    } catch {
      alert("Erro ao cadastrar artista. Tente novamente.");
    } finally {
      setLoadingForm(false);
    }
  };

  const handleInativar = async (artista: Artista) => {
    if (
      confirm(
        `Confirma a inativação de "${artista.nome}"? O artista não poderá ser vinculado a novos eventos.`
      )
    ) {
      try {
        await inativarArtista(artista.id);
      } catch {
        alert("Erro ao inativar artista. Tente novamente.");
      }
    }
  };

  const handleReativar = async (artista: Artista) => {
    if (confirm(`Confirma a reativação de "${artista.nome}"?`)) {
      try {
        await reativarArtista(artista.id);
      } catch {
        alert("Erro ao reativar artista. Tente novamente.");
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
      titulo="Meus Artistas"
      subtitulo="Gerencie os artistas vinculados ao seu perfil de produtor."
      acoes={
        <Button onClick={() => setIsCadastroOpen(true)} disabled={!produtorId}>
          <Plus className="w-4 h-4 mr-2" />
          Novo Artista
        </Button>
      }
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={artistas ?? []}
        empty="Nenhum artista cadastrado. Clique em 'Novo Artista' para começar."
      />

      <Modal
        open={isCadastroOpen}
        onClose={() => setIsCadastroOpen(false)}
        title="Novo Artista"
      >
        <form onSubmit={handleCadastro} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Nome</label>
            <Input
              required
              value={formData.nome}
              onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
              placeholder="Nome do artista ou banda"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              Itens do Rider{" "}
              <span className="text-muted-foreground font-normal">
                (separados por vírgula)
              </span>
            </label>
            <Input
              value={formData.riderItens}
              onChange={(e) =>
                setFormData({ ...formData, riderItens: e.target.value })
              }
              placeholder="Ex: Microfone, Amplificador, Água mineral"
            />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsCadastroOpen(false)}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={loadingForm}>
              {loadingForm ? "Salvando..." : "Cadastrar"}
            </Button>
          </div>
        </form>
      </Modal>
    </PageLayout>
  );
}