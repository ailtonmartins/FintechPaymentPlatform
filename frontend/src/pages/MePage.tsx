import { useQuery } from "@tanstack/react-query";
import { userApi } from "../api/services";
import { extractApiError } from "../api/error";
import { KeyValueGrid } from "../components/DataView";
import { AsyncForm, Field } from "../components/Form";
import { ErrorState, LoadingState } from "../components/State";
import { StatusBadge } from "../components/StatusBadge";
import { dateTime } from "../utils/format";
import { useState } from "react";
import type { UserResponse } from "../types/api";

export function MePage() {
  const meQuery = useQuery({ queryKey: ["me"], queryFn: userApi.me });
  const [userId, setUserId] = useState("");
  const [email, setEmail] = useState("");
  const [result, setResult] = useState<UserResponse | null>(null);

  return (
    <section className="page">
      <div className="page-heading">
        <h1>Usuario</h1>
        <p>Perfil autenticado e consultas administrativas.</p>
      </div>

      <div className="content-grid">
        <article className="panel">
          <h2>Meu usuario</h2>
          {meQuery.isLoading && <LoadingState />}
          {meQuery.isError && <ErrorState message={extractApiError(meQuery.error)} />}
          {meQuery.data && (
            <KeyValueGrid
              data={{
                Id: meQuery.data.id,
                Nome: meQuery.data.name,
                Email: meQuery.data.email,
                Roles: meQuery.data.roles.join(", "),
                Status: <StatusBadge value={meQuery.data.active} />,
                Criado: dateTime(meQuery.data.createdAt),
                Atualizado: dateTime(meQuery.data.updatedAt)
              }}
            />
          )}
        </article>

        <article className="panel">
          <h2>Buscar por id</h2>
          <AsyncForm
            submitLabel="Buscar usuario"
            onSubmit={async () => {
              setResult(await userApi.findById(userId));
            }}
          >
            <Field label="User id" value={userId} onChange={setUserId} placeholder="UUID do usuario" />
          </AsyncForm>
        </article>

        <article className="panel">
          <h2>Buscar por e-mail</h2>
          <AsyncForm
            submitLabel="Buscar e-mail"
            onSubmit={async () => {
              setResult(await userApi.findByEmail(email));
            }}
          >
            <Field label="E-mail" value={email} onChange={setEmail} type="email" placeholder="usuario@email.com" />
          </AsyncForm>
        </article>
      </div>

      {result && (
        <article className="panel">
          <h2>Resultado administrativo</h2>
          <KeyValueGrid
            data={{
              Id: result.id,
              Nome: result.name,
              Email: result.email,
              Roles: result.roles.join(", "),
              Status: <StatusBadge value={result.active} />,
              Criado: dateTime(result.createdAt)
            }}
          />
        </article>
      )}
    </section>
  );
}
