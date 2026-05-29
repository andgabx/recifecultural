"use client";

import { AnimatePresence, motion } from "motion/react";
import { X } from "lucide-react";
import { useEffect, type ReactNode } from "react";

import { cn } from "@/lib/utils";

type ModalProps = {
  open: boolean;
  onClose: () => void;
  title?: string;
  description?: string;
  children: ReactNode;
  footer?: ReactNode;
  className?: string;
  hideClose?: boolean;
};

export function Modal({
  open,
  onClose,
  title,
  description,
  children,
  footer,
  className,
  hideClose,
}: ModalProps) {
  useEffect(() => {
    if (!open) return;
    const handler = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handler);
    document.body.style.overflow = "hidden";
    return () => {
      window.removeEventListener("keydown", handler);
      document.body.style.overflow = "";
    };
  }, [open, onClose]);

  return (
    <AnimatePresence>
      {open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.18 }}
            className="bg-palco/70 absolute inset-0 backdrop-blur-sm"
            onClick={onClose}
            aria-hidden
          />
          <motion.div
            role="dialog"
            aria-modal
            initial={{ opacity: 0, scale: 0.96, y: 12 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.96, y: 12 }}
            transition={{ type: "spring", stiffness: 400, damping: 32 }}
            className={cn(
              "bg-marquee-card relative z-10 w-full max-w-lg overflow-hidden rounded-2xl shadow-raised",
              className,
            )}
          >
            {!hideClose && (
              <button
                type="button"
                onClick={onClose}
                aria-label="Fechar"
                className="text-muted-foreground hover:bg-muted hover:text-foreground absolute right-3 top-3 z-10 rounded-full p-1.5 transition-colors"
              >
                <X className="h-4 w-4" />
              </button>
            )}
            {(title || description) && (
              <header className="border-border border-b p-6 pr-12">
                {title && (
                  <h2 className="font-display text-palco text-lg font-semibold">
                    {title}
                  </h2>
                )}
                {description && (
                  <p className="text-muted-foreground mt-1 text-sm">
                    {description}
                  </p>
                )}
              </header>
            )}
            <div className="p-6">{children}</div>
            {footer && (
              <footer className="bg-marquee-muted/40 border-border flex items-center justify-end gap-2 border-t px-6 py-4">
                {footer}
              </footer>
            )}
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
