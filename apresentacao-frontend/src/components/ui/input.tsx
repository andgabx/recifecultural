import * as React from "react";

import { cn } from "@/lib/utils";

const Input = React.forwardRef<HTMLInputElement, React.ComponentProps<"input">>(
  ({ className, type, ...props }, ref) => {
    return (
      <input
        ref={ref}
        type={type}
        data-slot="input"
        className={cn(
          "border-border bg-marquee-card text-foreground placeholder:text-muted-foreground flex h-10 w-full rounded-lg border px-3 py-2 text-sm shadow-sm transition-colors",
          "focus-visible:border-vinho focus-visible:ring-vinho/30 focus-visible:outline-none focus-visible:ring-2",
          "disabled:cursor-not-allowed disabled:opacity-50",
          "file:text-foreground file:border-0 file:bg-transparent file:text-sm file:font-medium",
          "aria-invalid:border-destructive aria-invalid:ring-destructive/30",
          className,
        )}
        {...props}
      />
    );
  },
);
Input.displayName = "Input";

export { Input };
