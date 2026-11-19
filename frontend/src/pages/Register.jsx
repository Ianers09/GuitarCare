import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { Guitar } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { getApiError } from "../services/api";

export default function Register() {
  const navigate = useNavigate();
  const { user, loading, register, login } = useAuth();
  const [form, setForm] = useState({ fullName: "", email: "", password: "", confirmPassword: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (!loading && user) return <Navigate to="/dashboard" replace />;

  function update(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    if (form.password.length < 8) return setError("Password must contain at least 8 characters.");
    if (form.password !== form.confirmPassword) return setError("Passwords do not match.");
    setSubmitting(true);
    try {
      await register({ fullName: form.fullName.trim(), email: form.email.trim(), password: form.password });
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
      <section className="auth-card" aria-labelledby="register-title">
        <div className="brand-mark"><Guitar size={22} strokeWidth={2.2} /></div>
        <h1 id="register-title" className="auth-logo">Create your account</h1>
        <p className="auth-subtitle">Start managing your guitars with GuitarCare.</p>
        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="fullName">Full Name</label>
            <input id="fullName" name="fullName" type="text" autoComplete="name" placeholder="Enter your full name" value={form.fullName} onChange={update} maxLength={120} required />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input id="email" name="email" type="email" autoComplete="email" placeholder="Enter your email" value={form.email} onChange={update} required />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input id="password" name="password" type="password" autoComplete="new-password" placeholder="At least 8 characters" value={form.password} onChange={update} minLength={8} required />
          </div>
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input id="confirmPassword" name="confirmPassword" type="password" autoComplete="new-password" placeholder="Confirm your password" value={form.confirmPassword} onChange={update} required />
          </div>
          {error && <p className="error-message" role="alert">{error}</p>}
          <button className="auth-button" type="submit" disabled={submitting || loading}>{submitting ? "Creating account…" : "Create Account"}</button>
        </form>
        <p className="auth-footer">Already have an account? <Link to="/login">Sign In</Link></p>
      </section>
    </main>
  );
}
