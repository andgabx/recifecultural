"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Armchair, Pencil, Plus } from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorEspaco } from "@/components/form/Seletores";
import { AssentoGrid } from "@/components/domain/AssentoGrid";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useCapacidadeEspaco,
  useConfigurarSetor,
  useEditarSetor,
  useSetoresPorEspaco,
} from "@/hooks/useSetores";
import type { ApiError } from "@/lib/api";
import type { SetorComAssentos } from "@/services/bff/setores";

const TIPOS_SETOR = [
  { value: "PLATEIA", label: "Plateia" },
  { value: "BALCAO", label: "Balcão" },
  { value: "CAMAROTE", label: "Camarote" },
  { value: "ARENA", label: "Arena" },
] as const;

const novoSchema = z.object({
  espacoId: z.string().uuid("Selecione um espaço"),
  nome: z.string().min(2, "Nome muito curto"),
  tipoSetor: z.enum(["PLATEIA", "BALCAO", "CAMAROTE", "ARENA"]),
  fileirasHorizontais: z.coerce
    .number()
    .int()
    .min(1, "Mínimo 1 fileira")
    .max(26, "Máximo 26 fileiras (A–Z)"),
  assentosPorFileiraVertical: z.coerce
    .number()
    .int()
    .positive("Deve ser positivo"),
});
type NovoFormInput = z.input<typeof novoSchema>;
type NovoFormOutput = z.output<typeof novoSchema>;

const editarSchema = novoSchema.omit({ espacoId: true });
type EditarFormInput = z.input<typeof editarSchema>;
type EditarFormOutput = z.output<typeof editarSchema>;

