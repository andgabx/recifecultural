"use client";

import { useState } from "react";
import { PageLayout } from "../../../components/layout/PageLayout";
import { DataTable } from "../../../components/shared/DataTable";
import { Button } from "../../../components/ui/button";
import { Input } from "../../../components/ui/input";
import { Modal } from "../../../components/shared/Modal";
import { Badge } from "../../../components/ui/badge";
import { useArtistas, Artista } from "../../../hooks/useArtistas";
import { Edit, Trash2, Power, PowerOff, Plus } from "lucide-react";

export default function ArtistasProdutorPage() {
  const { artistas, isLoading, createArtista, updateArtista, toggleStatus, deleteArtista } = useArtistas();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  
  const [formData, setFormData] = useState({
    nome: "",
    biografia: "",
    cpfCnpj: "",
  });

  const handleOpenNew = () => {
    setEditingId(null);
    setFormData({ nome: "", biografia: "", cpfCnpj: "" });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (artista: Artista) => {
    setEditingId(artista.id);
    setFormData({
      nome: artista.nome,
      biografia: artista.biografia,
      cpfCnpj: artista.cpfCnpj,
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        await updateArtista({ id: editingId, ...formData });
      } else {
        await createArtista(formData);
      }
      setIsModalOpen(false);
    } catch (error) {
      console.error("Erro ao salvar artista:", error);
      alert("Ocorreu um erro ao salvar o artista.");
    }
  };

  const handleToggleStatus = async (id: string, currentStatus: string) => {
    if (confirm(`Deseja realmente ${currentStatus === 'ATIVO' ? 'desativar' : 'reativar'} este artista?`)) {
      try {
        const newStatus = currentStatus === 'ATIVO' ? 'INATIVO' : 'ATIVO';
        await toggleStatus({ id, status: newStatus });
      } catch (error) {
        console.error("Erro ao alterar status:", error);
      }
    }
  };

  const handleDelete = async (id: string) => {
    if (confirm("Atenção: Deseja realmente excluir este artista permanentemente?")) {
      try {
        await deleteArtista(id);
      } catch (error) {
        console.error("Erro ao excluir artista:", error);
      }
    }
  };

  const columns = [
    { 
      header: "Nome", 
      accessor: "nome" as keyof Artista,
      cell: (row: Artista) => <span>{row.nome}</span> 
    },
    { 
      header: "CPF/CNPJ", 
      accessor: "cpfCnpj" as keyof Artista,
      cell: (row: Artista) => <span>{row.cpfCnpj}</span>
    },
    {
      header: "Status",
      accessor: "status" as keyof Artista,
      cell: (row: Artista) => (
        <Badge variant={row.status === "ATIVO" ? "default" : "destructive"}>
          {row.status}
        </Badge>
      ),
    },
    {
      header: "Ações",
      accessor: "id" as keyof Artista,
      cell: (row: Artista) => (
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={() => handleOpenEdit(row)} title="Editar">
            <Edit className="w-4 h-4" />
          </Button>
          <Button 
            variant="outline" 
            size="sm" 
            onClick={() => handleToggleStatus(row.id, row.status)}
            title={row.status === 'ATIVO' ? "Desativar" : "Reativar"}
          >
            {row.status === 'ATIVO' ? <PowerOff className="w-4 h-4 text-orange-500" /> : <Power className="w-4 h-4 text-green-500" />}
          </Button>
          <Button variant="destructive" size="sm" onClick={() => handleDelete(row.id)} title="Excluir">
            <Trash2 className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Meus Artistas"
      subtitulo="Gerencie os artistas vinculados aos seus eventos"
      acoes={
        <Button onClick={handleOpenNew}>
          <Plus className="w-4 h-4 mr-2" />
          Novo Artista
        </Button>
      }
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={artistas || []}
        empty="Nenhum artista cadastrado."
      />

      <Modal
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingId ? "Editar Artista" : "Novo Artista"}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
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
            <label className="block text-sm font-medium mb-1">CPF ou CNPJ</label>
            <Input
              required
              value={formData.cpfCnpj}
              onChange={(e) => setFormData({ ...formData, cpfCnpj: e.target.value })}
              placeholder="Apenas números"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Biografia</label>
            <textarea
              className="w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
              rows={4}
              required
              value={formData.biografia}
              onChange={(e) => setFormData({ ...formData, biografia: e.target.value })}
              placeholder="Breve descrição do artista..."
            />
          </div>
          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit">Salvar</Button>
          </div>
        </form>
      </Modal>
    </PageLayout>
  );
}