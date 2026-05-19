import { httpClient } from "./httpClient";
import type {
  AccountOperationSummaryResponse,
  AccountResponse,
  AuthResponse,
  HealthResponse,
  MetricResponse,
  MetricsIndexResponse,
  OpenApiResponse,
  OutboxEventResponse,
  PendingTransactionResponse,
  TransactionOperationSummaryResponse,
  TransactionResponse,
  UserResponse
} from "../types/api";

export const authApi = {
  register: (body: { name: string; email: string; password: string }) =>
    httpClient.post<UserResponse>("/api/v1/auth/register", body).then((response) => response.data),
  login: (body: { email: string; password: string }) =>
    httpClient.post<AuthResponse>("/api/v1/auth/login", body).then((response) => response.data),
  refresh: (refreshToken: string) =>
    httpClient.post<AuthResponse>("/api/v1/auth/refresh-token", { refreshToken }).then((response) => response.data)
};

export const userApi = {
  me: () => httpClient.get<UserResponse>("/api/v1/me").then((response) => response.data),
  findById: (id: string) => httpClient.get<UserResponse>(`/api/v1/users/${id}`).then((response) => response.data),
  findByEmail: (email: string) =>
    httpClient.get<UserResponse>("/api/v1/users", { params: { email } }).then((response) => response.data)
};

export const accountApi = {
  create: () => httpClient.post<AccountResponse>("/api/v1/accounts").then((response) => response.data),
  mine: () => httpClient.get<AccountResponse>("/api/v1/accounts/me").then((response) => response.data),
  findById: (id: string) => httpClient.get<AccountResponse>(`/api/v1/accounts/${id}`).then((response) => response.data),
  credit: (id: string, amount: number) =>
    httpClient.post<AccountResponse>(`/api/v1/accounts/${id}/credit`, { amount }).then((response) => response.data),
  debit: (id: string, amount: number) =>
    httpClient.post<AccountResponse>(`/api/v1/accounts/${id}/debit`, { amount }).then((response) => response.data)
};

export const transactionApi = {
  transfer: (body: { sourceAccountId: string; destinationAccountId: string; amount: number }) =>
    httpClient.post<TransactionResponse>("/api/v1/transactions/transfers", body).then((response) => response.data),
  findById: (id: string) => httpClient.get<TransactionResponse>(`/api/v1/transactions/${id}`).then((response) => response.data)
};

export const operationsApi = {
  transactionSummary: () =>
    httpClient
      .get<TransactionOperationSummaryResponse>("/api/v1/operations/transactions/summary")
      .then((response) => response.data),
  pendingTransactions: () =>
    httpClient
      .get<PendingTransactionResponse[]>("/api/v1/operations/transactions/pending")
      .then((response) => response.data),
  failedTransactionOutbox: () =>
    httpClient
      .get<OutboxEventResponse[]>("/api/v1/operations/transactions/outbox/failed")
      .then((response) => response.data),
  accountSummary: () =>
    httpClient
      .get<AccountOperationSummaryResponse>("/api/v1/operations/accounts/summary")
      .then((response) => response.data),
  failedAccountOutbox: () =>
    httpClient.get<OutboxEventResponse[]>("/api/v1/operations/accounts/outbox/failed").then((response) => response.data),
  metric: (path: string) => httpClient.get<MetricResponse>(path).then((response) => response.data),
  metricsIndex: (path: string) => httpClient.get<MetricsIndexResponse>(path).then((response) => response.data),
  health: () => httpClient.get<HealthResponse>("/actuator/health").then((response) => response.data),
  openApi: (path: string) => httpClient.get<OpenApiResponse>(path).then((response) => response.data)
};
