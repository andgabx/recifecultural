"use client";

import { useState } from "react";
import { PageLayout } from "../../../components/layout/PageLayout";
import { DataTable } from "../../../components/shared/DataTable";
import { Button } from "../../../components/ui/button";
import { Input } from "../../../components/ui/input";
import { Modal } from "../../../components/shared/Modal";
import { Badge } from "../../../components/ui/badge";
import { useProdutores, Produtor } from "../../../hooks/useProdutores";
import { Power, PowerOff, Trash2, CheckCircle, XCircle } from "lucide-react";

export default function ProdutoresGestorPage() {
  const { produtores, isLoading, toggleStatus, deleteProdutor } = useProdutores();

  const handleToggleStatus = async (produtor: Produtor) => {
    if (confirm(`Deseja realmente alterar o status deste produtor?`)) {
      try {
        const newStatus = produtor.status === 'ATIVO' ? 'INATIVO' : 'ATIVO';
        await toggleStatus({ produtor, status: newStatus });
      } catch (error) {
        console.error("Erro ao alterar status:", error);
        alert("Falha ao atualizar o status do produtor.");
      }
    }
  };
  
  const handleApprove = async (produtor: Produtor, approve: boolean) => {
    const newStatus = approve ? 'ATIVO' : 'BLOQUEADO';
    try {
      await toggleStatus({ produtor, status: newStatus });
    } catch (error) {
      console.error("Erro ao avaliar produtor:", error);
      alert("Falha ao avaliar o produtor.");
    }
  }

  const handleDelete = async (id: string) => {
    if (confirm("Atenção: Deseja realmente excluir este produtor permanentemente do sistema?")) {
      try {
        await deleteProdutor(id);
      } catch (error) {
        console.error("Erro ao excluir produtor:", error);
      }
    }
  };

  const columns = [
    { 
      header: "Nome", 
      accessor: "nome" as keyof Produtor,
      cell: (row: Produtor) => <span>{row.nome}</span>
    },
    { 
      header: "CNPJ", 
      accessor: "cnpj" as keyof Produtor,
      cell: (row: Produtor) => <span>{row.cnpj}</span>
    },
    { 
      header: "Email", 
      accessor: "email" as keyof Produtor,
      cell: (row: Produtor) => <span>{row.email}</span>
    },
    {
      header: "Status",
      accessor: "status" as keyof Produtor,
      cell: (row: Produtor) => {
        const variants: Record<string, string> = {
          ATIVO: "default",
          INATIVO: "secondary",
          PENDENTE: "outline",
          BLOQUEADO: "destructive"
        };
        return <Badge variant={variants[row.status] as any || "default"}>{row.status}</Badge>;
      },
    },
    {
      header: "Ações",
      accessor: "id" as keyof Produtor,
      cell: (row: Produtor) => (
        <div className="flex gap-2">
          {row.status === 'PENDENTE' && (
            <>
              <Button variant="outline" size="sm" onClick={() => handleApprove(row, true)} title="Aprovar">
                <CheckCircle className="w-4 h-4 text-green-500" />
              </Button>
              <Button variant="outline" size="sm" onClick={() => handleApprove(row, false)} title="Rejeitar">
                <XCircle className="w-4 h-4 text-red-500" />
              </Button>
            </>
          )}

          {row.status !== 'PENDENTE' && (
            <Button 
              variant="outline" 
              size="sm" 
              onClick={() => handleToggleStatus(row)}
              title={row.status === 'ATIVO' ? "Desativar" : "Reativar"}
            >
              {row.status === 'ATIVO' ? <PowerOff className="w-4 h-4 text-orange-500" /> : <Power className="w-4 h-4 text-green-500" />}
            </Button>
          )}

          <Button variant="destructive" size="sm" onClick={() => handleDelete(row.id)} title="Excluir">
            <Trash2 className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Gestão de Produtores"
      subtitulo="Aprove, bloqueie e gerencie os produtores culturais da plataforma."
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={produtores || []}
        empty="Nenhum produtor encontrado."
      />
    </PageLayout>
  );
}