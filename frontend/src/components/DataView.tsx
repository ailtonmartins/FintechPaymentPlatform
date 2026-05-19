import { ReactNode } from "react";
import { EmptyState } from "./State";

export function KeyValueGrid({ data }: { data: Record<string, ReactNode> }) {
  return (
    <dl className="kv-grid">
      {Object.entries(data).map(([key, value]) => (
        <div key={key}>
          <dt>{key}</dt>
          <dd>{value ?? "-"}</dd>
        </div>
      ))}
    </dl>
  );
}

export function SimpleTable<T extends { id?: string } | Record<string, unknown>>({
  rows,
  columns
}: {
  rows: T[];
  columns: Array<{ key: string; label: string; render: (row: T) => ReactNode }>;
}) {
  if (rows.length === 0) {
    return <EmptyState />;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={String("id" in row && row.id ? row.id : index)}>
              {columns.map((column) => (
                <td key={column.key}>{column.render(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
