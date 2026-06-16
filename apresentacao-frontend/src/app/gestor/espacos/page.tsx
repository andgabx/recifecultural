"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Building2, Lock, Plus, Settings2, Unlock } from "lucide-react";
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
  useEspacos,
  useInterditarEspaco,
  useReativarEspaco,
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
  espacoId: z.string().min(1, "Selecione uma opção"),
  novaCapacidade: z.coerce.number().int().positive("Capacidade deve ser positiva"),
  ingressosVendidosFuturos: z.coerce.number().int().nonnegative("Não pode ser negativo"),
});
type CapacidadeFormInput = z.input<typeof capacidadeSchema>;
type CapacidadeFormOutput = z.output<typeof capacidadeSchema>;

const interditarSchema = z.object({
  espacoId: z.string().min(1, "Selecione uma opção"),
});
type InterditarForm = z.infer<typeof interditarSchema>;

const reativarSchema = z.object({
  espacoId: z.string().min(1, "Selecione uma opção"),
});
type ReativarForm = z.infer<typeof reativarSchema>;

export default function EspacosPage() {
  const [acao, setAcao] = useState<null | "cadastrar" | "capacidade" | "interditar" | "reativar">(
    null,
  );

  const { data: espacos } = useEspacos();
  const cadastrar = useCadastrarEspaco();
  const atualizar = useAtualizarCapacidade();
  const interditar = useInterditarEspaco();
  const reativar = useReativarEspaco();

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

  const reativarForm = useForm<ReativarForm>({
    resolver: zodResolver(reativarSchema),
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

  async function onReativar(values: ReativarForm) {
    try {
      await reativar.mutateAsync(values.espacoId);
      toast.success("Espaço reativado");
      reativarForm.reset();
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
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <CardAcao
          icon={Plus}
          titulo="Novo espaço"
          descricao="Cadastrar teatro, casa de shows ou centro cultural."
          cor="azul"
          onClick={() => setAcao("cadastrar")}
        />
        <CardAcao
          icon={Settings2}
          titulo="Atualizar capacidade"
          descricao="Alterar lotação máxima sem invalidar ingressos vendidos."
          cor="laranja"
          onClick={() => setAcao("capacidade")}
        />
        <CardAcao
          icon={Lock}
          titulo="Interditar espaço"
          descricao="Bloqueia novas reservas. Eventos em curso devem ser tratados separadamente."
          cor="destructive"
          onClick={() => setAcao("interditar")}
        />
        <CardAcao
          icon={Unlock}
          titulo="Reativar espaço"
          descricao="Reabilita espaço interditado para receber novas pautas."
          cor="verde"
          onClick={() => setAcao("reativar")}
        />
      </div>

      {/* Listagem de espaços */}
      <div className="mt-2">
        <h2 className="font-display text-noite mb-3 text-sm font-semibold">
          Espaços cadastrados
        </h2>
        {espacos && espacos.length > 0 ? (
          <div className="rounded-md border">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b bg-muted/50">
                  <th className="px-4 py-2 text-left font-medium">Nome</th>
                  <th className="px-4 py-2 text-right font-medium">Capacidade</th>
                  <th className="px-4 py-2 text-center font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {espacos.map((e) => (
                  <tr key={e.id} className="border-b last:border-0">
                    <td className="px-4 py-2">{e.nome}</td>
                    <td className="px-4 py-2 text-right">{e.capacidadeMaxima}</td>
                    <td className="px-4 py-2 text-center">
                      <span
                        className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ${
                          e.status === "ATIVO"
                            ? "bg-green-100 text-green-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {e.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-muted-foreground text-sm">Nenhum espaço cadastrado.</p>
        )}
      </div>

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
              className="bg-laranja hover:bg-laranja-dark text-nevoa"
            >
              {atualizar.isPending && <LoadingSpinner className="mr-2 text-nevoa" />}
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

      {/* Modal reativar */}
      <Modal
        open={acao === "reativar"}
        onClose={() => {
          reativarForm.reset();
          setAcao(null);
        }}
        title="Reativar espaço"
        description="Reabilita um espaço interditado para receber novas pautas e reservas."
        footer={
          <>
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                reativarForm.reset();
                setAcao(null);
              }}
              disabled={reativar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              onClick={reativarForm.handleSubmit(onReativar)}
              disabled={reativar.isPending}
              className="bg-green-600 text-white hover:bg-green-700"
            >
              {reativar.isPending && <LoadingSpinner className="mr-2 text-white" />}
              Reativar
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <FormField
            label="Espaço"
            htmlFor="espacoIdReativar"
            error={reativarForm.formState.errors.espacoId?.message}
            required
          >
            <SeletorEspaco
              id="espacoIdReativar"
              value={reativarForm.watch("espacoId") ?? ""}
              onChange={(v) => reativarForm.setValue("espacoId", v)}
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
  cor: "azul" | "laranja" | "destructive" | "verde";
  onClick: () => void;
}) {
  const corClasses = {
    azul: "bg-azul/10 text-azul",
    laranja: "bg-laranja/15 text-laranja-dark",
    destructive: "bg-destructive/10 text-destructive",
    verde: "bg-green-100 text-green-700",
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
      <h3 className="font-display text-noite mt-3 text-base font-semibold">
        {titulo}
      </h3>
      <p className="text-muted-foreground mt-1 text-xs">{descricao}</p>
    </Card>
  );
}
