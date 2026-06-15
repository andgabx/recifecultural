"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { ArrowLeft, Save, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/form/FormField";
import { SeletorArtista, SeletorEspaco } from "@/components/form/Seletores";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { PageLayout } from "@/components/layout/PageLayout";
import { useCriarEvento } from "@/hooks/useEventosProdutor";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import type { ApiError } from "@/lib/api";

const promotor = IDENTIDADES_MOCK.produtor;

const schema = z
  .object({
    titulo: z.string().min(3, "Título precisa ter ao menos 3 caracteres"),
    descricaoCurta: z
      .string()
      .max(280, "Descrição curta deve ter no máximo 280 caracteres")
      .optional()
      .or(z.literal("")),
    descricaoLonga: z.string().optional().or(z.literal("")),
    categoria: z.enum(["TEATRO", "DANCA", "MUSICA", "INFANTIL", "OUTROS"], {
      message: "Selecione uma categoria",
    }),
    localId: z.string().uuid("Selecione um espaço válido (UUID)").optional().or(z.literal("")),
    periodoInicio: z.string().optional().or(z.literal("")),
    periodoFim: z.string().optional().or(z.literal("")),
    precoInteira: z.coerce
      .number()
      .nonnegative("Preço não pode ser negativo")
      .optional()
      .or(z.literal("")),
    precoMeia: z.coerce
      .number()
      .nonnegative("Preço não pode ser negativo")
      .optional()
      .or(z.literal("")),
    primeiraApresentacao: z.string().optional().or(z.literal("")),
    artistaId: z
      .string()
      .uuid("Id do artista deve ser UUID")
      .optional()
      .or(z.literal("")),
  })
  .refine(
    (v) => {
      if (!v.periodoInicio && !v.periodoFim) return !!v.primeiraApresentacao;
      return true;
    },
    { message: "Informe o período ou ao menos uma data de apresentação", path: ["primeiraApresentacao"] },
  )
  .refine(
    (v) => {
      if (v.periodoInicio && v.periodoFim)
        return new Date(v.periodoFim).getTime() >= new Date(v.periodoInicio).getTime();
      return true;
    },
    { message: "Fim deve ser igual ou posterior ao início", path: ["periodoFim"] },
  )
  .refine(
    (v) => {
      if (v.primeiraApresentacao && v.periodoInicio && v.periodoFim) {
        const ts = new Date(v.primeiraApresentacao).getTime();
        return ts >= new Date(v.periodoInicio).getTime() && ts <= new Date(v.periodoFim).getTime();
      }
      return true;
    },
    { message: "A primeira apresentação deve estar dentro do período informado", path: ["primeiraApresentacao"] },
  );

type CriarFormInput = z.input<typeof schema>;
type CriarFormOutput = z.output<typeof schema>;

const CATEGORIAS = [
  { value: "TEATRO", label: "Teatro" },
  { value: "DANCA", label: "Dança" },
  { value: "MUSICA", label: "Música" },
  { value: "INFANTIL", label: "Infantil" },
  { value: "OUTROS", label: "Outros" },
];