export default function SetoresPage() {
  const [espacoId, setEspacoId] = useState("");
  const [novoAberto, setNovoAberto] = useState(false);
  const [editandoSetor, setEditandoSetor] = useState<SetorComAssentos | null>(null);

  const { data: setores, isLoading } = useSetoresPorEspaco(espacoId || undefined);
  const { data: capacidade } = useCapacidadeEspaco(espacoId || undefined);
  const configurar = useConfigurarSetor(espacoId || undefined);
  const editar = useEditarSetor(espacoId || undefined);

  const totalAssentos = setores?.reduce((acc, s) => acc + s.assentos.length, 0) ?? 0;

  const novoForm = useForm<NovoFormInput, unknown, NovoFormOutput>({
    resolver: zodResolver(novoSchema),
    defaultValues: {
      espacoId: "",
      nome: "",
      tipoSetor: "PLATEIA",
      fileirasHorizontais: 10,
      assentosPorFileiraVertical: 12,
    },
  });

  const editForm = useForm<EditarFormInput, unknown, EditarFormOutput>({
    resolver: zodResolver(editarSchema),
    defaultValues: {
      nome: "",
      tipoSetor: "PLATEIA",
      fileirasHorizontais: 10,
      assentosPorFileiraVertical: 12,
    },
  });

  // Pré-preenche o form de edição quando o setor é selecionado
  useEffect(() => {
    if (!editandoSetor) return;
    const tipoValido = TIPOS_SETOR.find((t) => t.value === editandoSetor.tipoSetor);
    editForm.reset({
      nome: editandoSetor.nome,
      tipoSetor: (tipoValido?.value ?? "PLATEIA") as EditarFormInput["tipoSetor"],
      fileirasHorizontais: editandoSetor.fileirasHorizontais,
      assentosPorFileiraVertical: editandoSetor.assentosPorFileiraVertical,
    });
  }, [editandoSetor, editForm]);

  async function onCriar(values: NovoFormOutput) {
    try {
      await configurar.mutateAsync(values);
      toast.success(
        `Setor "${values.nome}" configurado com ${
          values.fileirasHorizontais * values.assentosPorFileiraVertical
        } assentos.`,
      );
      novoForm.reset({ ...novoForm.getValues(), nome: "" });
      setNovoAberto(false);
      if (!espacoId) setEspacoId(values.espacoId);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onEditar(values: EditarFormOutput) {
    if (!editandoSetor) return;
    try {
      await editar.mutateAsync({ id: editandoSetor.id, payload: values });
      toast.success(`Setor "${values.nome}" atualizado.`);
      setEditandoSetor(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Setores e cadeiras"
      subtitulo="Mapa visual da plateia. Cores indicam status de cada cadeira."
      acoes={
        <Button
          onClick={() => {
            if (espacoId) novoForm.setValue("espacoId", espacoId);
            setNovoAberto(true);
          }}
          className="bg-azul hover:bg-azul-light text-nevoa"
        >
          <Plus className="mr-1 h-4 w-4" />
          Novo setor
        </Button>
      }
    >
      <Card className="space-y-3 p-5">
        <FormField
          label="Espaço"
          htmlFor="espacoIdConsulta"
          hint="Selecione o espaço para visualizar seus setores."
        >
          <SeletorEspaco
            id="espacoIdConsulta"
            value={espacoId}
            onChange={setEspacoId}
          />
        </FormField>
        {capacidade && (
          <div className="flex items-center gap-2">
            <Badge variant="success">
              {capacidade.assentosDisponiveis} disponíveis
            </Badge>
            {setores && (
              <Badge variant="secondary">
                {totalAssentos} cadastrados
              </Badge>
            )}
          </div>
        )}
      </Card>

      {isLoading && espacoId && (
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 2 }).map((_, i) => (
            <Skeleton key={i} className="h-80 w-full" />
          ))}
        </div>
      )}

      {setores && setores.length === 0 && espacoId && (
        <EmptyState
          icon={Armchair}
          title="Espaço sem setores configurados"
          description="Configure o primeiro setor para gerar a planta de cadeiras."
          action={
            <Button
              variant="outline"
              onClick={() => {
                novoForm.setValue("espacoId", espacoId);
                setNovoAberto(true);
              }}
            >
              Configurar primeiro setor
            </Button>
          }
        />
      )}

      {setores && setores.length > 0 && (
        <div className="space-y-6">
          {setores.map((setor) => (
            <Card key={setor.id} className="overflow-hidden p-0">
              <div className="border-border flex items-center justify-between gap-2 border-b px-6 py-4">
                <div>
                  <h2 className="font-display text-noite text-lg font-semibold">
                    {setor.nome}
                  </h2>
                  <p className="text-muted-foreground text-xs">
                    {setor.tipoSetor} ·{" "}
                    <span className="font-mono">
                      {setor.fileirasHorizontais}×{setor.assentosPorFileiraVertical}
                    </span>
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <Badge variant="accent">
                    {setor.assentos.filter((a) => a.status === "LIVRE").length}/
                    {setor.assentos.length} livres
                  </Badge>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setEditandoSetor(setor)}
                  >
                    <Pencil className="mr-1 h-3 w-3" />
                    Editar
                  </Button>
                </div>
              </div>
              <div className="p-6">
                <AssentoGrid
                  assentos={setor.assentos}
                  fileirasHorizontais={setor.fileirasHorizontais}
                  assentosPorFileiraVertical={setor.assentosPorFileiraVertical}
                />
              </div>
            </Card>
          ))}
        </div>
      )}

      {!espacoId && (
        <Card className="border-dashed border-laranja/30 bg-laranja/5 p-6">
          <p className="text-muted-foreground text-sm">
            Selecione um espaço acima para ver o mapa de cadeiras. A soma de
            todos os setores não pode ultrapassar a{" "}
            <strong className="text-noite">capacidade máxima</strong>{" "}
            cadastrada para o espaço — o backend valida isso na criação e
            edição. Setores podem ser editados livremente em nome/tipo;
            mudança de dimensões só é permitida se nenhum assento estiver
            ocupado ou pré-reservado.
          </p>
        </Card>
      )}

      <Modal
        open={novoAberto}
        onClose={() => {
          novoForm.reset();
          setNovoAberto(false);
        }}
        title="Configurar novo setor"
        description="A matriz fileiras × colunas gera os assentos automaticamente."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                novoForm.reset();
                setNovoAberto(false);
              }}
              disabled={configurar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={novoForm.handleSubmit(onCriar)}
              disabled={configurar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {configurar.isPending && (
                <LoadingSpinner className="mr-2 text-nevoa" />
              )}
              Configurar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Espaço"
            htmlFor="espacoIdNovo"
            error={novoForm.formState.errors.espacoId?.message}
            required
          >
            <SeletorEspaco
              id="espacoIdNovo"
              value={novoForm.watch("espacoId") ?? ""}
              onChange={(v) => novoForm.setValue("espacoId", v)}
            />
          </FormField>
          <FormField
            label="Nome do setor"
            htmlFor="nome"
            hint="Ex: Plateia A, Balcão Esquerdo, Camarote VIP"
            error={novoForm.formState.errors.nome?.message}
            required
          >
            <Input id="nome" {...novoForm.register("nome")} />
          </FormField>
          <FormField label="Tipo" htmlFor="tipoSetor" required>
            <Select id="tipoSetor" {...novoForm.register("tipoSetor")}>
              {TIPOS_SETOR.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </Select>
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Fileiras (1–26)"
              htmlFor="fileirasHorizontais"
              hint="Cada fileira recebe uma letra A–Z."
              error={novoForm.formState.errors.fileirasHorizontais?.message}
              required
            >
              <Input
                id="fileirasHorizontais"
                type="number"
                min={1}
                max={26}
                {...novoForm.register("fileirasHorizontais")}
              />
            </FormField>
            <FormField
              label="Assentos por fileira"
              htmlFor="assentosPorFileiraVertical"
              error={novoForm.formState.errors.assentosPorFileiraVertical?.message}
              required
            >
              <Input
                id="assentosPorFileiraVertical"
                type="number"
                min={1}
                {...novoForm.register("assentosPorFileiraVertical")}
              />
            </FormField>
          </div>
          <Card className="bg-nevoa-muted border-laranja/30 p-3 text-xs">
            <p className="text-muted-foreground">
              Total de assentos:{" "}
              <strong className="text-noite font-mono">
                {(Number(novoForm.watch("fileirasHorizontais")) || 0) *
                  (Number(novoForm.watch("assentosPorFileiraVertical")) || 0)}
              </strong>
            </p>
          </Card>
        </form>
      </Modal>

      <Modal
        open={!!editandoSetor}
        onClose={() => {
          editForm.reset();
          setEditandoSetor(null);
        }}
        title={`Editar setor: ${editandoSetor?.nome ?? ""}`}
        description="Mudança de dimensões só é permitida se não houver assentos ocupados ou pré-reservados."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                editForm.reset();
                setEditandoSetor(null);
              }}
              disabled={editar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={editForm.handleSubmit(onEditar)}
              disabled={editar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa"
            >
              {editar.isPending && (
                <LoadingSpinner className="mr-2 text-nevoa" />
              )}
              Salvar alterações
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Nome do setor"
            htmlFor="nomeEdit"
            error={editForm.formState.errors.nome?.message}
            required
          >
            <Input id="nomeEdit" {...editForm.register("nome")} />
          </FormField>
          <FormField label="Tipo" htmlFor="tipoSetorEdit" required>
            <Select id="tipoSetorEdit" {...editForm.register("tipoSetor")}>
              {TIPOS_SETOR.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </Select>
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Fileiras (1–26)"
              htmlFor="fileirasEdit"
              error={editForm.formState.errors.fileirasHorizontais?.message}
              required
            >
              <Input
                id="fileirasEdit"
                type="number"
                min={1}
                max={26}
                {...editForm.register("fileirasHorizontais")}
              />
            </FormField>
            <FormField
              label="Assentos por fileira"
              htmlFor="assentosEdit"
              error={editForm.formState.errors.assentosPorFileiraVertical?.message}
              required
            >
              <Input
                id="assentosEdit"
                type="number"
                min={1}
                {...editForm.register("assentosPorFileiraVertical")}
              />
            </FormField>
          </div>
          <Card className="bg-nevoa-muted border-laranja/30 p-3 text-xs">
            <p className="text-muted-foreground">
              Novo total de assentos:{" "}
              <strong className="text-noite font-mono">
                {(Number(editForm.watch("fileirasHorizontais")) || 0) *
                  (Number(editForm.watch("assentosPorFileiraVertical")) || 0)}
              </strong>
              {editandoSetor && (
                <span className="ml-2 text-muted-foreground">
                  (atual: {editandoSetor.assentos.length})
                </span>
              )}
            </p>
          </Card>
        </form>
      </Modal>
    </PageLayout>
  );
}
