import { useState } from "react";
import { transactionApi } from "../api/services";
import { KeyValueGrid } from "../components/DataView";
import { AsyncForm, Field } from "../components/Form";
import { StatusBadge } from "../components/StatusBadge";
import { dateTime, money } from "../utils/format";
import type { TransactionResponse } from "../types/api";

function TransactionDetails({ transaction }: { transaction: TransactionResponse }) {
  return (
    <KeyValueGrid
      data={{
        Id: transaction.id,
        Solicitante: transaction.requesterUserId,
        Origem: transaction.sourceAccountId,
        Destino: transaction.destinationAccountId,
        Valor: money(transaction.amount),
        Tipo: transaction.type,
        Status: <StatusBadge value={transaction.status} />,
        Falha: transaction.failureReason ?? "-",
        Criada: dateTime(transaction.createdAt),
        Atualizada: dateTime(transaction.updatedAt)
      }}
    />
  );
}

export function TransactionsPage() {
  const [sourceAccountId, setSourceAccountId] = useState("");
  const [destinationAccountId, setDestinationAccountId] = useState("");
  const [amount, setAmount] = useState("");
  const [transactionId, setTransactionId] = useState("");
  const [result, setResult] = useState<TransactionResponse | null>(null);

  return (
    <section className="page">
      <div className="page-heading">
        <h1>Transferencias</h1>
        <p>Solicite transferencias e consulte o status de processamento.</p>
      </div>

      <div className="content-grid two">
        <article className="panel">
          <h2>Solicitar transferencia</h2>
          <AsyncForm
            submitLabel="Solicitar"
            successMessage="Transferencia solicitada."
            onSubmit={async () => {
              setResult(
                await transactionApi.transfer({
                  sourceAccountId,
                  destinationAccountId,
                  amount: Number(amount)
                })
              );
            }}
          >
            <Field label="Conta origem" value={sourceAccountId} onChange={setSourceAccountId} placeholder="UUID da conta origem" />
            <Field
              label="Conta destino"
              value={destinationAccountId}
              onChange={setDestinationAccountId}
              placeholder="UUID da conta destino"
            />
            <Field label="Valor" value={amount} onChange={setAmount} type="number" min="0.01" step="0.01" />
          </AsyncForm>
        </article>

        <article className="panel">
          <h2>Consultar transacao</h2>
          <AsyncForm
            submitLabel="Consultar"
            onSubmit={async () => {
              setResult(await transactionApi.findById(transactionId));
            }}
          >
            <Field label="Transaction id" value={transactionId} onChange={setTransactionId} placeholder="UUID da transacao" />
          </AsyncForm>
        </article>
      </div>

      {result && (
        <article className="panel">
          <h2>Resultado</h2>
          <TransactionDetails transaction={result} />
        </article>
      )}
    </section>
  );
}