export default function NovoEventoPage() {
  const router = useRouter();
  const criar = useCriarEvento();

  const form = useForm<CriarFormInput, unknown, CriarFormOutput>({
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
      primeiraApresentacao: "",
      artistaId: "",
    },
  });

  async function onSubmit(values: CriarFormOutput) {
    try {
      const isoApresentacao = values.primeiraApresentacao
        ? new Date(values.primeiraApresentacao).toISOString()
        : undefined;

      const isoInicio = values.periodoInicio
        ? new Date(values.periodoInicio).toISOString()
        : undefined;
      const isoFim = values.periodoFim
        ? new Date(values.periodoFim).toISOString()
        : undefined;

      const resposta = await criar.mutateAsync({
        promotorId: promotor.id,
        localId: values.localId || undefined,
        titulo: values.titulo,
        descricaoCurta: values.descricaoCurta || undefined,
        descricaoLonga: values.descricaoLonga || undefined,
        periodoInicio: isoInicio ?? "",
        periodoFim: isoFim ?? "",
        categoria: values.categoria,
        precoInteira:
          typeof values.precoInteira === "number" ? values.precoInteira : undefined,
        precoMeia:
          typeof values.precoMeia === "number" ? values.precoMeia : undefined,
        artistas: values.artistaId ? [values.artistaId] : undefined,
        datasApresentacao: isoApresentacao ? [isoApresentacao] : undefined,
      });
      toast.success(
        `Evento "${values.titulo}" criado em rascunho. Id ${resposta.id.slice(0, 8)}…`,
      );
      router.push("/produtor/eventos");
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Novo evento"
      subtitulo="Crie o evento em rascunho. Você pode submeter para análise na lista de eventos."
      acoes={
        <Link
          href="/produtor/eventos"
          className="text-muted-foreground hover:text-azul inline-flex items-center gap-1 text-sm transition-colors"
        >
          <ArrowLeft className="h-3.5 w-3.5" />
          Voltar
        </Link>
      }
    >
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="grid gap-6 lg:grid-cols-[1fr_320px]"
      >
        <div className="space-y-6">
          <Card className="space-y-5 p-6">
            <h2 className="font-display text-noite text-lg font-semibold">
              Informações básicas
            </h2>
            <FormField
              label="Título"
              htmlFor="titulo"
              error={form.formState.errors.titulo?.message}
              required
            >
              <Input id="titulo" {...form.register("titulo")} />
            </FormField>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Categoria"
                htmlFor="categoria"
                error={form.formState.errors.categoria?.message}
                required
              >
                <Select id="categoria" {...form.register("categoria")}>
                  {CATEGORIAS.map((c) => (
                    <option key={c.value} value={c.value}>
                      {c.label}
                    </option>
                  ))}
                </Select>
              </FormField>
              <FormField
                label="Espaço"
                htmlFor="localId"
                hint="Opcional no rascunho, obrigatório para submeter"
                error={form.formState.errors.localId?.message}
              >
                <SeletorEspaco
                  id="localId"
                  value={form.watch("localId") ?? ""}
                  onChange={(v) => form.setValue("localId", v)}
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
                placeholder="Frase curta que aparece nos cards"
                {...form.register("descricaoCurta")}
              />
            </FormField>
            <FormField label="Descrição longa" htmlFor="descricaoLonga">
              <textarea
                id="descricaoLonga"
                rows={5}
                placeholder="Texto completo exibido na página do evento"
                {...form.register("descricaoLonga")}
                className="border-border bg-white placeholder:text-muted-foreground focus-visible:border-azul focus-visible:ring-azul/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
              />
            </FormField>
          </Card>

          <Card className="space-y-5 p-6">
            <h2 className="font-display text-noite text-lg font-semibold">
              Datas e apresentação
            </h2>
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Início do período"
                htmlFor="periodoInicio"
                error={form.formState.errors.periodoInicio?.message}
                hint="Deixe em branco para evento de apresentação única."
              >
                <Input
                  id="periodoInicio"
                  type="datetime-local"
                  {...form.register("periodoInicio")}
                />
              </FormField>
              <FormField
                label="Fim do período"
                htmlFor="periodoFim"
                error={form.formState.errors.periodoFim?.message}
              >
                <Input
                  id="periodoFim"
                  type="datetime-local"
                  {...form.register("periodoFim")}
                />
              </FormField>
            </div>
            <FormField
              label="Primeira apresentação"
              htmlFor="primeiraApresentacao"
              error={form.formState.errors.primeiraApresentacao?.message}
              hint="Obrigatório quando o período não for informado."
            >
              <Input
                id="primeiraApresentacao"
                type="datetime-local"
                {...form.register("primeiraApresentacao")}
              />
            </FormField>
          </Card>

          <Card className="space-y-5 p-6">
            <h2 className="font-display text-noite text-lg font-semibold">
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
                  {...form.register("precoMeia")}
                />
              </FormField>
            </div>
            <FormField
              label="Artista principal"
              htmlFor="artistaId"
              hint="Obrigatório para submeter (mínimo 1 artista)."
              error={form.formState.errors.artistaId?.message}
            >
              <SeletorArtista
                id="artistaId"
                value={form.watch("artistaId") ?? ""}
                onChange={(v) => form.setValue("artistaId", v)}
              />
            </FormField>
          </Card>
        </div>

        <aside className="space-y-5">
          <Card className="bg-nevoa-muted border-laranja/30 space-y-3 p-5">
            <p className="text-muted-foreground text-xs uppercase tracking-widest">
              Próximos passos
            </p>
            <ol className="text-noite list-decimal space-y-2 pl-4 text-sm">
              <li>Crie o evento em rascunho preenchendo o formulário.</li>
              <li>Acerte os detalhes (espaço, datas, artistas, categoria).</li>
              <li>Na lista de eventos, clique em <strong>Submeter</strong>.</li>
              <li>O gestor recebe na fila de aprovação.</li>
            </ol>
          </Card>

          <Card className="space-y-3 p-5">
            <Button
              type="submit"
              disabled={criar.isPending}
              className="bg-azul hover:bg-azul-light text-nevoa shadow-stage w-full"
            >
              {criar.isPending ? (
                <LoadingSpinner className="mr-2 text-nevoa" />
              ) : (
                <Save className="mr-2 h-4 w-4" />
              )}
              {criar.isPending ? "Salvando" : "Salvar rascunho"}
            </Button>
            <p className="text-muted-foreground flex items-center gap-1 text-center text-[10px]">
              <Sparkles className="h-3 w-3 text-laranja" />
              O evento nasce com status <strong>RASCUNHO</strong>.
            </p>
          </Card>
        </aside>
      </form>
    </PageLayout>
  );
}
