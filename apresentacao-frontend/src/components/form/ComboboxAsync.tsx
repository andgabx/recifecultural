"use client";

import { useQuery, type QueryKey } from "@tanstack/react-query";
import { forwardRef } from "react";
import { Loader2 } from "lucide-react";

import { Select } from "@/components/ui/select";
import { cn } from "@/lib/utils";

type ComboboxAsyncProps<T> = {
  id?: string;
  value: string;
  onChange: (value: string) => void;
  queryKey: QueryKey;
  queryFn: () => Promise<T[]>;
  toOption: (item: T) => { value: string; label: string; hint?: string };
  onItemChange?: (item: T | undefined) => void;
  enabled?: boolean;
  placeholder?: string;
  emptyMessage?: string;
  className?: string;
};

function ComboboxAsyncInner<T>(
  {
    id,
    value,
    onChange,
    queryKey,
    queryFn,
    toOption,
    onItemChange,
    enabled = true,
    placeholder = "Selecione…",
    emptyMessage = "Nenhuma opção encontrada",
    className,
  }: ComboboxAsyncProps<T>,
  ref: React.Ref<HTMLSelectElement>,
) {
  const { data, isLoading } = useQuery({
    queryKey,
    queryFn,
    enabled,
  });

  const options = data?.map((item) => ({ ...toOption(item), item })) ?? [];

  return (
    <div className={cn("relative", className)}>
      <Select
        id={id}
        ref={ref}
        value={value}
        onChange={(e) => {
          const newValue = e.target.value;
          onChange(newValue);
          onItemChange?.(options.find((opt) => opt.value === newValue)?.item);
        }}
        disabled={!enabled || isLoading}
      >
        <option value="">
          {!enabled
            ? "Selecione um pré-requisito primeiro"
            : isLoading
              ? "Carregando…"
              : options.length === 0
                ? emptyMessage
                : placeholder}
        </option>
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
            {opt.hint ? `  ·  ${opt.hint}` : ""}
          </option>
        ))}
      </Select>
      {isLoading && (
        <Loader2 className="text-azul/60 absolute right-9 top-1/2 h-3.5 w-3.5 -translate-y-1/2 animate-spin" />
      )}
    </div>
  );
}

export const ComboboxAsync = forwardRef(ComboboxAsyncInner) as <T>(
  props: ComboboxAsyncProps<T> & { ref?: React.Ref<HTMLSelectElement> },
) => ReturnType<typeof ComboboxAsyncInner>;
