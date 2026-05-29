"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useEffect } from "react";
import { useFieldArray, useForm } from "react-hook-form";
import { ArrowLeft, Lock, Plus, Save, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { z } from "zod";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { SeletorArtista, SeletorEspaco } from "@/components/form/Seletores";
import { EmptyState } from "@/components/shared/EmptyState";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PageLayout } from "@/components/layout/PageLayout";
import { useEvento } from "@/hooks/useEventos";
import { useEditarEvento } from "@/hooks/useEventosProdutor";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import type { ApiError } from "@/lib/api";

const promotor = IDENTIDADES_MOCK.produtor;

const schema = z
  .object({
    titulo: z.string().min(3, "Título precisa ter ao menos 3 caracteres"),
    descricaoCurta: z.string().max(280).optional().or(z.literal("")),
    descricaoLonga: z.string().optional().or(z.literal("")),
    categoria: z.enum(["TEATRO", "DANCA", "MUSICA", "INFANTIL", "OUTROS"]),
    localId: z.string().uuid().optional().or(z.literal("")),
    periodoInicio: z.string().min(1, "Informe quando começa"),
    periodoFim: z.string().min(1, "Informe quando termina"),
    precoInteira: z.coerce.number().nonnegative().optional().or(z.literal("")),
    precoMeia: z.coerce.number().nonnegative().optional().or(z.literal("")),
    apresentacoes: z.array(z.object({ dataHora: z.string().min(1) })).min(1, "Adicione ao menos uma data de apresentação"),
    artistaId: z.string().uuid().optional().or(z.literal("")),
  })
  .refine(
    (v) => new Date(v.periodoFim).getTime() >= new Date(v.periodoInicio).getTime(),
    { message: "Fim deve ser posterior ao início", path: ["periodoFim"] },
  );
type EditarFormInput = z.input<typeof schema>;
type EditarFormOutput = z.output<typeof schema>;

const CATEGORIAS = [
  { value: "TEATRO", label: "Teatro" },
  { value: "DANCA", label: "Dança" },
  { value: "MUSICA", label: "Música" },
  { value: "INFANTIL", label: "Infantil" },
  { value: "OUTROS", label: "Outros" },
];

const toLocalInput = (iso?: string) => (iso ? iso.slice(0, 16) : "");

