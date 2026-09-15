import React, { useState } from "react";
import { updateCustomer } from "../api/api";

export default function CustomerProfile({ customer, onUpdated }) {
  const [form, setForm] = useState({
    fullName: customer.fullName,
    phoneNumber: customer.phoneNumber,
    address: customer.address,
  });
  const [message, setMessage] = useState("");

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await updateCustomer(customer.id, { ...customer, ...form });
      onUpdated(response.data);
      setMessage("Profile updated successfully.");
    } catch (err) {
      setMessage("Update failed. Please try again.");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>My Profile</h2>
      {message && <p>{message}</p>}

      <label>Full Name</label>
      <input name="fullName" value={form.fullName} onChange={handleChange} required />

      <label>Phone Number</label>
      <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required />

      <label>Address</label>
      <input name="address" value={form.address} onChange={handleChange} />

      <p>Email: {customer.email} (contact support to change)</p>

      <button type="submit">Save Changes</button>
    </form>
  );
}
