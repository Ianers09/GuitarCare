import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Guitar } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import authService from "../services/authService";
import { getApiError } from "../services/api";
export default function Profile(){
 const {user}=useAuth(), nav=useNavigate();
 const [fullName,setFullName]=useState(user?.fullName||""); const [email,setEmail]=useState(user?.email||"");
 const [message,setMessage]=useState(""); const [error,setError]=useState(""); const [busy,setBusy]=useState(false);
 useEffect(()=>{authService.profile().then(u=>{setFullName(u.fullName);setEmail(u.email)}).catch(e=>setError(getApiError(e)))},[]);
 async function submit(e){e.preventDefault();setBusy(true);setError("");setMessage("");try{await authService.updateProfile({fullName});setMessage("Your profile has been updated.");}catch(e){setError(getApiError(e))}finally{setBusy(false)}}
 return <main className="auth-page"><section className="auth-card"><div className="brand-mark"><Guitar size={22}/></div><h1 className="auth-logo">My Profile</h1><p className="auth-subtitle">View and update your GuitarCare account information.</p><form className="auth-form" onSubmit={submit}><div className="form-group"><label htmlFor="fullName">Full Name</label><input id="fullName" value={fullName} onChange={e=>setFullName(e.target.value)} maxLength={120} minLength={2} required/></div><div className="form-group"><label htmlFor="email">Email Address</label><input id="email" value={email} readOnly disabled/></div>{error&&<p className="error-message">{error}</p>}{message&&<p className="success-message">{message}</p>}<button className="auth-button" disabled={busy}>{busy?"Saving…":"Save Changes"}</button></form><p className="auth-footer"><Link to="/change-password">Change Password</Link> · <Link to="/dashboard">Back to Dashboard</Link></p></section></main>
}