export default function EditarEventoPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { data: evento, isLoading, isError } = useEvento(params.id);
  const editar = useEditarEvento(promotor.id);

  const form = useForm<EditarFormInput, unknown, EditarFormOutput>({
    resolver: zodResolver(schema),
    defaultValues: {
      titulo: "",
      descricaoCurta: "",
      descricaoLonga: "",
      categoria: "TEATRO",
      localId: "",
      periodoInicio: "",
      periodoFim: "",
      precoInteira: "" as unknown as number,
      precoMeia: "" as unknown as number,
      apresentacoes: [{ dataHora: "" }],
      artistaId: "",
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "apresentacoes",
  });

  // Pré-preenche o form quando o evento carrega
  useEffect(() => {
    if (!evento) return;
    const categoriaValida = CATEGORIAS.find((c) => c.value === evento.categoria);
    const datas = evento.apresentacoes && evento.apresentacoes.length > 0
      ? evento.apresentacoes.map((a) => ({ dataHora: toLocalInput(a.dataHora) }))
      : [{ dataHora: "" }];
    form.reset({
      titulo: evento.titulo ?? "",
      descricaoCurta: evento.descricaoCurta ?? "",
      descricaoLonga: evento.descricaoLonga ?? "",
      categoria: (categoriaValida?.value ??
        "TEATRO") as EditarFormInput["categoria"],
      localId: evento.localId ?? "",
      periodoInicio: toLocalInput(evento.periodoInicio),
      periodoFim: toLocalInput(evento.periodoFim),
      precoInteira: evento.precoInteira
        ? (Number(evento.precoInteira) as unknown as number)
        : ("" as unknown as number),
      precoMeia: evento.precoMeia
        ? (Number(evento.precoMeia) as unknown as number)
        : ("" as unknown as number),
      apresentacoes: datas,
      artistaId: "",
    });
  }, [evento, form]);

  async function onSubmit(values: EditarFormOutput) {
    try {
      await editar.mutateAsync({
        id: params.id,
        payload: {
          localId: values.localId || undefined,
          titulo: values.titulo,
          descricaoCurta: values.descricaoCurta || undefined,
          descricaoLonga: values.descricaoLonga || undefined,
          periodoInicio: new Date(values.periodoInicio).toISOString(),
          periodoFim: new Date(values.periodoFim).toISOString(),
          categoria: values.categoria,
          precoInteira:
            typeof values.precoInteira === "number" ? values.precoInteira : undefined,
          precoMeia:
            typeof values.precoMeia === "number" ? values.precoMeia : undefined,
          artistas: values.artistaId ? [values.artistaId] : undefined,
          datasApresentacao: values.apresentacoes
            .map((a) => new Date(a.dataHora).toISOString()),
        },
      });
      toast.success("Evento atualizado");
      router.push("/produtor/eventos");
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  // Renderização condicional
  if (isLoading) {
    return (
      <PageLayout titulo="Editar evento">
        <Skeleton className="h-96 w-full" />
      </PageLayout>
    );
  }

  if (isError || !evento) {
    return (
      <PageLayout titulo="Editar evento">
        <EmptyState
          icon={ArrowLeft}
          title="Evento não encontrado"
          description="Verifique o link ou volte à lista."
        />
      </PageLayout>
    );
  }

  const editavel = evento.status === "RASCUNHO";

  return (
    <PageLayout
      titulo="Editar evento"
      subtitulo={evento.titulo}
      acoes={
        <Link
          href="/produtor/eventos"
          className="text-muted-foreground hover:text-vinho inline-flex items-center gap-1 text-sm"
        >
          <ArrowLeft className="h-3.5 w-3.5" />
          Voltar
        </Link>
      }
    >
      {!editavel && (
        <Card className="border-frevo/40 bg-frevo/10 flex items-center gap-3 p-4">
          <Lock className="text-frevo h-5 w-5" />
          <div className="flex-1">
            <p className="text-palco text-sm font-semibold">
              Edição bloqueada
            </p>
            <p className="text-muted-foreground text-xs">
              Apenas eventos em RASCUNHO podem ser editados. Status atual:{" "}
              <Badge variant="frevo">{evento.status}</Badge>
            </p>
          </div>
        </Card>
      )}

      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="grid gap-6 lg:grid-cols-[1fr_320px]"
      >
        <div className="space-y-6">
          <Card className="space-y-5 p-6">
            <h2 className="font-display text-palco text-lg font-semibold">
              Informações básicas
            </h2>
            <FormField
              label="Título"
              htmlFor="titulo"
              error={form.formState.errors.titulo?.message}
              required
            >
              <Input id="titulo" disabled={!editavel} {...form.register("titulo")} />
            </FormField>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField label="Categoria" htmlFor="categoria" required>
                <Select
                  id="categoria"
                  disabled={!editavel}
                  {...form.register("categoria")}
                >
                  {CATEGORIAS.map((c) => (
                    <option key={c.value} value={c.value}>
                      {c.label}
                    </option>
                  ))}
                </Select>
              </FormField>
              <FormField label="Espaço" htmlFor="localId">
                <SeletorEspaco
                  id="localId"
                  value={form.watch("localId") ?? ""}
                  onChange={(v) => editavel && form.setValue("localId", v)}
                />
              </FormField>
            </div>
            <FormField
              label="Descrição curta"
              htmlFor="descricaoCurta"
              hint={`${form.watch("descricaoCurta")?.length ?? 0} / 280`}
              error={form.formState.errors.descricaoCurta?.message}
            >
              <Input
                id="descricaoCurta"
                disabled={!editavel}
                {...form.register("descricaoCurta")}
              />
            </FormField>
            <FormField label="Descrição longa" htmlFor="descricaoLonga">
              <textarea
                id="descricaoLonga"
                rows={5}
                disabled={!editavel}
                {...form.register("descricaoLonga")}
                className="border-border bg-marquee-card placeholder:text-muted-foreground focus-visible:border-vinho focus-visible:ring-vinho/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </FormField>
          </Card>

          <Card className="space-y-5 p-6">
            <h2 className="font-display text-palco text-lg font-semibold">
              Datas
            </h2>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Início do período"
                htmlFor="periodoInicio"
                error={form.formState.errors.periodoInicio?.message}
                required
              >
                <Input
                  id="periodoInicio"
                  type="datetime-local"
                  disabled={!editavel}
                  {...form.register("periodoInicio")}
                />
              </FormField>
              <FormField
                label="Fim do período"
                htmlFor="periodoFim"
                error={form.formState.errors.periodoFim?.message}
                required
              >
                <Input
                  id="periodoFim"
                  type="datetime-local"
                  disabled={!editavel}
                  {...form.register("periodoFim")}
                />
              </FormField>
            </div>
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-2">
                <p className="text-palco text-sm font-medium">
                  Datas de apresentação
                  <span className="text-destructive ml-1">*</span>
                </p>
                <Button
                  type="button"
                  size="sm"
                  variant="outline"
                  disabled={!editavel}
                  onClick={() => append({ dataHora: "" })}
                >
                  <Plus className="mr-1 h-3.5 w-3.5" />
                  Adicionar sessão
                </Button>
              </div>
              {form.formState.errors.apresentacoes?.root?.message && (
                <p className="text-destructive text-xs">
                  {form.formState.errors.apresentacoes.root.message}
                </p>
              )}
              <div className="space-y-2">
                {fields.map((field, index) => (
                  <div key={field.id} className="flex items-center gap-2">
                    <Input
                      type="datetime-local"
                      disabled={!editavel}
                      className="flex-1"
                      {...form.register(`apresentacoes.${index}.dataHora`)}
                    />
                    {fields.length > 1 && editavel && (
                      <Button
                        type="button"
                        size="icon-sm"
                        variant="ghost"
                        onClick={() => remove(index)}
                        className="text-destructive hover:bg-destructive/10"
                        aria-label="Remover sessão"
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </Button>
                    )}
                  </div>
                ))}
              </div>
              <p className="text-muted-foreground text-xs">
                Cada sessão gera um ID de apresentação único — usado para sorteios e acessibilidade.
              </p>
            </div>
          </Card>

          <Card className="space-y-5 p-6">
            <h2 className="font-display text-palco text-lg font-semibold">
              Preços e elenco
            </h2>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Preço inteira (R$)"
                htmlFor="precoInteira"
                error={form.formState.errors.precoInteira?.message}
              >
                <Input
                  id="precoInteira"
                  type="number"
                  step="0.01"
                  min={0}
                  disabled={!editavel}
                  {...form.register("precoInteira")}
                />
              </FormField>
              <FormField
                label="Preço meia (R$)"
                htmlFor="precoMeia"
                error={form.formState.errors.precoMeia?.message}
              >
                <Input
                  id="precoMeia"
                  type="number"
                  step="0.01"
                  min={0}
                  disabled={!editavel}
                  {...form.register("precoMeia")}
                />
              </FormField>
            </div>
            <FormField
              label="Artista principal"
              htmlFor="artistaId"
              hint="Substitui a lista atual de artistas."
            >
              <SeletorArtista
                id="artistaId"
                value={form.watch("artistaId") ?? ""}
                onChange={(v) => editavel && form.setValue("artistaId", v)}
              />
            </FormField>
          </Card>
        </div>

        <aside className="space-y-5">
          <Card className="bg-marquee-muted border-ouro/30 space-y-2 p-5">
            <p className="text-muted-foreground text-xs uppercase tracking-widest">
              Status atual
            </p>
            <Badge variant={editavel ? "secondary" : "frevo"}>
              {evento.status}
            </Badge>
            <p className="text-muted-foreground text-xs">
              Você só pode editar enquanto estiver em RASCUNHO. Depois de
              submeter, o evento fica imutável até decisão do gestor.
            </p>
          </Card>

          <Card className="space-y-3 p-5">
            <Button
              type="submit"
              disabled={!editavel || editar.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee shadow-stage w-full"
            >
              {editar.isPending ? (
                <LoadingSpinner className="mr-2 text-marquee" />
              ) : (
                <Save className="mr-2 h-4 w-4" />
              )}
              Salvar alterações
            </Button>
          </Card>
        </aside>
      </form>
    </PageLayout>
  );
}
