"use client";

import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { useState, type ReactNode } from "react";
import { Toaster } from "sonner";

import { createQueryClient } from "@/lib/queryClient";
import { RoleProvider } from "@/lib/role";

export function Providers({ children }: { children: ReactNode }) {
  const [queryClient] = useState(createQueryClient);

  return (
    <QueryClientProvider client={queryClient}>
      <RoleProvider>{children}</RoleProvider>
      <Toaster
        position="top-right"
        toastOptions={{
          classNames: {
            toast: "border border-border shadow-card",
            success: "border-l-4 border-l-emerald-500",
            error: "border-l-4 border-l-destructive",
            info: "border-l-4 border-l-vinho",
          },
        }}
      />
      {process.env.NODE_ENV === "development" && (
        <ReactQueryDevtools initialIsOpen={false} buttonPosition="bottom-left" />
      )}
    </QueryClientProvider>
  );
}
