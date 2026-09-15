import React, { useState } from "react";
import { registerVehicle } from "../api/api";

export default function VehicleForm({ customerId, onVehicleAdded }) {
  const [form, setForm] = useState({
    licensePlateNumber: "",
    make: "",
    model: "",
    year: "",
    color: "",
  });
  const [error, setError] = useState("");

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const payload = { ...form, year: parseInt(form.year, 10) || 0 };
      const response = await registerVehicle(customerId, payload);
      onVehicleAdded(response.data);
      setForm({ licensePlateNumber: "", make: "", model: "", year: "", color: "" });
    } catch (err) {
      setError(err.response?.data || "Could not add vehicle.");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>Register a Vehicle</h3>
      {error && <p style={{ color: "red" }}>{String(error)}</p>}

      <label>License Plate Number</label>
      <input name="licensePlateNumber" value={form.licensePlateNumber} onChange={handleChange} required />

      <label>Make</label>
      <input name="make" value={form.make} onChange={handleChange} required />

      <label>Model</label>
      <input name="model" value={form.model} onChange={handleChange} required />

      <label>Year</label>
      <input type="number" name="year" value={form.year} onChange={handleChange} />

      <label>Color</label>
      <input name="color" value={form.color} onChange={handleChange} />

      <button type="submit">Add Vehicle</button>
    </form>
  );
}
