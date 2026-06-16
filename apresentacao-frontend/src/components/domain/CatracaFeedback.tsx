"use client";

import { AnimatePresence, motion } from "motion/react";
import { AlertTriangle, CheckCircle2, Clock, XCircle } from "lucide-react";

import { cn } from "@/lib/utils";

export type CatracaFeedbackTipo =
  | "valido"
  | "invalido"
  | "ja-usado"
  | "fora-horario"
  | null;

type Props = {
  tipo: CatracaFeedbackTipo;
  titulo: string;
  detalhe?: string;
  evento?: string;
  onFinalizar?: () => void;
};

const config: Record<
  Exclude<CatracaFeedbackTipo, null>,
  { bg: string; texto: string; icone: typeof CheckCircle2; cor: string }
> = {
  valido: {
    bg: "bg-emerald-950",
    texto: "text-emerald-200",
    icone: CheckCircle2,
    cor: "text-emerald-400",
  },
  invalido: {
    bg: "bg-red-950",
    texto: "text-red-100",
    icone: XCircle,
    cor: "text-red-400",
  },
  "ja-usado": {
    bg: "bg-yellow-950",
    texto: "text-yellow-100",
    icone: AlertTriangle,
    cor: "text-violeta",
  },
  "fora-horario": {
    bg: "bg-sky-950",
    texto: "text-sky-100",
    icone: Clock,
    cor: "text-sky-300",
  },
};

export function CatracaFeedback({
  tipo,
  titulo,
  detalhe,
  evento,
  onFinalizar,
}: Props) {
  return (
    <AnimatePresence
      onExitComplete={onFinalizar}
    >
      {tipo && (
        <motion.div
          key={tipo}
          initial={{ scale: 0.85, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 0.9, opacity: 0 }}
          transition={{ type: "spring", stiffness: 500, damping: 25 }}
          className={cn(
            "fixed inset-0 z-50 flex flex-col items-center justify-center gap-6 text-center",
            config[tipo].bg,
            config[tipo].texto,
          )}
        >
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.12, type: "spring", stiffness: 600 }}
          >
            {(() => {
              const Icon = config[tipo].icone;
              return (
                <Icon className={cn("h-32 w-32 stroke-[1.5]", config[tipo].cor)} />
              );
            })()}
          </motion.div>
          <div className="space-y-2">
            <h2 className="font-display text-5xl font-bold leading-none drop-shadow-lg">
              {titulo}
            </h2>
            {evento && (
              <p className="text-nevoa/70 font-display text-lg">{evento}</p>
            )}
            {detalhe && (
              <p className="text-nevoa/60 max-w-xl text-sm">{detalhe}</p>
            )}
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
