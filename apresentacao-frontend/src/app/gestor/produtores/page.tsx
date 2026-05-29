"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Pause, Play, Plus, Trash2, UserPlus } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { DataTable, type Coluna } from "@/components/shared/DataTable";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useCadastrarProdutor,
  useInativarProdutor,
  useProdutores,
  useReativarProdutor,
  useSuspenderProdutor,
} from "@/hooks/useProdutores";
import type { ApiError } from "@/lib/api";
import type { ProdutorResumo } from "@/services/bff/produtores";
import type { StatusProdutor } from "@/types/dominio";

const cadastroSchema = z.object({
  nomeFantasia: z.string().min(2, "Informe o nome fantasia"),
  cnpj: z
    .string()
    .regex(/^\d{14}$/, "CNPJ deve ter 14 dígitos sem formatação"),
  email: z.string().email("E-mail inválido"),
  telefone: z.string().optional().or(z.literal("")),
});
type CadastroForm = z.infer<typeof cadastroSchema>;

const statusVariant: Record<StatusProdutor, "success" | "frevo" | "destructive"> = {
  ATIVO: "success",
  SUSPENSO: "frevo",
  INATIVO: "destructive",
};

export default function ProdutoresPage() {
  const { data, isLoading, isError } = useProdutores();
  const [cadastroAberto, setCadastroAberto] = useState(false);

  const cadastrar = useCadastrarProdutor();
  const suspender = useSuspenderProdutor();
  const reativar = useReativarProdutor();
  const inativar = useInativarProdutor();

  const form = useForm<CadastroForm>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: { nomeFantasia: "", cnpj: "", email: "", telefone: "" },
  });

  async function onSubmit(values: CadastroForm) {
    try {
      await cadastrar.mutateAsync({
        nomeFantasia: values.nomeFantasia,
        cnpj: values.cnpj,
        email: values.email,
        telefone: values.telefone || undefined,
      });
      toast.success("Produtor cadastrado");
      form.reset();
      setCadastroAberto(false);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  const colunas: Coluna<ProdutorResumo>[] = [
    {
      header: "Nome fantasia",
      cell: (p) => <span className="font-medium">{p.nomeFantasia}</span>,
    },
    {
      header: "CNPJ",
      cell: (p) => (
        <span className="font-mono text-xs">{p.cnpj ?? "—"}</span>
      ),
    },
    {
      header: "Contato",
      cell: (p) => (
        <div className="text-xs">
          <p>{p.email}</p>
          {p.telefone && (
            <p className="text-muted-foreground">{p.telefone}</p>
          )}
        </div>
      ),
    },
    {
      header: "Status",
      cell: (p) => (
        <Badge variant={statusVariant[p.status]}>{p.status}</Badge>
      ),
    },
    {
      header: "",
      width: "1%",
      cell: (p) => <AcoesLinha produtor={p} />,
    },
  ];

  function AcoesLinha({ produtor }: { produtor: ProdutorResumo }) {
    async function handle(
      acao: "suspender" | "reativar" | "inativar",
    ) {
      const map = { suspender, reativar, inativar };
      try {
        await map[acao].mutateAsync(produtor.id);
        toast.success(`Produtor ${acao}do`);
      } catch (error) {
        toast.error((error as ApiError).message);
      }
    }
    return (
      <div className="flex items-center gap-1">
        {produtor.status === "ATIVO" && (
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => handle("suspender")}
            aria-label="Suspender"
          >
            <Pause className="h-3.5 w-3.5" />
          </Button>
        )}
        {produtor.status === "SUSPENSO" && (
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => handle("reativar")}
            aria-label="Reativar"
          >
            <Play className="h-3.5 w-3.5" />
          </Button>
        )}
        {produtor.status !== "INATIVO" && (
          <Button
            size="icon-sm"
            variant="ghost"
            onClick={() => handle("inativar")}
            aria-label="Inativar"
            className="text-destructive hover:bg-destructive/10"
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
        )}
      </div>
    );
  }

  return (
    <PageLayout
      titulo="Produtores"
      subtitulo="Cadastro, suspensão e inativação de produtores culturais."
      acoes={
        <Button
          onClick={() => setCadastroAberto(true)}
          className="bg-vinho hover:bg-vinho-light text-marquee"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo produtor
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-2">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={UserPlus}
          title="Falha ao carregar produtores"
          description="Verifique se o backend está rodando."
        />
      )}

      {data && (
        <DataTable
          data={data}
          rowKey={(p) => p.id}
          columns={colunas}
          empty={
            <EmptyState
              icon={UserPlus}
              title="Nenhum produtor cadastrado"
              description="Cadastre o primeiro produtor para liberar a criação de eventos."
              action={
                <Button
                  onClick={() => setCadastroAberto(true)}
                  variant="outline"
                >
                  Novo produtor
                </Button>
              }
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
        title="Novo produtor"
        description="O CNPJ é validado pelos dígitos verificadores."
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
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {cadastrar.isPending && <LoadingSpinner className="mr-2 text-marquee" />}
              Cadastrar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Nome fantasia"
            htmlFor="nomeFantasia"
            error={form.formState.errors.nomeFantasia?.message}
            required
          >
            <Input id="nomeFantasia" {...form.register("nomeFantasia")} />
          </FormField>
          <FormField
            label="CNPJ (somente dígitos)"
            htmlFor="cnpj"
            error={form.formState.errors.cnpj?.message}
            required
          >
            <Input
              id="cnpj"
              placeholder="00000000000000"
              {...form.register("cnpj")}
            />
          </FormField>
          <FormField
            label="E-mail"
            htmlFor="email"
            error={form.formState.errors.email?.message}
            required
          >
            <Input id="email" type="email" {...form.register("email")} />
          </FormField>
          <FormField label="Telefone" htmlFor="telefone">
            <Input
              id="telefone"
              placeholder="(81) 99999-9999"
              {...form.register("telefone")}
            />
          </FormField>
        </form>
      </Modal>
    </PageLayout>
  );
}
