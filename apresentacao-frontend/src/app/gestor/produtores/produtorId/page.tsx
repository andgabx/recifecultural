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
import { useProdutores } from "@/hooks/useProdutores";
import { ArrowLeft, Ban, Plus, Power, RefreshCw } from "lucide-react";
import Link from "next/link";

// ---------------------------------------------------------------------------
// Page: /gestor/produtores/[produtorId]/artistas
// Gestor visualiza e gerencia artistas de um produtor específico.
// Pode cadastrar, inativar, reativar e transferir artistas entre produtores.
// ---------------------------------------------------------------------------
export default function ArtistasDoProducerGestorPage() {
  const params = useParams();
  const produtorId = params?.produtorId as string;

  const { artistas, isLoading, createArtista, inativarArtista, reativarArtista } =
    useArtistas(produtorId);
  const { produtores } = useProdutores();

  const produtor = produtores?.find((p) => p.id === produtorId);
  const outrosProdutoresAtivos = produtores?.filter(
    (p) => p.id !== produtorId && p.status === "ATIVO"
  ) ?? [];

  // Modais
  const [isCadastroOpen, setIsCadastroOpen] = useState(false);
  const [artistaParaTransferir, setArtistaParaTransferir] = useState<Artista | null>(null);
  const [loadingForm, setLoadingForm] = useState(false);

  // Formulário de cadastro
  const [formData, setFormData] = useState({ nome: "", riderItens: "" });

  // Formulário de transferência
  const [novoProdutorId, setNovoProdutorId] = useState("");

  // -------------------------------------------------------------------------
  const handleCadastro = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoadingForm(true);
    try {
      await createArtista({
        produtorId,
        nome: formData.nome,
        riderItens: formData.riderItens.split(",").map((s) => s.trim()).filter(Boolean),
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

  // Transferência: inativa vínculo atual → recadastra no novo produtor.
  // Estratégia necessária pois o backend não expõe PATCH de produtorId.
  const handleTransferir = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!artistaParaTransferir || !novoProdutorId) return;
    setLoadingForm(true);
    try {
      await inativarArtista(artistaParaTransferir.id);
      await createArtista({
        produtorId: novoProdutorId,
        nome: artistaParaTransferir.nome,
        riderItens: [],
      });
      setArtistaParaTransferir(null);
      setNovoProdutorId("");
    } catch {
      alert("Erro ao transferir artista. Tente novamente.");
    } finally {
      setLoadingForm(false);
    }
  };

  // -------------------------------------------------------------------------
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
            <>
              {/* Transferir para outro produtor */}
              <Button
                variant="outline"
                size="sm"
                title="Transferir para outro produtor"
                onClick={() => {
                  setArtistaParaTransferir(row);
                  setNovoProdutorId("");
                }}
              >
                <RefreshCw className="w-4 h-4" />
              </Button>
              {/* Inativar */}
              <Button
                variant="destructive"
                size="sm"
                title="Inativar artista"
                onClick={() => handleInativar(row)}
              >
                <Ban className="w-4 h-4" />
              </Button>
            </>
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
      titulo={`Artistas — ${produtor?.nomeFantasia ?? "Produtor"}`}
      subtitulo="Gerencie os artistas vinculados a este produtor. Você pode cadastrar, inativar, reativar ou transferir."
      acoes={
        <div className="flex gap-2">
          <Link href="/gestor/produtores">
            <Button variant="outline">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Produtores
            </Button>
          </Link>
          <Button onClick={() => setIsCadastroOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Novo Artista
          </Button>
        </div>
      }
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={artistas ?? []}
        empty="Nenhum artista vinculado a este produtor."
      />

      {/* Modal: Cadastro */}
      <Modal open={isCadastroOpen} onClose={() => setIsCadastroOpen(false)} title="Novo Artista">
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
              <span className="text-muted-foreground font-normal">(separados por vírgula)</span>
            </label>
            <Input
              value={formData.riderItens}
              onChange={(e) => setFormData({ ...formData, riderItens: e.target.value })}
              placeholder="Ex: Microfone, Amplificador, Água mineral"
            />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="outline" onClick={() => setIsCadastroOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit" disabled={loadingForm}>
              {loadingForm ? "Salvando..." : "Cadastrar"}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Transferência de produtor */}
      <Modal
        open={!!artistaParaTransferir}
        onClose={() => setArtistaParaTransferir(null)}
        title="Transferir Artista"
      >
        {artistaParaTransferir && (
          <form onSubmit={handleTransferir} className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Artista: <strong>{artistaParaTransferir.nome}</strong>
              <br />
              O vínculo atual será inativado e um novo vínculo será criado com o
              produtor selecionado.
            </p>
            <div>
              <label className="block text-sm font-medium mb-1">Novo Produtor</label>
              <select
                required
                className="w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                value={novoProdutorId}
                onChange={(e) => setNovoProdutorId(e.target.value)}
              >
                <option value="">Selecione um produtor...</option>
                {outrosProdutoresAtivos.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.nomeFantasia}
                  </option>
                ))}
              </select>
              {outrosProdutoresAtivos.length === 0 && (
                <p className="text-xs text-muted-foreground mt-1">
                  Não há outros produtores ativos disponíveis.
                </p>
              )}
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setArtistaParaTransferir(null)}>
                Cancelar
              </Button>
              <Button type="submit" disabled={loadingForm || !novoProdutorId}>
                {loadingForm ? "Transferindo..." : "Confirmar Transferência"}
              </Button>
            </div>
          </form>
        )}
      </Modal>
    </PageLayout>
  );
}