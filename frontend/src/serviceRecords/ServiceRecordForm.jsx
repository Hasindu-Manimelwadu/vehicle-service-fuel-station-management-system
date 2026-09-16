import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";

export default function ServiceRecordForm() {
  const { id } = useParams();
  const editing = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState({
    bookingId: "",
    serviceDescription: "",
    customerComplaint: "",
    mileage: "",
    remarks: "",
  });
  const [error, setError] = useState("");

  useEffect(() => {
    if (!editing) return;
    api.get(`/api/service-records/${id}`)
      .then(({ data }) => setForm({
        bookingId: data.bookingId,
        serviceDescription: data.serviceDescription || "",
        customerComplaint: data.customerComplaint || "",
        mileage: data.mileage,
        remarks: data.remarks || "",
      }))
      .catch((e) => setError(e.response?.data?.message || "Unable to load record"));
  }, [editing, id]);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (event) => {
    event.preventDefault();
    setError("");

    const payload = {
      serviceDescription: form.serviceDescription,
      customerComplaint: form.customerComplaint,
      mileage: Number(form.mileage),
      remarks: form.remarks,
    };

    try {
      if (editing) {
        await api.put(`/api/service-records/${id}`, payload);
      } else {
        await api.post("/api/service-records", {
          ...payload,
          bookingId: Number(form.bookingId),
        });
      }
      navigate("/service-records");
    } catch (e) {
      const data = e.response?.data;
      const validation = data?.errors ? Object.values(data.errors).join(" | ") : null;
      setError(validation || data?.message || "Save failed");
    }
  };

  return (
    <div className="container py-4" style={{ maxWidth: 760 }}>
      <div className="card shadow-sm">
        <div className="card-body p-4">
          <h2>{editing ? "Edit Service Record" : "Create Service Record"}</h2>
          <p className="text-muted">Booking ID is a temporary integration reference until the booking module is merged.</p>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={submit}>
            {!editing && (
              <div className="mb-3">
                <label className="form-label">Booking Reference ID</label>
                <input className="form-control" name="bookingId" type="number" min="1" value={form.bookingId} onChange={change} required />
              </div>
            )}
            <div className="mb-3">
              <label className="form-label">Service Description</label>
              <textarea className="form-control" name="serviceDescription" rows="3" value={form.serviceDescription} onChange={change} required />
            </div>
            <div className="mb-3">
              <label className="form-label">Customer Complaint</label>
              <textarea className="form-control" name="customerComplaint" rows="2" value={form.customerComplaint} onChange={change} />
            </div>
            <div className="mb-3">
              <label className="form-label">Current Mileage</label>
              <input className="form-control" name="mileage" type="number" min="0" value={form.mileage} onChange={change} required />
            </div>
            <div className="mb-3">
              <label className="form-label">Remarks</label>
              <textarea className="form-control" name="remarks" rows="2" value={form.remarks} onChange={change} />
            </div>
            <div className="d-flex gap-2">
              <button className="btn btn-primary">{editing ? "Update" : "Create"}</button>
              <button className="btn btn-outline-secondary" type="button" onClick={() => navigate("/service-records")}>Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
