import React, { useState } from "react";
import { registerCustomer } from "../api/api";

export default function CustomerRegister({ onRegistered }) {
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    address: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await registerCustomer(form);
      onRegistered(response.data);
    } catch (err) {
      setError(err.response?.data || "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Customer Registration</h2>
      {error && <p style={{ color: "red" }}>{String(error)}</p>}

      <label>Full Name</label>
      <input name="fullName" value={form.fullName} onChange={handleChange} required />

      <label>Email</label>
      <input type="email" name="email" value={form.email} onChange={handleChange} required />

      <label>Phone Number</label>
      <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required />

      <label>Address</label>
      <input name="address" value={form.address} onChange={handleChange} />

      <label>Password</label>
      <input type="password" name="password" value={form.password} onChange={handleChange} required />

      <button type="submit" disabled={loading}>
        {loading ? "Registering..." : "Register"}
      </button>
    </form>
  );
}
