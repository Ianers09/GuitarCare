import { useState } from "react";
import { Link } from "react-router-dom";
import { Guitar } from "lucide-react";
import authService from "../services/authService";
import { getApiError } from "../services/api";
export default function ForgotPassword(){
 const [email,setEmail]=useState("");const [message,setMessage]=useState("");const [error,setError]=useState("");const [busy,setBusy]=useState(false);
 async function submit(e){e.preventDefault();setBusy(true);setError("");setMessage("");try{const r=await authService.forgotPassword({email});setMessage(r.message)}catch(e){setError(getApiError(e))}finally{setBusy(false)}}
 return <main className="auth-page"><section className="auth-card"><div className="brand-mark"><Guitar size={22}/></div><h1 className="auth-logo">Forgot Password</h1><p className="auth-subtitle">Enter your account email and we will send password reset instructions.</p><form className="auth-form" onSubmit={submit}><div className="form-group"><label htmlFor="email">Email Address</label><input id="email" type="email" value={email} onChange={e=>setEmail(e.target.value)} autoComplete="email" required/></div>{error&&<p className="error-message">{error}</p>}{message&&<p className="success-message">{message}</p>}<button className="auth-button" disabled={busy}>{busy?"Sending…":"Send Reset Link"}</button></form><p className="auth-footer"><Link to="/login">Back to Sign In</Link></p></section></main>
}