export type Role = "USER" | "ADMIN";

export type FieldErrorResponse = {
  field: string;
  message: string;
};

export type ErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  fields?: FieldErrorResponse[];
};

export type AuthResponse = {
  userId: string;
  accessToken: string;
  refreshToken: string;
  refreshTokenExpiresAt: string;
  tokenType: string;
};

export type UserResponse = {
  id: string;
  name: string;
  email: string;
  roles: Role[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export type AccountResponse = {
  id: string;
  userId: string;
  accountNumber: string;
  balance: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export type TransactionStatus = "PENDING" | "COMPLETED" | "FAILED";
export type TransactionType = "TRANSFER";

export type TransactionResponse = {
  id: string;
  requesterUserId: string;
  sourceAccountId: string;
  destinationAccountId: string;
  amount: number;
  type: TransactionType;
  status: TransactionStatus;
  failureReason: string | null;
  createdAt: string;
  updatedAt: string;
};

export type PendingTransactionResponse = {
  id: string;
  sourceAccountId: string;
  destinationAccountId: string;
  amount: number;
  createdAt: string;
};

export type OutboxEventResponse = {
  id: string;
  eventType: string;
  topic: string;
  messageKey: string;
  status: string;
  attempts: number;
  lastError: string | null;
  createdAt: string;
};

export type TransactionOperationSummaryResponse = {
  pendingTransactions: number;
  failedTransactions: number;
  pendingOutboxEvents: number;
  failedOutboxEvents: number;
  dlqMessages: Record<string, number>;
};

export type AccountOperationSummaryResponse = {
  completedTransfers: number;
  failedTransfers: number;
  pendingOutboxEvents: number;
  failedOutboxEvents: number;
  dlqMessages: Record<string, number>;
};

export type MetricResponse = {
  name: string;
  description?: string;
  baseUnit?: string;
  measurements?: Array<{
    statistic: string;
    value: number;
  }>;
  availableTags?: Array<{
    tag: string;
    values: string[];
  }>;
};

export type HealthResponse = {
  status: string;
};

export type MetricsIndexResponse = {
  names: string[];
};

export type OpenApiResponse = {
  openapi?: string;
  info?: {
    title?: string;
    version?: string;
  };
  paths?: Record<string, unknown>;
};
