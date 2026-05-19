import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { accountApi } from "../api/services";
import { extractApiError } from "../api/error";
import { KeyValueGrid } from "../components/DataView";
import { AsyncForm, Field } from "../components/Form";
import { ErrorState, LoadingState } from "../components/State";
import { StatusBadge } from "../components/StatusBadge";
import { dateTime, money } from "../utils/format";
import type { AccountResponse } from "../types/api";

function AccountDetails({ account }: { account: AccountResponse }) {
  return (
    <KeyValueGrid
      data={{
        Id: account.id,
        Usuario: account.userId,
        Numero: account.accountNumber,
        Saldo: money(account.balance),
        Status: <StatusBadge value={account.active} />,
        Criada: dateTime(account.createdAt),
        Atualizada: dateTime(account.updatedAt)
      }}
    />
  );
}

export function AccountsPage() {
  const queryClient = useQueryClient();
  const mineQuery = useQuery({ queryKey: ["account", "mine"], queryFn: accountApi.mine, retry: false });
  const [searchId, setSearchId] = useState("");
  const [operationAccountId, setOperationAccountId] = useState("");
  const [creditAmount, setCreditAmount] = useState("");
  const [debitAmount, setDebitAmount] = useState("");
  const [foundAccount, setFoundAccount] = useState<AccountResponse | null>(null);
  const [operationResult, setOperationResult] = useState<AccountResponse | null>(null);

  function refreshAccountQueries() {
    queryClient.invalidateQueries({ queryKey: ["account"] });
  }

  return (
    <section className="page">
      <div className="page-heading">
        <h1>Contas</h1>
        <p>Criacao, consulta e movimentacao de saldo.</p>
      </div>

      <div className="content-grid">
        <article className="panel">
          <h2>Minha conta</h2>
          {mineQuery.isLoading && <LoadingState />}
          {mineQuery.isError && <ErrorState message={extractApiError(mineQuery.error)} />}
          {mineQuery.data && <AccountDetails account={mineQuery.data} />}
        </article>

        <article className="panel">
          <h2>Criar conta</h2>
          <AsyncForm
            submitLabel="Criar conta"
            successMessage="Conta criada com sucesso."
            onSubmit={async () => {
              const account = await accountApi.create();
              setFoundAccount(account);
              refreshAccountQueries();
            }}
          >
            <p className="muted">A conta sera criada para o usuario autenticado.</p>
          </AsyncForm>
        </article>

        <article className="panel">
          <h2>Buscar conta</h2>
          <AsyncForm
            submitLabel="Buscar conta"
            onSubmit={async () => {
              setFoundAccount(await accountApi.findById(searchId));
            }}
          >
            <Field label="Account id" value={searchId} onChange={setSearchId} placeholder="UUID da conta" />
          </AsyncForm>
        </article>
      </div>

      {foundAccount && (
        <article className="panel">
          <h2>Conta encontrada</h2>
          <AccountDetails account={foundAccount} />
        </article>
      )}

      <div className="content-grid two">
        <article className="panel">
          <h2>Creditar saldo</h2>
          <AsyncForm
            submitLabel="Creditar"
            successMessage="Credito realizado."
            onSubmit={async () => {
              const account = await accountApi.credit(operationAccountId, Number(creditAmount));
              setOperationResult(account);
              refreshAccountQueries();
            }}
          >
            <Field label="Account id" value={operationAccountId} onChange={setOperationAccountId} placeholder="UUID da conta" />
            <Field label="Valor" value={creditAmount} onChange={setCreditAmount} type="number" min="0.01" step="0.01" />
          </AsyncForm>
        </article>

        <article className="panel">
          <h2>Debitar saldo</h2>
          <AsyncForm
            submitLabel="Debitar"
            successMessage="Debito realizado."
            onSubmit={async () => {
              const account = await accountApi.debit(operationAccountId, Number(debitAmount));
              setOperationResult(account);
              refreshAccountQueries();
            }}
          >
            <Field label="Account id" value={operationAccountId} onChange={setOperationAccountId} placeholder="UUID da conta" />
            <Field label="Valor" value={debitAmount} onChange={setDebitAmount} type="number" min="0.01" step="0.01" />
          </AsyncForm>
        </article>
      </div>

      {operationResult && (
        <article className="panel">
          <h2>Resultado da operacao</h2>
          <AccountDetails account={operationResult} />
        </article>
      )}
    </section>
  );
}
