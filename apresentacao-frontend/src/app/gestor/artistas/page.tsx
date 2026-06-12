"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Drama, Plus, Trash2 } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorProdutor } from "@/components/form/Seletores";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useArtistas,
  useCadastrarArtista,
  useInativarArtista,
} from "@/hooks/useArtistas";
import type { ApiError } from "@/lib/api";
import type { ArtistaResumo } from "@/services/bff/artistas";
import type { StatusArtista } from "@/types/dominio";

const cadastroSchema = z.object({
  produtorId: z.string().uuid("Id de produtor inválido"),
  nome: z.string().min(2, "Informe o nome do artista"),
});
type CadastroForm = z.infer<typeof cadastroSchema>;

const statusVariant: Record<StatusArtista, "success" | "destructive"> = {
  ATIVO: "success",
  INATIVO: "destructive",
};

export default function ArtistasPage() {
  const { data, isLoading, isError } = useArtistas();
  const [cadastroAberto, setCadastroAberto] = useState(false);

  const cadastrar = useCadastrarArtista();
  const inativar = useInativarArtista();

  const form = useForm<CadastroForm>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: { produtorId: "", nome: "" },
  });

  async function onSubmit(values: CadastroForm) {
    try {
      await cadastrar.mutateAsync(values);
      toast.success("Artista cadastrado");
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function handleInativar(artista: ArtistaResumo) {
    try {
      await inativar.mutateAsync(artista.id);
      toast.success(`"${artista.nome}" inativado`);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const colunas: Coluna<ArtistaResumo>[] = [
    {
      header: "Nome artístico",
      cell: (a) => <span className="font-medium">{a.nome}</span>,
    },
    {
      header: "Produtor",
      cell: (a) => (
        <span className="font-mono text-xs">{a.produtorId.slice(0, 8)}…</span>
      ),
    },
    {
      header: "Status",
      cell: (a) => <Badge variant={statusVariant[a.status]}>{a.status}</Badge>,
    },
    {
      header: "",
      width: "1%",
      cell: (a) =>
        a.status === "ATIVO" ? (
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => handleInativar(a)}
            aria-label="Inativar"
            className="text-destructive hover:bg-destructive/10"
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
        ) : null,
    },
  ];

  return (
    <PageLayout
      titulo="Artistas"
      subtitulo="Artistas vinculados a produtores cadastrados."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          className="bg-azul hover:bg-azul-light text-nevoa"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo artista
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={Drama}
          title="Falha ao carregar artistas"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && (
        <DataTable
          data={data}
          rowKey={(a) => a.id}
          columns={colunas}
          empty={
            <EmptyState
              icon={Drama}
              title="Nenhum artista cadastrado"
              description="Cadastre o primeiro artista vinculando-o a um produtor."
            />
          }
        />
      )}

      <Modal
        open={cadastroAberto}
        onClose={() => {
          form.reset();
          setCadastroAberto(false);
        }}
        title="Novo artista"
        description="Vincule o artista a um produtor existente (use o id do produtor)."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                form.reset();
                setCadastroAberto(false);
              }}
              disabled={cadastrar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={form.handleSubmit(onSubmit)}
              disabled={cadastrar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {cadastrar.isPending && <LoadingSpinner className="mr-2 text-nevoa" />}
              Cadastrar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Produtor"
            htmlFor="produtorId"
            hint="Produtor responsável pelo artista"
            error={form.formState.errors.produtorId?.message}
            required
          >
            <SeletorProdutor
              id="produtorId"
              value={form.watch("produtorId") ?? ""}
              onChange={(v) => form.setValue("produtorId", v)}
            />
          </FormField>
          <FormField
            label="Nome artístico"
            htmlFor="nome"
            error={form.formState.errors.nome?.message}
            required
          >
            <Input id="nome" {...form.register("nome")} />
          </FormField>
        </form>
      </Modal>
    </PageLayout>
  );
}
