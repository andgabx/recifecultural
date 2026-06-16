"use client";

import { useState } from "react";
import { motion } from "motion/react";
import { QRCodeSVG } from "qrcode.react";
import { Check, Copy } from "lucide-react";

import { springConfig } from "@/lib/motion";
import { cn } from "@/lib/utils";

export function QRCodeDisplay({
  codigo,
  eventoNome,
  size = 220,
  className,
}: {
  codigo: string;
  eventoNome?: string;
  size?: number;
  className?: string;
}) {
  const [copiado, setCopiado] = useState(false);

  async function copiar() {
    try {
      await navigator.clipboard.writeText(codigo);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 1500);
    } catch {
      // clipboard pode não estar disponível (http, permissões); silencioso
    }
  }

  return (
    <motion.div
      initial={{ rotateY: 90, opacity: 0 }}
      animate={{ rotateY: 0, opacity: 1 }}
      transition={{ ...springConfig, stiffness: 200 }}
      className={cn(
        "bg-noite flex flex-col items-center gap-4 rounded-2xl p-6",
        className,
      )}
    >
      {eventoNome && (
        <p className="text-nevoa/70 font-display max-w-xs text-center text-sm">
          {eventoNome}
        </p>
      )}
      <div className="bg-white rounded-lg p-3">
        <QRCodeSVG
          value={codigo}
          size={size}
          level="H"
          fgColor="#1a1018"
          bgColor="#ffffff"
        />
      </div>
      <button
        type="button"
        onClick={copiar}
        title="Copiar código"
        className="text-nevoa/70 hover:text-nevoa flex max-w-full items-center gap-2 break-all rounded-md px-2 py-1 font-mono text-xs transition-colors"
      >
        <span>{codigo}</span>
        {copiado ? (
          <Check className="h-3.5 w-3.5 shrink-0" />
        ) : (
          <Copy className="h-3.5 w-3.5 shrink-0" />
        )}
      </button>
    </motion.div>
  );
}
