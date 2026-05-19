import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AsyncForm, Field } from "../components/Form";
import { useAuth } from "../auth/AuthProvider";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="auth-page">
      <section className="auth-panel">
        <h1>Entrar</h1>
        <p className="muted">Acesse a plataforma pelo API Gateway.</p>
        <AsyncForm
          submitLabel="Entrar"
          onSubmit={async () => {
            await login(email, password);
            navigate("/dashboard", { replace: true });
          }}
        >
          <Field label="E-mail" value={email} onChange={setEmail} type="email" placeholder="ailton@email.com" />
          <Field label="Senha" value={password} onChange={setPassword} type="password" placeholder="123456" />
        </AsyncForm>
        <p className="auth-link">
          Nao tem conta? <Link to="/register">Cadastrar usuario</Link>
        </p>
      </section>
    </div>
  );
}
