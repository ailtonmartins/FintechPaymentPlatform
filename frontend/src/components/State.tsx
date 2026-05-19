import { ReactNode } from "react";

export function LoadingState() {
  return <p className="muted">Carregando...</p>;
}

export function ErrorState({ message }: { message: string }) {
  return <p className="form-message error">{message}</p>;
}

export function EmptyState({ children = "Nenhum registro encontrado." }: { children?: ReactNode }) {
  return <p className="muted">{children}</p>;
}
