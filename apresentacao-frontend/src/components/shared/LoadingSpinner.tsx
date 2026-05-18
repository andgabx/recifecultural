"use client";

import { motion } from "motion/react";
import { Loader2 } from "lucide-react";

import { cn } from "@/lib/utils";

export function LoadingSpinner({ className }: { className?: string }) {
  return (
    <motion.span
      animate={{ rotate: 360 }}
      transition={{ repeat: Infinity, duration: 0.9, ease: "linear" }}
      className={cn("text-vinho inline-flex", className)}
      aria-label="Carregando"
    >
      <Loader2 className="h-4 w-4" />
    </motion.span>
  );
}
