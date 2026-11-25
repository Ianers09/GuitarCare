import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Guitar } from "lucide-react";
import authService from "../services/authService";
import { getApiError } from "../services/api";
export default function ChangePassword(){
 const nav=useNavigate(); const [form,setForm]=useState({currentPassword:"",newPassword:"",confirmPassword:""}); const [error,setError]=useState(""); const [busy,setBusy]=useState(false);
 function update(e){setForm({...form,[e.target.name]:e.target.value})}
 async function submit(e){e.preventDefault();setError("");if(form.newPassword.length<8)return setError("New password must contain at least 8 characters.");if(form.newPassword!==form.confirmPassword)return setError("New passwords do not match.");setBusy(true);try{await authService.changePassword(form);await authService.logout().catch(()=>{});nav("/login",{replace:true})}catch(e){setError(getApiError(e))}finally{setBusy(false)}}
 return <main className="auth-page"><section className="auth-card"><div className="brand-mark"><Guitar size={22}/></div><h1 className="auth-logo">Change Password</h1><p className="auth-subtitle">Verify your current password before choosing a new one.</p><form className="auth-form" onSubmit={submit}>{[["currentPassword","Current Password"],["newPassword","New Password"],["confirmPassword","Confirm New Password"]].map(([name,label])=><div className="form-group" key={name}><label htmlFor={name}>{label}</label><input id={name} name={name} type="password" value={form[name]} onChange={update} minLength={name==="currentPassword"?1:8} autoComplete="new-password" required/></div>)}{error&&<p className="error-message">{error}</p>}<button className="auth-button" disabled={busy}>{busy?"Updating…":"Update Password"}</button></form><p className="auth-footer"><Link to="/profile">Back to Profile</Link></p></section></main>
}