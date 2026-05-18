"use client";

import { motion } from "motion/react";
import { QRCodeSVG } from "qrcode.react";

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
  return (
    <motion.div
      initial={{ rotateY: 90, opacity: 0 }}
      animate={{ rotateY: 0, opacity: 1 }}
      transition={{ ...springConfig, stiffness: 200 }}
      className={cn(
        "bg-palco flex flex-col items-center gap-4 rounded-2xl p-6",
        className,
      )}
    >
      {eventoNome && (
        <p className="text-marquee/70 font-display max-w-xs text-center text-sm">
          {eventoNome}
        </p>
      )}
      <div className="bg-marquee-card rounded-lg p-3">
        <QRCodeSVG
          value={codigo}
          size={size}
          level="H"
          fgColor="#1a1018"
          bgColor="#ffffff"
        />
      </div>
      <p className="text-marquee/60 font-mono text-xs tracking-[0.3em]">
        {codigo.slice(0, 8).toUpperCase()}-{codigo.slice(-4).toUpperCase()}
      </p>
    </motion.div>
  );
}
