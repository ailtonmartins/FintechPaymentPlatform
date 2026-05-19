import { FormEvent, ReactNode, useState } from "react";
import { extractApiError } from "../api/error";

type AsyncFormProps = {
  children: ReactNode;
  submitLabel: string;
  onSubmit: () => Promise<void>;
  successMessage?: string;
};

export function AsyncForm({ children, submitLabel, onSubmit, successMessage }: AsyncFormProps) {
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setSuccess(null);
    setSubmitting(true);

    try {
      await onSubmit();
      if (successMessage) {
        setSuccess(successMessage);
      }
    } catch (requestError) {
      setError(extractApiError(requestError));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      {children}
      {error && <p className="form-message error">{error}</p>}
      {success && <p className="form-message success">{success}</p>}
      <button className="button primary" type="submit" disabled={submitting}>
        {submitting ? "Processando..." : submitLabel}
      </button>
    </form>
  );
}

type FieldProps = {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  placeholder?: string;
  required?: boolean;
  min?: string;
  step?: string;
};

export function Field({ label, value, onChange, type = "text", placeholder, required = true, min, step }: FieldProps) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        value={value}
        onChange={(event) => onChange(event.target.value)}
        type={type}
        placeholder={placeholder}
        required={required}
        min={min}
        step={step}
      />
    </label>
  );
}
