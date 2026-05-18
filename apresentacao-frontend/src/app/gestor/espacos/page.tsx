"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Building2, Lock, Plus, Settings2 } from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/form/FormField";
import { SeletorEspaco } from "@/components/form/Seletores";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import { Modal } from "@/components/shared/Modal";
import { PageLayout } from "@/components/layout/PageLayout";
import {
  useAtualizarCapacidade,
  useCadastrarEspaco,
  useInterditarEspaco,
} from "@/hooks/useEspacos";
import type { ApiError } from "@/lib/api";

const cadastroSchema = z.object({
  nome: z.string().min(2, "Informe o nome"),
  capacidadeMaxima: z.coerce
    .number()
    .int()
    .positive("Capacidade deve ser positiva"),
  riderTecnico: z.string().optional().or(z.literal("")),
});
type CadastroFormInput = z.input<typeof cadastroSchema>;
type CadastroFormOutput = z.output<typeof cadastroSchema>;

const capacidadeSchema = z.object({
  espacoId: z.string().uuid("Id inválido"),
  novaCapacidade: z.coerce.number().int().positive("Capacidade deve ser positiva"),
  ingressosVendidosFuturos: z.coerce.number().int().nonnegative("Não pode ser negativo"),
});
type CapacidadeFormInput = z.input<typeof capacidadeSchema>;
type CapacidadeFormOutput = z.output<typeof capacidadeSchema>;

const interditarSchema = z.object({
  espacoId: z.string().uuid("Id inválido"),
});
type InterditarForm = z.infer<typeof interditarSchema>;

