export function StatusBadge({ value }: { value: string | boolean }) {
  const label = typeof value === "boolean" ? (value ? "Ativo" : "Inativo") : value;
  const normalized = String(label).toLowerCase();
  return <span className={`status ${normalized}`}>{label}</span>;
}
