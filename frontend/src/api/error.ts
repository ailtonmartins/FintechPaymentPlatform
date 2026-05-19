import axios from "axios";
import type { ErrorResponse } from "../types/api";

export function extractApiError(error: unknown): string {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    const data = error.response?.data;
    const fieldMessages = data?.fields?.map((field) => `${field.field}: ${field.message}`) ?? [];

    if (fieldMessages.length > 0) {
      return fieldMessages.join(" | ");
    }

    if (data?.message) {
      return data.message;
    }

    if (error.response?.status === 401) {
      return "Sessao expirada ou token invalido.";
    }

    if (error.response?.status === 403) {
      return "Voce nao tem permissao para acessar este recurso.";
    }
  }

  if (error instanceof Error && error.message) {
    return error.message;
  }

  return "Nao foi possivel concluir a operacao.";
}
