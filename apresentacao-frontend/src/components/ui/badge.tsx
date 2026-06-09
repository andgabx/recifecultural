import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";

import { cn } from "@/lib/utils";

const badgeVariants = cva(
  "inline-flex items-center justify-center rounded-full border px-2.5 py-0.5 text-xs font-bold tracking-[0.07em] uppercase w-fit whitespace-nowrap shrink-0 transition-colors",
  {
    variants: {
      variant: {
        default:     "bg-azul-fill text-azul border-azul/20",
        secondary:   "bg-muted text-muted-foreground border-border",
        accent:      "bg-laranja-fill text-laranja border-laranja/20",
        violeta:     "bg-violeta text-violeta-dark border-violeta-dark/15",
        success:     "bg-verde-fill text-verde border-verde/20",
        warning:     "bg-amber text-amber-dark border-amber-dark/15 animate-pulse",
        destructive: "bg-laranja-fill text-destructive border-destructive/20",
        outline:     "bg-transparent text-foreground border-border",
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