export default function EspacosPage() {
  const [acao, setAcao] = useState<null | "cadastrar" | "capacidade" | "interditar">(
    null,
  );

  const cadastrar = useCadastrarEspaco();
  const atualizar = useAtualizarCapacidade();
  const interditar = useInterditarEspaco();

  const cadastroForm = useForm<CadastroFormInput, unknown, CadastroFormOutput>({
    resolver: zodResolver(cadastroSchema),
    defaultValues: { nome: "", capacidadeMaxima: 100, riderTecnico: "" },
  });

  const capacidadeForm = useForm<CapacidadeFormInput, unknown, CapacidadeFormOutput>({
    resolver: zodResolver(capacidadeSchema),
    defaultValues: { espacoId: "", novaCapacidade: 100, ingressosVendidosFuturos: 0 },
  });

  const interditarForm = useForm<InterditarForm>({
    resolver: zodResolver(interditarSchema),
    defaultValues: { espacoId: "" },
  });

  async function onCadastrar(values: CadastroFormOutput) {
    try {
      const resposta = await cadastrar.mutateAsync({
        nome: values.nome,
        capacidadeMaxima: values.capacidadeMaxima,
        riderTecnico: values.riderTecnico
          ? values.riderTecnico.split(",").map((s) => s.trim()).filter(Boolean)
          : undefined,
      });
      toast.success(`Espaço cadastrado: ${resposta.id.slice(0, 8)}…`);
      cadastroForm.reset();
      setAcao(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onAtualizarCapacidade(values: CapacidadeFormOutput) {
    try {
      await atualizar.mutateAsync({
        id: values.espacoId,
        payload: {
          novaCapacidade: values.novaCapacidade,
          ingressosVendidosFuturos: values.ingressosVendidosFuturos,
        },
      });
      toast.success("Capacidade atualizada");
      capacidadeForm.reset();
      setAcao(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  async function onInterditar(values: InterditarForm) {
    try {
      await interditar.mutateAsync(values.espacoId);
      toast.success("Espaço interditado");
      interditarForm.reset();
      setAcao(null);
    } catch (error) {
      toast.error((error as ApiError).message);
    }
  }

  return (
    <PageLayout
      titulo="Espaços culturais"
      subtitulo="Cadastro, capacidade e interdição administrativa de espaços."
    >
      <div className="grid gap-4 md:grid-cols-3">
        <CardAcao
          icon={Plus}
          titulo="Novo espaço"
          descricao="Cadastrar teatro, casa de shows ou centro cultural."
          cor="vinho"
          onClick={() => setAcao("cadastrar")}
        />
        <CardAcao
          icon={Settings2}
          titulo="Atualizar capacidade"
          descricao="Alterar lotação máxima sem invalidar ingressos vendidos."
          cor="ouro"
          onClick={() => setAcao("capacidade")}
        />
        <CardAcao
          icon={Lock}
          titulo="Interditar espaço"
          descricao="Bloqueia novas reservas. Eventos em curso devem ser tratados separadamente."
          cor="destructive"
          onClick={() => setAcao("interditar")}
        />
      </div>

      <Card className="border-dashed border-ouro/30 bg-ouro/5 p-5">
        <h2 className="font-display text-palco text-sm font-semibold">
          Listagem
        </h2>
        <p className="text-muted-foreground mt-1 text-xs">
          O BFF não expõe <code className="font-mono">GET /api/bff/espacos</code> nesta
          versão — a listagem completa fica disponível em{" "}
          <code className="font-mono">/api/espacos</code> (CRUD direto). Aqui ficam apenas
          as operações de domínio (cadastrar, atualizar capacidade, interditar).
        </p>
      </Card>

      {/* Modal cadastrar */}
      <Modal
        open={acao === "cadastrar"}
        onClose={() => {
          cadastroForm.reset();
          setAcao(null);
        }}
        title="Cadastrar espaço"
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                cadastroForm.reset();
                setAcao(null);
              }}
              disabled={cadastrar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={cadastroForm.handleSubmit(onCadastrar)}
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
            label="Nome"
            htmlFor="nome"
            error={cadastroForm.formState.errors.nome?.message}
            required
          >
            <Input id="nome" {...cadastroForm.register("nome")} />
          </FormField>
          <FormField
            label="Capacidade máxima"
            htmlFor="capacidadeMaxima"
            error={cadastroForm.formState.errors.capacidadeMaxima?.message}
            required
          >
            <Input
              id="capacidadeMaxima"
              type="number"
              min={1}
              {...cadastroForm.register("capacidadeMaxima")}
            />
          </FormField>
          <FormField
            label="Rider técnico (itens separados por vírgula)"
            htmlFor="riderTecnico"
            hint="Ex: Piano,Microfone sem fio,Iluminação LED"
          >
            <Input id="riderTecnico" {...cadastroForm.register("riderTecnico")} />
          </FormField>
        </form>
      </Modal>

      {/* Modal atualizar capacidade */}
      <Modal
        open={acao === "capacidade"}
        onClose={() => {
          capacidadeForm.reset();
          setAcao(null);
        }}
        title="Atualizar capacidade"
        description="A nova capacidade não pode ser menor que ingressos já vendidos para eventos futuros."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                capacidadeForm.reset();
                setAcao(null);
              }}
              disabled={atualizar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={capacidadeForm.handleSubmit(onAtualizarCapacidade)}
              disabled={atualizar.isPending}
              className="bg-ouro hover:bg-ouro-dark text-marquee"
            >
              {atualizar.isPending && <LoadingSpinner className="mr-2 text-marquee" />}
              Atualizar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Espaço"
            htmlFor="espacoId"
            error={capacidadeForm.formState.errors.espacoId?.message}
            required
          >
            <SeletorEspaco
              id="espacoId"
              value={capacidadeForm.watch("espacoId") ?? ""}
              onChange={(v) => capacidadeForm.setValue("espacoId", v)}
            />
          </FormField>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField
              label="Nova capacidade"
              htmlFor="novaCapacidade"
              error={capacidadeForm.formState.errors.novaCapacidade?.message}
              required
            >
              <Input
                id="novaCapacidade"
                type="number"
                min={1}
                {...capacidadeForm.register("novaCapacidade")}
              />
            </FormField>
            <FormField
              label="Ingressos vendidos (futuros)"
              htmlFor="ingressosVendidosFuturos"
              error={
                capacidadeForm.formState.errors.ingressosVendidosFuturos?.message
              }
              required
            >
              <Input
                id="ingressosVendidosFuturos"
                type="number"
                min={0}
                {...capacidadeForm.register("ingressosVendidosFuturos")}
              />
            </FormField>
          </div>
        </form>
      </Modal>

      {/* Modal interditar */}
      <Modal
        open={acao === "interditar"}
        onClose={() => {
          interditarForm.reset();
          setAcao(null);
        }}
        title="Interditar espaço"
        description="Espaços interditados não recebem novos eventos até serem reativados."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                interditarForm.reset();
                setAcao(null);
              }}
              disabled={interditar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={interditarForm.handleSubmit(onInterditar)}
              disabled={interditar.isPending}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              {interditar.isPending && <LoadingSpinner className="mr-2 text-white" />}
              Interditar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Espaço"
            htmlFor="espacoIdInt"
            error={interditarForm.formState.errors.espacoId?.message}
            required
          >
            <SeletorEspaco
              id="espacoIdInt"
              value={interditarForm.watch("espacoId") ?? ""}
              onChange={(v) => interditarForm.setValue("espacoId", v)}
            />
          </FormField>
        </form>
      </Modal>
    </PageLayout>
  );
}

function CardAcao({
  icon: Icon,
  titulo,
  descricao,
  cor,
  onClick,
}: {
  icon: typeof Plus;
  titulo: string;
  descricao: string;
  cor: "vinho" | "ouro" | "destructive";
  onClick: () => void;
}) {
  const corClasses = {
    vinho: "bg-vinho/10 text-vinho",
    ouro: "bg-ouro/15 text-ouro-dark",
    destructive: "bg-destructive/10 text-destructive",
  };
  return (
    <Card
      className="hover:shadow-raised cursor-pointer p-5 transition-shadow"
      onClick={onClick}
    >
      <span
        className={`inline-flex rounded-lg p-2 ${corClasses[cor]}`}
      >
        <Icon className="h-5 w-5" />
      </span>
      <h3 className="font-display text-palco mt-3 text-base font-semibold">
        {titulo}
      </h3>
      <p className="text-muted-foreground mt-1 text-xs">{descricao}</p>
    </Card>
  );
}
