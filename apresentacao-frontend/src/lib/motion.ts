import type { Transition, Variants } from "motion/react";

/*
 * Animacoes globais (Fase 6 §0.5).
 * Spring "teatral": rapido com peso, sem rebote excessivo.
 */

export const springConfig: Transition = {
  type: "spring",
  stiffness: 380,
  damping: 28,
};

export const pageVariants: Variants = {
  initial: { opacity: 0, y: 16 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.28, ease: "easeOut" },
  },
  exit: {
    opacity: 0,
    y: -8,
    transition: { duration: 0.18, ease: "easeIn" },
  },
};

export const containerVariants: Variants = {
  animate: { transition: { staggerChildren: 0.07 } },
};

export const itemVariants: Variants = {
  initial: { opacity: 0, y: 20 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.3, ease: "easeOut" },
  },
};
