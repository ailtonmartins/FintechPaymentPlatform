import { useQuery } from "@tanstack/react-query";
import { operationsApi } from "../api/services";
import { extractApiError } from "../api/error";
import { KeyValueGrid, SimpleTable } from "../components/DataView";
import { ErrorState, LoadingState } from "../components/State";
import { dateTime, money, shortId } from "../utils/format";
import type { MetricResponse, OpenApiResponse, OutboxEventResponse, PendingTransactionResponse } from "../types/api";

const metricPaths = [
  "/transaction-service/actuator/metrics/financial.transactions.pending",
  "/transaction-service/actuator/metrics/financial.transactions.failed",
  "/transaction-service/actuator/metrics/financial.outbox.pending",
  "/transaction-service/actuator/metrics/financial.outbox.failed",
  "/account-service/actuator/metrics/financial.transfers.completed",
  "/account-service/actuator/metrics/financial.transfers.failed",
  "/account-service/actuator/metrics/financial.outbox.pending",
  "/account-service/actuator/metrics/financial.outbox.failed"
];

const metricsIndexPaths = ["/transaction-service/actuator/metrics", "/account-service/actuator/metrics"];
const openApiPaths = ["/user-service/v3/api-docs", "/account-service/v3/api-docs", "/transaction-service/v3/api-docs"];

function dlqData(messages?: Record<string, number>) {
  if (!messages || Object.keys(messages).length === 0) {
    return { DLQ: "0" };
  }

  return Object.fromEntries(Object.entries(messages).map(([topic, count]) => [topic, count]));
}

function metricValue(metric: MetricResponse) {
  const measurement = metric.measurements?.[0];
  if (!measurement) {
    return "-";
  }

  return `${measurement.value} (${measurement.statistic})`;
}

function openApiTitle(document: OpenApiResponse) {
  const pathCount = Object.keys(document.paths ?? {}).length;
  return `${document.info?.title ?? "OpenAPI"} (${pathCount} rotas)`;
}

