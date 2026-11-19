import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { Guitar } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { getApiError } from "../services/api";

export default function Login() {
  const navigate = useNavigate();
  const { user, loading, login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (!loading && user) return <Navigate to="/dashboard" replace />;

  function update(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await login({ email: form.email.trim(), password: form.password });
      navigate("/dashboard", { replace: true });
    } catch (requestError) {
      setError(getApiError(requestError));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card" aria-labelledby="login-title">
        <div className="brand-mark"><Guitar size={22} strokeWidth={2.2} /></div>
        <h1 id="login-title" className="auth-logo">GuitarCare</h1>
        <p className="auth-subtitle">Welcome back! Sign in to manage your guitars.</p>
        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input id="email" name="email" type="email" autoComplete="email" placeholder="Enter your email" value={form.email} onChange={update} required />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input id="password" name="password" type="password" autoComplete="current-password" placeholder="Enter your password" value={form.password} onChange={update} required />
          </div>
          {error && <p className="error-message" role="alert">{error}</p>}
          <button className="auth-button" type="submit" disabled={submitting || loading}>{submitting ? "Signing in…" : "Sign In"}</button>
        </form>
        <p className="auth-footer">Don't have an account? <Link to="/register">Create Account</Link></p>
      </section>
    </main>
  );
}
