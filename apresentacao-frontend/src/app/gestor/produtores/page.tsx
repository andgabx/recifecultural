"use client";

import { useState } from "react";
import { PageLayout } from "@/components/layout/PageLayout";
import { DataTable } from "@/components/shared/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Modal } from "@/components/shared/Modal";
import { Badge } from "@/components/ui/badge";
import {
  useProdutores,
  Produtor,
  AcaoAdministrativaPayload,
} from "@/hooks/useProdutores";
import { Ban, CheckCircle, Mic, Plus, Power, PowerOff, XCircle } from "lucide-react";
import Link from "next/link";

// ---------------------------------------------------------------------------
// Sub-componente: Modal de ação administrativa (suspender / reativar / inativar)
// ---------------------------------------------------------------------------
type TipoAcao = "suspender" | "reativar" | "inativar";

interface AcaoModalProps {
  open: boolean;
  tipo: TipoAcao | null;
  produtor: Produtor | null;
  onClose: () => void;
  onConfirm: (payload: AcaoAdministrativaPayload) => Promise<void>;
}

const TITULO_ACAO: Record<TipoAcao, string> = {
  suspender: "Suspender Produtor",
  reativar:  "Reativar Produtor",
  inativar:  "Inativar Produtor",
};

function AcaoAdministrativaModal({ open, tipo, produtor, onClose, onConfirm }: AcaoModalProps) {
  const [responsavel, setResponsavel] = useState("");
  const [motivo, setMotivo] = useState("");
  const [loading, setLoading] = useState(false);

  if (!open || !tipo || !produtor) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onConfirm({ responsavel, motivo });
      setResponsavel("");
      setMotivo("");
      onClose();
    } catch {
      alert("Erro ao executar a ação. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  const isDestructive = tipo === "suspender" || tipo === "inativar";

  return (
    <Modal open={open} onClose={onClose} title={TITULO_ACAO[tipo]}>
      <p className="text-sm text-muted-foreground mb-4">
        Produtor: <strong>{produtor.nomeFantasia}</strong>
      </p>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Responsável</label>
          <Input
            required
            value={responsavel}
            onChange={(e) => setResponsavel(e.target.value)}
            placeholder="Nome de quem está executando a ação"
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Motivo</label>
          <textarea
            className="w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
            rows={3}
            required
            value={motivo}
            onChange={(e) => setMotivo(e.target.value)}
            placeholder="Descreva o motivo da ação..."
          />
        </div>
        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={loading} variant={isDestructive ? "destructive" : "default"}>
            {loading ? "Salvando..." : "Confirmar"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

// ---------------------------------------------------------------------------
// Page principal
// ---------------------------------------------------------------------------
const STATUS_VARIANT: Record<Produtor["status"], "default" | "secondary" | "outline" | "destructive"> = {
  ATIVO:     "default",
  INATIVO:   "secondary",
  PENDENTE:  "outline",
  BLOQUEADO: "destructive",
};

export default function ProdutoresGestorPage() {
  const { produtores, isLoading, createProdutor, suspenderProdutor, reativarProdutor, inativarProdutor } =
    useProdutores();

  const [isCadastroOpen, setIsCadastroOpen] = useState(false);
  const [acaoModal, setAcaoModal] = useState<{ tipo: TipoAcao; produtor: Produtor } | null>(null);
  const [loadingForm, setLoadingForm] = useState(false);

  const [formData, setFormData] = useState({
    nomeFantasia: "",
    cnpj: "",
    email: "",
    telefone: "",
  });

  const handleCadastro = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoadingForm(true);
    try {
      await createProdutor(formData);
      setFormData({ nomeFantasia: "", cnpj: "", email: "", telefone: "" });
      setIsCadastroOpen(false);
    } catch {
      alert("Erro ao cadastrar produtor. Verifique os dados e tente novamente.");
    } finally {
      setLoadingForm(false);
    }
  };

  const handleAcaoConfirm = async (payload: AcaoAdministrativaPayload) => {
    if (!acaoModal) return;
    const { tipo, produtor } = acaoModal;
    if (tipo === "suspender") await suspenderProdutor({ id: produtor.id, ...payload });
    if (tipo === "reativar")  await reativarProdutor({ id: produtor.id, ...payload });
    if (tipo === "inativar")  await inativarProdutor({ id: produtor.id, ...payload });
    setAcaoModal(null);
  };

  const columns = [
    {
      header: "Nome Fantasia",
      accessor: "nomeFantasia" as keyof Produtor,
      cell: (row: Produtor) => <span className="font-medium">{row.nomeFantasia}</span>,
    },
    {
      header: "CNPJ",
      accessor: "cnpj" as keyof Produtor,
      cell: (row: Produtor) => <span className="font-mono text-sm">{row.cnpj}</span>,
    },
    {
      header: "Email",
      accessor: "email" as keyof Produtor,
      cell: (row: Produtor) => <span>{row.email}</span>,
    },
    {
      header: "Status",
      accessor: "status" as keyof Produtor,
      cell: (row: Produtor) => (
        <Badge variant={STATUS_VARIANT[row.status]}>{row.status}</Badge>
      ),
    },
    {
      header: "Ações",
      accessor: "id" as keyof Produtor,
      cell: (row: Produtor) => (
        <div className="flex gap-2 flex-wrap items-center">
          {/* Drill-down: artistas deste produtor */}
          <Link href={`/gestor/produtores/${row.id}/artistas`}>
            <Button variant="outline" size="sm" title="Ver artistas">
              <Mic className="w-4 h-4" />
            </Button>
          </Link>

          {/* PENDENTE: aprovar ou rejeitar */}
          {row.status === "PENDENTE" && (
            <>
              <Button
                variant="outline"
                size="sm"
                title="Aprovar"
                onClick={() => setAcaoModal({ tipo: "reativar", produtor: row })}
              >
                <CheckCircle className="w-4 h-4 text-green-500" />
              </Button>
              <Button
                variant="outline"
                size="sm"
                title="Rejeitar"
                onClick={() => setAcaoModal({ tipo: "suspender", produtor: row })}
              >
                <XCircle className="w-4 h-4 text-red-500" />
              </Button>
            </>
          )}

          {/* ATIVO: suspender */}
          {row.status === "ATIVO" && (
            <Button
              variant="outline"
              size="sm"
              title="Suspender"
              onClick={() => setAcaoModal({ tipo: "suspender", produtor: row })}
            >
              <PowerOff className="w-4 h-4 text-orange-500" />
            </Button>
          )}

          {/* BLOQUEADO ou INATIVO: reativar */}
          {(row.status === "BLOQUEADO" || row.status === "INATIVO") && (
            <Button
              variant="outline"
              size="sm"
              title="Reativar"
              onClick={() => setAcaoModal({ tipo: "reativar", produtor: row })}
            >
              <Power className="w-4 h-4 text-green-500" />
            </Button>
          )}

          {/* Inativar permanentemente (exceto quem já está INATIVO) */}
          {row.status !== "INATIVO" && (
            <Button
              variant="destructive"
              size="sm"
              title="Inativar permanentemente"
              onClick={() => setAcaoModal({ tipo: "inativar", produtor: row })}
            >
              <Ban className="w-4 h-4" />
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <PageLayout
      titulo="Gestão de Produtores"
      subtitulo="Aprove, suspenda e gerencie os produtores culturais da plataforma."
      acoes={
        <Button onClick={() => setIsCadastroOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Novo Produtor
        </Button>
      }
    >
      <DataTable
        columns={columns}
        rowKey={(row) => row.id}
        data={produtores ?? []}
        empty="Nenhum produtor encontrado."
      />

      {/* Modal: Cadastro */}
      <Modal open={isCadastroOpen} onClose={() => setIsCadastroOpen(false)} title="Novo Produtor">
        <form onSubmit={handleCadastro} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Nome Fantasia</label>
            <Input
              required
              value={formData.nomeFantasia}
              onChange={(e) => setFormData({ ...formData, nomeFantasia: e.target.value })}
              placeholder="Nome do produtor cultural"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">CNPJ</label>
            <Input
              required
              value={formData.cnpj}
              onChange={(e) => setFormData({ ...formData, cnpj: e.target.value })}
              placeholder="Apenas números"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <Input
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              placeholder="contato@produtor.com"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Telefone</label>
            <Input
              required
              value={formData.telefone}
              onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
              placeholder="(81) 99999-9999"
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

      {/* Modal: Ação Administrativa */}
      <AcaoAdministrativaModal
        open={!!acaoModal}
        tipo={acaoModal?.tipo ?? null}
        produtor={acaoModal?.produtor ?? null}
        onClose={() => setAcaoModal(null)}
        onConfirm={handleAcaoConfirm}
      />
    </PageLayout>
  );
}