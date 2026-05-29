import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";

import { cn } from "@/lib/utils";

const badgeVariants = cva(
  "inline-flex items-center justify-center rounded-full border px-2.5 py-0.5 text-xs font-medium tracking-wide w-fit whitespace-nowrap shrink-0 transition-colors",
  {
    variants: {
      variant: {
        default: "bg-vinho/10 text-vinho border-vinho/20",
        secondary: "bg-marquee-muted text-palco border-border",
        accent: "bg-ouro/15 text-ouro-dark border-ouro/30",
        frevo: "bg-frevo/20 text-yellow-800 border-frevo/40",
        success: "bg-emerald-100 text-emerald-800 border-emerald-200",
        warning: "bg-frevo/20 text-yellow-900 border-frevo/40 animate-pulse",
        destructive: "bg-destructive/15 text-destructive border-destructive/30",
        outline: "bg-transparent text-foreground border-border",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  },
);

type BadgeProps = React.ComponentProps<"span"> &
  VariantProps<typeof badgeVariants>;

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <span
      data-slot="badge"
      className={cn(badgeVariants({ variant }), className)}
      {...props}
    />
  );
}

export { Badge, badgeVariants };
