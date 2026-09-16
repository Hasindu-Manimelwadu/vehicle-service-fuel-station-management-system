import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      await api.post("/api/auth/register", {
        fullName: form.fullName,
        email: form.email,
        phone: form.phone,
        password: form.password,
      });
      navigate("/login", { state: { message: "Registration successful. Please login." } });
    } catch (requestError) {
      const data = requestError.response?.data;
      const validation = data?.errors ? Object.values(data.errors).join(" | ") : null;
      setError(validation || data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5" style={{ maxWidth: 620 }}>
      <div className="card shadow-sm">
        <div className="card-body p-4">
          <h2 className="mb-3">Customer Registration</h2>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={submit}>
            {[
              ["fullName", "Full Name", "text"],
              ["email", "Email", "email"],
              ["phone", "Phone", "text"],
              ["password", "Password", "password"],
              ["confirmPassword", "Confirm Password", "password"],
            ].map(([name, label, type]) => (
              <div className="mb-3" key={name}>
                <label className="form-label">{label}</label>
                <input className="form-control" name={name} type={type}
                  value={form[name]} onChange={change} required />
              </div>
            ))}
            <div className="form-text mb-3">
              Password: 8+ characters with uppercase, lowercase, number and special character.
            </div>
            <button className="btn btn-primary w-100" disabled={loading}>
              {loading ? "Creating..." : "Register"}
            </button>
          </form>
          <p className="mt-3 mb-0 text-center">Already registered? <Link to="/login">Login</Link></p>
        </div>
      </div>
    </div>
  );
}
