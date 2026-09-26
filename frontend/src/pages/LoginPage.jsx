import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { data } = await api.post("/api/auth/login", { email, password });
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("role", data.role);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("fullName", data.fullName);
      localStorage.setItem("email", data.email);
      navigate("/dashboard");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "We couldn't sign you in. Please check your details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <section className="auth-hero fade-up">
        <div className="auth-hero-badge"><Icon name="shield" size={17} /> Secure workshop operations</div>
        <h1>Keep every service job clear, connected and on time.</h1>
        <p>One workspace for service records, technician assignments and progress tracking.</p>
        <div className="auth-feature-grid">
          <div><Icon name="clipboard" /><span><strong>Structured records</strong><small>Service history that stays easy to follow.</small></span></div>
          <div><Icon name="wrench" /><span><strong>Technician workflow</strong><small>Assigned jobs, progress and completion.</small></span></div>
          <div><Icon name="shield" /><span><strong>Role-based access</strong><small>Protected views for staff and technicians.</small></span></div>
        </div>
      </section>

      <section className="auth-card fade-up delay-1">
        <div className="auth-card-head">
          <div className="auth-icon"><Icon name="user" size={23} /></div>
          <div><h2>Welcome back</h2><p>Sign in to continue to AutoCare.</p></div>
        </div>
        {location.state?.message && <div className="notice notice-success"><Icon name="check" size={18} />{location.state.message}</div>}
        {error && <div className="notice notice-danger"><Icon name="alert" size={18} />{error}</div>}
        <form onSubmit={submit} className="form-stack">
          <label className="field-group">
            <span>Email address</span>
            <div className="input-with-icon"><Icon name="mail" size={18} /><input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" autoComplete="email" required /></div>
          </label>
          <label className="field-group">
            <span>Password</span>
            <div className="input-with-icon"><Icon name="lock" size={18} /><input type={showPassword ? "text" : "password"} value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Enter your password" autoComplete="current-password" required /><button type="button" className="input-action" onClick={() => setShowPassword((value) => !value)}>{showPassword ? "Hide" : "Show"}</button></div>
          </label>
          <button className="btn-app btn-wide" disabled={loading}>{loading ? <><span className="button-spinner" /> Signing in...</> : "Sign in"}</button>
        </form>
        <p className="auth-footer">New customer? <Link to="/register">Create an account</Link></p>
      </section>
    </div>
  );
}
