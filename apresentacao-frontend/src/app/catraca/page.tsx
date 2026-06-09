"use client";

import { useEffect, useRef, useState } from "react";
import { DoorOpen, ScanLine } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/form/FormField";
import { LoadingSpinner } from "@/components/shared/LoadingSpinner";
import {
  CatracaFeedback,
  type CatracaFeedbackTipo,
} from "@/components/domain/CatracaFeedback";
import { useValidarCatraca } from "@/hooks/useCatraca";
import type { ApiError } from "@/lib/api";

const FEEDBACK_DURATION_MS = 2400;

const portoes = ["PORTAO_A", "PORTAO_B", "PORTAO_VIP", "PORTAO_PCD"];

export default function CatracaPage() {
  const [codigoQr, setCodigoQr] = useState("");
  const [portao, setPortao] = useState("PORTAO_A");
  const [feedback, setFeedback] = useState<{
    tipo: CatracaFeedbackTipo;
    titulo: string;
    detalhe?: string;
  }>({ tipo: null, titulo: "" });
  const inputRef = useRef<HTMLInputElement | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  const validar = useValidarCatraca();

  useEffect(() => {
    inputRef.current?.focus();
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, []);

  function agendarLimpeza() {
    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => {
      setFeedback({ tipo: null, titulo: "" });
      setCodigoQr("");
      inputRef.current?.focus();
    }, FEEDBACK_DURATION_MS);
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!codigoQr.trim()) return;
    try {
      const resultado = await validar.mutateAsync({
        codigoQr: codigoQr.trim(),
        portaoAcesso: portao,
        horario: new Date().toISOString(),
      });
      if (resultado.liberado) {
        setFeedback({
          tipo: "valido",
          titulo: "Acesso liberado",
          detalhe: resultado.ingressoId
            ? `Ingresso ${resultado.ingressoId.slice(0, 8)}…`
            : undefined,
        });
      } else {
        const jaUsado = (resultado.motivo ?? "")
          .toLowerCase()
          .includes("utilizado");
        setFeedback({
          tipo: jaUsado ? "ja-usado" : "invalido",
          titulo: jaUsado ? "Ingresso já utilizado" : "Acesso negado",
          detalhe: resultado.motivo,
        });
      }
    } catch (error) {
      const err = error as ApiError;
      setFeedback({
        tipo: "invalido",
        titulo: "Erro na validação",
        detalhe: err.message,
      });
    } finally {
      agendarLimpeza();
    }
  }

  return (
    <main className="relative flex flex-1 items-center justify-center px-4 py-12">
      <div className="from-azul-dark/30 via-noite to-noite absolute inset-0 bg-gradient-to-br" />

      <div className="bg-noite-surface/80 border-laranja/15 relative w-full max-w-lg space-y-6 rounded-3xl border p-8 shadow-stage backdrop-blur">
        <header className="space-y-2 text-center">
          <p className="text-violeta font-mono text-xs uppercase tracking-[0.4em]">
            Catraca · Operação
          </p>
          <h1 className="font-display text-3xl font-bold">
            Validar acesso
          </h1>
          <p className="text-nevoa/60 text-sm">
            Aponte o leitor para o QR Code ou digite o código manualmente.
          </p>
        </header>

        <div className="border-laranja/30 mx-auto flex h-40 w-40 items-center justify-center rounded-2xl border-2 border-dashed">
          <ScanLine className="text-laranja/60 h-16 w-16" />
        </div>

        <form onSubmit={onSubmit} className="space-y-4">
          <FormField label="Código do ingresso" htmlFor="codigoQr">
            <Input
              id="codigoQr"
              ref={inputRef}
              value={codigoQr}
              onChange={(e) => setCodigoQr(e.target.value)}
              placeholder="Cole ou digite o código"
              className="bg-noite border-laranja/30 text-nevoa placeholder:text-nevoa/30 text-center font-mono text-lg tracking-widest"
              autoComplete="off"
            />
          </FormField>

          <FormField label="Portão de acesso" htmlFor="portao">
            <Select
              id="portao"
              value={portao}
              onChange={(e) => setPortao(e.target.value)}
              className="bg-noite border-laranja/30 text-nevoa"
            >
              {portoes.map((p) => (
                <option key={p} value={p}>
                  {p.replace("_", " ")}
                </option>
              ))}
            </Select>
          </FormField>

          <Button
            type="submit"
            disabled={validar.isPending || !codigoQr.trim()}
            className="bg-laranja hover:bg-laranja-light text-noite shadow-stage w-full"
            size="lg"
          >
            {validar.isPending ? (
              <LoadingSpinner className="mr-2 text-noite" />
            ) : (
              <DoorOpen className="mr-2 h-4 w-4" />
            )}
            {validar.isPending ? "Validando" : "Liberar entrada"}
          </Button>
        </form>

        <p className="text-nevoa/30 text-center font-mono text-[10px] uppercase tracking-widest">
          Modo demonstração · sem leitor físico
        </p>
      </div>

      <CatracaFeedback
        tipo={feedback.tipo}
        titulo={feedback.titulo}
        detalhe={feedback.detalhe}
      />
    </main>
  );
}
