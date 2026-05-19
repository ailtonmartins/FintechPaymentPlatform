import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AsyncForm, Field } from "../components/Form";
import { useAuth } from "../auth/AuthProvider";

export function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { register } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="auth-page">
      <section className="auth-panel">
        <h1>Cadastrar</h1>
        <p className="muted">Crie um usuario com role USER.</p>
        <AsyncForm
          submitLabel="Cadastrar"
          onSubmit={async () => {
            await register({ name, email, password });
            navigate("/login", { replace: true });
          }}
        >
          <Field label="Nome" value={name} onChange={setName} placeholder="Ailton Martins" />
          <Field label="E-mail" value={email} onChange={setEmail} type="email" placeholder="ailton@email.com" />
          <Field label="Senha" value={password} onChange={setPassword} type="password" placeholder="Minimo 6 caracteres" />
        </AsyncForm>
        <p className="auth-link">
          Ja possui conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </div>
  );
}
