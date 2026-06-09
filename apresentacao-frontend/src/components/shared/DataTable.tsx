import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

export type Coluna<T> = {
  header: ReactNode;
  cell: (row: T) => ReactNode;
  className?: string;
  width?: string;
};

type DataTableProps<T> = {
  data: T[];
  columns: Coluna<T>[];
  rowKey: (row: T) => string;
  empty?: ReactNode;
  className?: string;
};

export function DataTable<T>({
  data,
  columns,
  rowKey,
  empty,
  className,
}: DataTableProps<T>) {
  if (data.length === 0 && empty) {
    return <div>{empty}</div>;
  }
  return (
    <div className={cn("overflow-x-auto rounded-xl border border-border", className)}>
      <table className="w-full text-sm">
        <thead className="bg-nevoa-muted/60 border-b border-border">
          <tr>
            {columns.map((col, i) => (
              <th
                key={i}
                style={{ width: col.width }}
                className={cn(
                  "text-muted-foreground px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide",
                  col.className,
                )}
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-border bg-card">
          {data.map((row) => (
            <tr
              key={rowKey(row)}
              className="hover:bg-nevoa-muted/30 transition-colors"
            >
              {columns.map((col, i) => (
                <td
                  key={i}
                  className={cn("text-foreground px-4 py-3", col.className)}
                >
                  {col.cell(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
