import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: "", email: "", phone: "", password: "", confirmPassword: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    if (form.password !== form.confirmPassword) return setError("Passwords do not match.");
    setLoading(true);
    try {
      await api.post("/api/auth/register", { fullName: form.fullName, email: form.email, phone: form.phone, password: form.password });
      navigate("/login", { state: { message: "Registration successful. You can now sign in." } });
    } catch (requestError) {
      const data = requestError.response?.data;
      const validation = data?.errors ? Object.values(data.errors).join(" • ") : null;
      setError(validation || data?.message || "Registration failed.");
    } finally { setLoading(false); }
  };

  return (
    <div className="auth-shell auth-shell-register">
      <section className="auth-hero fade-up">
        <div className="auth-hero-badge"><Icon name="car" size={17} /> AutoCare customer access</div>
        <h1>Your service journey starts with one simple account.</h1>
        <p>Register your details securely. Staff and technician access is managed separately by authorized users.</p>
        <div className="security-note"><Icon name="shield" size={21} /><span><strong>Security by default</strong><small>Passwords are protected using secure hashing and role-based access.</small></span></div>
      </section>

      <section className="auth-card auth-card-wide fade-up delay-1">
        <div className="auth-card-head"><div className="auth-icon"><Icon name="user" size={23} /></div><div><h2>Create your account</h2><p>Enter your details to get started.</p></div></div>
        {error && <div className="notice notice-danger"><Icon name="alert" size={18} />{error}</div>}
        <form onSubmit={submit} className="form-stack">
          <div className="form-grid-2">
            <label className="field-group"><span>Full name</span><div className="input-with-icon"><Icon name="user" size={18} /><input name="fullName" value={form.fullName} onChange={change} placeholder="Your full name" required /></div></label>
            <label className="field-group"><span>Phone number</span><div className="input-with-icon"><Icon name="phone" size={18} /><input name="phone" value={form.phone} onChange={change} placeholder="077 123 4567" required /></div></label>
          </div>
          <label className="field-group"><span>Email address</span><div className="input-with-icon"><Icon name="mail" size={18} /><input name="email" type="email" value={form.email} onChange={change} placeholder="you@example.com" required /></div></label>
          <div className="form-grid-2">
            <label className="field-group"><span>Password</span><div className="input-with-icon"><Icon name="lock" size={18} /><input name="password" type={showPassword ? "text" : "password"} value={form.password} onChange={change} placeholder="Strong password" required /></div></label>
            <label className="field-group"><span>Confirm password</span><div className="input-with-icon"><Icon name="lock" size={18} /><input name="confirmPassword" type={showPassword ? "text" : "password"} value={form.confirmPassword} onChange={change} placeholder="Repeat password" required /></div></label>
          </div>
          <div className="password-row"><span>8+ characters, uppercase, lowercase, number & special character.</span><button type="button" className="text-button" onClick={() => setShowPassword((value) => !value)}>{showPassword ? "Hide passwords" : "Show passwords"}</button></div>
          <button className="btn-app btn-wide" disabled={loading}>{loading ? <><span className="button-spinner" /> Creating account...</> : "Create account"}</button>
        </form>
        <p className="auth-footer">Already registered? <Link to="/login">Sign in</Link></p>
      </section>
    </div>
  );
}
