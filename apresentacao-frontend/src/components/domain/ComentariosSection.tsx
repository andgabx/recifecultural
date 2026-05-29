"use client";

import { AnimatePresence, motion } from "motion/react";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Heart,
  MessageCircle,
  Send,
  Star,
  Trash2,
  type LucideIcon,
} from "lucide-react";
import { toast } from "sonner";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { FormField } from "@/components/form/FormField";
import { ConfirmDialog } from "@/components/shared/ConfirmDialog";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import {
  useComentarios,
  useCurtirComentario,
  useDeletarComentario,
  usePostarComentario,
} from "@/hooks/useComentarios";
import type { ApiError } from "@/lib/api";
import { IDENTIDADES_MOCK } from "@/lib/identidadeMock";
import { useRole } from "@/lib/role";
import { cn } from "@/lib/utils";
import type { ComentarioResumo } from "@/services/bff/comentarios";
import type { UUID } from "@/types/dominio";

const TEXTO_MIN = 10;
const TEXTO_MAX = 500;

const novoSchema = z.object({
  texto: z
    .string()
    .trim()
    .min(TEXTO_MIN, `Mínimo ${TEXTO_MIN} caracteres`)
    .max(TEXTO_MAX, `Máximo ${TEXTO_MAX} caracteres`),
});
type NovoForm = z.infer<typeof novoSchema>;

function formatarData(iso: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(iso));
}

