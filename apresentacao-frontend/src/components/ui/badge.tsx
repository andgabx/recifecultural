import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";

import { cn } from "@/lib/utils";

const badgeVariants = cva(
  "inline-flex items-center justify-center rounded-full border px-2.5 py-0.5 text-xs font-medium tracking-wide w-fit whitespace-nowrap shrink-0 transition-colors",
  {
    variants: {
      variant: {
        default: "bg-azul/10 text-azul border-azul/20",
        secondary: "bg-nevoa-muted text-noite border-border",
        accent: "bg-laranja/15 text-laranja-dark border-laranja/30",
        violeta: "bg-violeta/15 text-violeta-dark border-violeta/30",
        success: "bg-emerald-100 text-emerald-800 border-emerald-200",
        warning: "bg-violeta/15 text-violeta-dark border-violeta/30 animate-pulse",
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
