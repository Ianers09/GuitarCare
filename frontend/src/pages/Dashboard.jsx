import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { CalendarDays, Guitar, LogOut, ShieldCheck } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [signingOut, setSigningOut] = useState(false);

  async function handleLogout() {
    setSigningOut(true);
    try { await logout(); } finally { navigate("/login", { replace: true }); }
  }

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <a className="dashboard-brand" href="/dashboard"><span className="brand-mark small"><Guitar size={19} /></span> GuitarCare</a>
        <button className="logout-button" onClick={handleLogout} disabled={signingOut}><LogOut size={16} /> {signingOut ? "Signing out…" : "Sign out"}</button>
      </header>
      <section className="dashboard-content">
        <p className="eyebrow">YOUR WORKSPACE</p>
        <h1>Welcome, {user?.fullName || "guitarist"}.</h1>
        <p className="dashboard-lead">Manage your instruments and keep their maintenance on track.</p>
        <div className="dashboard-grid">
          <article className="dashboard-tile"><span className="tile-icon"><Guitar /></span><h2>Your guitars</h2><p>Register instruments, update their details, and keep your collection organized.</p><a className="tile-status" href="/guitars">Open guitar collection →</a></article>
          <article className="dashboard-tile"><span className="tile-icon"><CalendarDays /></span><h2>Maintenance</h2><p>Record service activities, review maintenance history, and plan upcoming care.</p><a className="tile-status" href="/guitars">Manage maintenance →</a></article>
          <article className="dashboard-tile"><span className="tile-icon"><ShieldCheck /></span><h2>Account</h2><p>{user?.email}</p><span className="tile-status">Signed in</span><p className="account-links"><a href="/profile">Manage Profile</a> · <a href="/change-password">Change Password</a></p></article>
        </div>
      </section>
    </main>
  );
}
