import { useQuery } from "@tanstack/react-query";
import { accountApi, operationsApi, userApi } from "../api/services";
import { ErrorState, LoadingState } from "../components/State";
import { money } from "../utils/format";
import { extractApiError } from "../api/error";

export function DashboardPage() {
  const meQuery = useQuery({ queryKey: ["me"], queryFn: userApi.me });
  const accountQuery = useQuery({ queryKey: ["account", "mine"], queryFn: accountApi.mine, retry: false });
  const transactionSummaryQuery = useQuery({
    queryKey: ["operations", "transactions", "summary"],
    queryFn: operationsApi.transactionSummary,
    retry: false
  });

  return (
    <section className="page">
      <div className="page-heading">
        <h1>Dashboard</h1>
        <p>Resumo rapido do usuario, conta e fluxo financeiro.</p>
      </div>

      {meQuery.isLoading && <LoadingState />}
      {meQuery.isError && <ErrorState message={extractApiError(meQuery.error)} />}

      <div className="stats-grid">
        <article className="stat">
          <span>Usuario</span>
          <strong>{meQuery.data?.name ?? "-"}</strong>
          <small>{meQuery.data?.email ?? "-"}</small>
        </article>
        <article className="stat">
          <span>Saldo</span>
          <strong>{accountQuery.data ? money(accountQuery.data.balance) : "-"}</strong>
          <small>{accountQuery.data?.accountNumber ?? "Conta nao carregada"}</small>
        </article>
        <article className="stat">
          <span>Transacoes pendentes</span>
          <strong>{transactionSummaryQuery.data?.pendingTransactions ?? "-"}</strong>
          <small>via transaction-service</small>
        </article>
        <article className="stat">
          <span>Outbox com falha</span>
          <strong>{transactionSummaryQuery.data?.failedOutboxEvents ?? "-"}</strong>
          <small>eventos de transacao</small>
        </article>
      </div>
    </section>
  );
}