export function ComentariosSection({ eventoId }: { eventoId: UUID }) {
  const { papel } = useRole();
  const usuarioAtual = IDENTIDADES_MOCK[papel];
  const [respondendoA, setRespondendoA] = useState<ComentarioResumo | null>(null);
  const [comentarioParaDeletar, setComentarioParaDeletar] =
    useState<ComentarioResumo | null>(null);

  const { data: comentarios, isLoading, isError } = useComentarios(eventoId);
  const postar = usePostarComentario(eventoId);
  const curtir = useCurtirComentario(eventoId);
  const deletar = useDeletarComentario(eventoId);

  const form = useForm<NovoForm>({
    resolver: zodResolver(novoSchema),
    defaultValues: { texto: "" },
  });

  const { topo, respostasPorPai } = useMemo(() => {
    const lista = comentarios ?? [];
    const topo = lista.filter((c) => !c.comentarioPaiId);
    const respostasPorPai = new Map<string, ComentarioResumo[]>();
    for (const c of lista) {
      if (c.comentarioPaiId) {
        const arr = respostasPorPai.get(c.comentarioPaiId) ?? [];
        arr.push(c);
        respostasPorPai.set(c.comentarioPaiId, arr);
      }
    }
    return { topo, respostasPorPai };
  }, [comentarios]);

  const notas = useMemo(
    () =>
      (comentarios ?? [])
        .map((c) => c.nota)
        .filter((n): n is number => n != null),
    [comentarios],
  );
  const mediaNotas = notas.length
    ? notas.reduce((a, b) => a + b, 0) / notas.length
    : null;

  async function onPostar(values: NovoForm) {
    try {
      await postar.mutateAsync({
        eventoId,
        espectadorId: usuarioAtual.id,
        texto: values.texto,
        comentarioPaiId: respondendoA?.id,
      });
      toast.success(
        respondendoA ? "Resposta enviada" : "Comentário publicado",
      );
      form.reset();
      setRespondendoA(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onCurtir(c: ComentarioResumo) {
    try {
      await curtir.mutateAsync({
        id: c.id,
        payload: { espectadorId: usuarioAtual.id },
      });
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function confirmarDeletar() {
    if (!comentarioParaDeletar) return;
    try {
      await deletar.mutateAsync(comentarioParaDeletar.id);
      toast.success("Comentário removido");
      setComentarioParaDeletar(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <section className="space-y-6">
      <header className="flex items-end justify-between gap-3 border-b border-border pb-3">
        <div>
          <h2 className="font-display text-palco flex items-center gap-2 text-2xl font-semibold tracking-tight">
            <MessageCircle className="text-vinho h-5 w-5" />
            Comentários
          </h2>
          <p className="text-muted-foreground text-sm">
            {comentarios
              ? `${comentarios.length} ${comentarios.length === 1 ? "comentário" : "comentários"}`
              : "Carregando…"}
          </p>
        </div>
        {mediaNotas != null && (
          <Card className="bg-marquee-muted border-ouro/30 flex items-center gap-2 px-4 py-2">
            <Star className="text-ouro h-4 w-4 fill-ouro" />
            <span className="font-display text-palco text-lg font-bold">
              {mediaNotas.toFixed(1)}
            </span>
            <span className="text-muted-foreground text-xs">
              ({notas.length})
            </span>
          </Card>
        )}
      </header>

      {/* Form de novo comentário (ou resposta) */}
      <Card className="space-y-3 p-5">
        {respondendoA && (
          <div className="bg-vinho/5 border-l-vinho border-l-2 -mx-5 -mt-5 mb-3 px-5 py-2 text-xs">
            <p className="text-muted-foreground">
              Respondendo a{" "}
              <span className="font-mono">
                {respondendoA.espectadorId.slice(0, 8)}…
              </span>
              :
            </p>
            <p className="text-palco line-clamp-1 italic">
              "{respondendoA.texto}"
            </p>
          </div>
        )}
        <form onSubmit={form.handleSubmit(onPostar)} className="space-y-3">
          <FormField
            label={
              respondendoA
                ? "Sua resposta"
                : `Comentando como ${usuarioAtual.nome}`
            }
            htmlFor="texto"
            hint={`${form.watch("texto")?.length ?? 0} / ${TEXTO_MAX} (mín. ${TEXTO_MIN})`}
            error={form.formState.errors.texto?.message}
            required
          >
            <textarea
              id="texto"
              rows={3}
              maxLength={TEXTO_MAX}
              placeholder="O que você achou desse evento?"
              {...form.register("texto")}
              className="border-border bg-marquee-card placeholder:text-muted-foreground focus-visible:border-vinho focus-visible:ring-vinho/30 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2"
            />
          </FormField>
          <div className="flex justify-end gap-2">
            {respondendoA && (
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => {
                  setRespondendoA(null);
                  form.reset();
                }}
              >
                Cancelar
              </Button>
            )}
            <Button
              type="submit"
              size="sm"
              disabled={postar.isPending}
              className="bg-vinho hover:bg-vinho-light text-marquee"
            >
              {postar.isPending ? (
                <LoadingSpinner className="mr-2 text-marquee" />
              ) : (
                <Send className="mr-2 h-3.5 w-3.5" />
              )}
              Publicar
            </Button>
          </div>
        </form>
      </Card>

      {isLoading && (
        <div className="space-y-3">
          {Array.from({ length: 2 }).map((_, i) => (
            <Skeleton key={i} className="h-24 w-full" />
          ))}
        </div>
      )}

      {isError && (
        <Card className="border-destructive/30 bg-destructive/5 p-4 text-sm">
          <p className="text-destructive">
            Não foi possível carregar comentários.
          </p>
        </Card>
      )}

      {comentarios && comentarios.length === 0 && (
        <Card className="border-dashed border-ouro/30 bg-ouro/5 p-6 text-center">
          <MessageCircle className="text-ouro/60 mx-auto mb-2 h-8 w-8" />
          <p className="text-muted-foreground text-sm">
            Seja o primeiro a comentar sobre este evento.
          </p>
        </Card>
      )}

      {comentarios && comentarios.length > 0 && (
        <ul className="space-y-3">
          <AnimatePresence>
            {topo.map((c) => (
              <motion.li
                key={c.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0.3, x: 8 }}
                layout
              >
                <ComentarioCard
                  comentario={c}
                  podeCurtir={c.espectadorId !== usuarioAtual.id}
                  podeDeletar={c.espectadorId === usuarioAtual.id}
                  onCurtir={() => onCurtir(c)}
                  onResponder={() => setRespondendoA(c)}
                  onDeletar={() => setComentarioParaDeletar(c)}
                  curtindo={curtir.isPending}
                  deletando={deletar.isPending}
                />
                {/* Respostas */}
                {(respostasPorPai.get(c.id) ?? []).map((r) => (
                  <div key={r.id} className="ml-8 mt-2">
                    <ComentarioCard
                      comentario={r}
                      podeCurtir={r.espectadorId !== usuarioAtual.id}
                      podeDeletar={r.espectadorId === usuarioAtual.id}
                      onCurtir={() => onCurtir(r)}
                      onResponder={() => setRespondendoA(c)}
                      onDeletar={() => setComentarioParaDeletar(r)}
                      curtindo={curtir.isPending}
                      deletando={deletar.isPending}
                      compacto
                    />
                  </div>
                ))}
              </motion.li>
            ))}
          </AnimatePresence>
        </ul>
      )}

      <ConfirmDialog
        open={comentarioParaDeletar !== null}
        onClose={() => setComentarioParaDeletar(null)}
        onConfirm={confirmarDeletar}
        title="Apagar comentário?"
        description={
          comentarioParaDeletar && (
            <div className="space-y-2">
              <p>Esta ação é permanente. O comentário abaixo será removido:</p>
              <blockquote className="border-l-vinho bg-marquee-muted text-palco border-l-2 px-3 py-2 text-sm italic">
                "{comentarioParaDeletar.texto}"
              </blockquote>
            </div>
          )
        }
        confirmLabel="Apagar"
        dangerous
        loading={deletar.isPending}
      />
    </section>
  );
}

function ComentarioCard({
  comentario,
  podeCurtir,
  podeDeletar,
  onCurtir,
  onResponder,
  onDeletar,
  curtindo,
  deletando,
  compacto,
}: {
  comentario: ComentarioResumo;
  podeCurtir: boolean;
  podeDeletar: boolean;
  onCurtir: () => void;
  onResponder: () => void;
  onDeletar: () => void;
  curtindo: boolean;
  deletando: boolean;
  compacto?: boolean;
}) {
  return (
    <Card className={cn("space-y-2", compacto ? "p-3" : "p-4")}>
      <header className="flex items-start justify-between gap-3">
        <div className="flex items-center gap-2">
          <div className="bg-vinho/15 text-vinho flex h-8 w-8 items-center justify-center rounded-full font-mono text-xs font-bold uppercase">
            {comentario.espectadorId.slice(0, 2)}
          </div>
          <div>
            <p className="text-palco font-mono text-xs">
              {comentario.espectadorId.slice(0, 8)}…
            </p>
            <p className="text-muted-foreground text-[10px]">
              {formatarData(comentario.criadoEm)}
            </p>
          </div>
        </div>
        {comentario.nota != null && (
          <Badge variant="accent" className="gap-1">
            <Star className="h-3 w-3 fill-ouro-dark" />
            {comentario.nota}
          </Badge>
        )}
      </header>
      <p className="text-palco text-sm">{comentario.texto}</p>
      <footer className="flex items-center gap-2 pt-1">
        <BotaoAcao
          icon={Heart}
          ativo={false}
          disabled={!podeCurtir || curtindo}
          onClick={onCurtir}
          contagem={comentario.totalCurtidas}
          label="Curtir"
          tooltip={!podeCurtir ? "Não pode curtir o próprio comentário" : "Curtir"}
        />
        {!compacto && (
          <BotaoAcao
            icon={MessageCircle}
            onClick={onResponder}
            label="Responder"
          />
        )}
        {podeDeletar && (
          <BotaoAcao
            icon={Trash2}
            onClick={onDeletar}
            disabled={deletando}
            label="Apagar"
            destructivo
          />
        )}
      </footer>
    </Card>
  );
}

function BotaoAcao({
  icon: Icon,
  onClick,
  ativo,
  disabled,
  contagem,
  label,
  tooltip,
  destructivo,
}: {
  icon: LucideIcon;
  onClick: () => void;
  ativo?: boolean;
  disabled?: boolean;
  contagem?: number;
  label: string;
  tooltip?: string;
  destructivo?: boolean;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      title={tooltip ?? label}
      className={cn(
        "inline-flex items-center gap-1 rounded-full px-2 py-1 text-xs transition-colors",
        destructivo
          ? "text-destructive hover:bg-destructive/10"
          : ativo
            ? "bg-vinho/10 text-vinho"
            : "text-muted-foreground hover:bg-marquee-muted hover:text-palco",
        disabled && "cursor-not-allowed opacity-50",
      )}
    >
      <Icon className="h-3.5 w-3.5" />
      <span>{label}</span>
      {contagem != null && contagem > 0 && (
        <span className="font-mono">{contagem}</span>
      )}
    </button>
  );
}