export function OperationsPage() {
  const transactionSummaryQuery = useQuery({
    queryKey: ["operations", "transactions", "summary"],
    queryFn: operationsApi.transactionSummary
  });
  const pendingTransactionsQuery = useQuery({
    queryKey: ["operations", "transactions", "pending"],
    queryFn: operationsApi.pendingTransactions
  });
  const failedTransactionOutboxQuery = useQuery({
    queryKey: ["operations", "transactions", "outbox", "failed"],
    queryFn: operationsApi.failedTransactionOutbox
  });
  const accountSummaryQuery = useQuery({
    queryKey: ["operations", "accounts", "summary"],
    queryFn: operationsApi.accountSummary
  });
  const failedAccountOutboxQuery = useQuery({
    queryKey: ["operations", "accounts", "outbox", "failed"],
    queryFn: operationsApi.failedAccountOutbox
  });
  const metricsQuery = useQuery({
    queryKey: ["operations", "metrics"],
    queryFn: async () => Promise.allSettled(metricPaths.map((path) => operationsApi.metric(path))),
    retry: false
  });
  const metricsIndexQuery = useQuery({
    queryKey: ["operations", "metrics", "index"],
    queryFn: async () => Promise.allSettled(metricsIndexPaths.map((path) => operationsApi.metricsIndex(path))),
    retry: false
  });
  const healthQuery = useQuery({
    queryKey: ["operations", "gateway", "health"],
    queryFn: operationsApi.health,
    retry: false
  });
  const openApiQuery = useQuery({
    queryKey: ["operations", "openapi"],
    queryFn: async () => Promise.allSettled(openApiPaths.map((path) => operationsApi.openApi(path))),
    retry: false
  });

  const metrics =
    metricsQuery.data
      ?.filter((result): result is PromiseFulfilledResult<MetricResponse> => result.status === "fulfilled")
      .map((result) => result.value) ?? [];
  const metricIndexes =
    metricsIndexQuery.data
      ?.filter((result): result is PromiseFulfilledResult<{ names: string[] }> => result.status === "fulfilled")
      .map((result, index) => ({
        service: metricsIndexPaths[index].split("/")[1],
        total: result.value.names.length
      })) ?? [];
  const openApiDocuments =
    openApiQuery.data
      ?.filter((result): result is PromiseFulfilledResult<OpenApiResponse> => result.status === "fulfilled")
      .map((result, index) => ({
        service: openApiPaths[index].split("/")[1],
        title: openApiTitle(result.value)
      })) ?? [];

  return (
    <section className="page">
      <div className="page-heading">
        <h1>Operacional</h1>
        <p>Resumo de transacoes, contas, Outbox, DLQ e metricas.</p>
      </div>

      <div className="content-grid two">
        <article className="panel">
          <h2>Resumo de transacoes</h2>
          {transactionSummaryQuery.isLoading && <LoadingState />}
          {transactionSummaryQuery.isError && <ErrorState message={extractApiError(transactionSummaryQuery.error)} />}
          {transactionSummaryQuery.data && (
            <KeyValueGrid
              data={{
                Pendentes: transactionSummaryQuery.data.pendingTransactions,
                Falhadas: transactionSummaryQuery.data.failedTransactions,
                "Outbox pendente": transactionSummaryQuery.data.pendingOutboxEvents,
                "Outbox falha": transactionSummaryQuery.data.failedOutboxEvents,
                ...dlqData(transactionSummaryQuery.data.dlqMessages)
              }}
            />
          )}
        </article>

        <article className="panel">
          <h2>Resumo de contas</h2>
          {accountSummaryQuery.isLoading && <LoadingState />}
          {accountSummaryQuery.isError && <ErrorState message={extractApiError(accountSummaryQuery.error)} />}
          {accountSummaryQuery.data && (
            <KeyValueGrid
              data={{
                "Transferencias concluidas": accountSummaryQuery.data.completedTransfers,
                "Transferencias falhadas": accountSummaryQuery.data.failedTransfers,
                "Outbox pendente": accountSummaryQuery.data.pendingOutboxEvents,
                "Outbox falha": accountSummaryQuery.data.failedOutboxEvents,
                ...dlqData(accountSummaryQuery.data.dlqMessages)
              }}
            />
          )}
        </article>
      </div>

      <div className="content-grid two">
        <article className="panel">
          <h2>Gateway</h2>
          {healthQuery.isLoading && <LoadingState />}
          {healthQuery.isError && <ErrorState message={extractApiError(healthQuery.error)} />}
          {healthQuery.data && <KeyValueGrid data={{ Health: healthQuery.data.status }} />}
        </article>

        <article className="panel">
          <h2>OpenAPI</h2>
          {openApiQuery.isLoading && <LoadingState />}
          {openApiQuery.isError && <ErrorState message={extractApiError(openApiQuery.error)} />}
          {openApiQuery.data && (
            <SimpleTable<{ service: string; title: string }>
              rows={openApiDocuments}
              columns={[
                { key: "service", label: "Servico", render: (row) => row.service },
                { key: "title", label: "Documento", render: (row) => row.title }
              ]}
            />
          )}
        </article>
      </div>

      <article className="panel">
        <h2>Transacoes pendentes</h2>
        {pendingTransactionsQuery.isLoading && <LoadingState />}
        {pendingTransactionsQuery.isError && <ErrorState message={extractApiError(pendingTransactionsQuery.error)} />}
        {pendingTransactionsQuery.data && (
          <SimpleTable<PendingTransactionResponse>
            rows={pendingTransactionsQuery.data}
            columns={[
              { key: "id", label: "Id", render: (row) => shortId(row.id) },
              { key: "source", label: "Origem", render: (row) => shortId(row.sourceAccountId) },
              { key: "destination", label: "Destino", render: (row) => shortId(row.destinationAccountId) },
              { key: "amount", label: "Valor", render: (row) => money(row.amount) },
              { key: "createdAt", label: "Criada", render: (row) => dateTime(row.createdAt) }
            ]}
          />
        )}
      </article>

      <div className="content-grid two">
        <OutboxPanel
          title="Outbox falha - transacoes"
          isLoading={failedTransactionOutboxQuery.isLoading}
          error={failedTransactionOutboxQuery.error}
          rows={failedTransactionOutboxQuery.data}
        />
        <OutboxPanel
          title="Outbox falha - contas"
          isLoading={failedAccountOutboxQuery.isLoading}
          error={failedAccountOutboxQuery.error}
          rows={failedAccountOutboxQuery.data}
        />
      </div>

      <article className="panel">
        <h2>Metricas</h2>
        {metricsQuery.isLoading && <LoadingState />}
        {metricsQuery.isError && <ErrorState message={extractApiError(metricsQuery.error)} />}
        {metricsQuery.data && (
          <SimpleTable<MetricResponse>
            rows={metrics}
            columns={[
              { key: "name", label: "Metrica", render: (row) => row.name },
              { key: "description", label: "Descricao", render: (row) => row.description ?? "-" },
              { key: "value", label: "Valor", render: (row) => metricValue(row) }
            ]}
          />
        )}
      </article>

      <article className="panel">
        <h2>Indice de metricas</h2>
        {metricsIndexQuery.isLoading && <LoadingState />}
        {metricsIndexQuery.isError && <ErrorState message={extractApiError(metricsIndexQuery.error)} />}
        {metricsIndexQuery.data && (
          <SimpleTable<{ service: string; total: number }>
            rows={metricIndexes}
            columns={[
              { key: "service", label: "Servico", render: (row) => row.service },
              { key: "total", label: "Metricas disponiveis", render: (row) => row.total }
            ]}
          />
        )}
      </article>
    </section>
  );
}

function OutboxPanel({
  title,
  rows,
  isLoading,
  error
}: {
  title: string;
  rows?: OutboxEventResponse[];
  isLoading: boolean;
  error: Error | null;
}) {
  return (
    <article className="panel">
      <h2>{title}</h2>
      {isLoading && <LoadingState />}
      {error && <ErrorState message={extractApiError(error)} />}
      {rows && (
        <SimpleTable<OutboxEventResponse>
          rows={rows}
          columns={[
            { key: "id", label: "Id", render: (row) => shortId(row.id) },
            { key: "eventType", label: "Evento", render: (row) => row.eventType },
            { key: "topic", label: "Topico", render: (row) => row.topic },
            { key: "attempts", label: "Tentativas", render: (row) => row.attempts },
            { key: "lastError", label: "Erro", render: (row) => row.lastError ?? "-" },
            { key: "createdAt", label: "Criado", render: (row) => dateTime(row.createdAt) }
          ]}
        />
      )}
    </article>
  );
}
