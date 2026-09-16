import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";

export default function ServiceRecordDetails() {
  const { id } = useParams();
  const [record, setRecord] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get(`/api/service-records/${id}`)
      .then((r) => setRecord(r.data))
      .catch((e) => setError(e.response?.data?.message || "Unable to load record"));
  }, [id]);

  if (error) return <div className="container py-4"><div className="alert alert-danger">{error}</div></div>;
  if (!record) return <div className="container py-4">Loading...</div>;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between mb-3">
        <h2>Service Record #{record.serviceRecordId}</h2>
        <Link className="btn btn-outline-secondary" to="/service-records">Back</Link>
      </div>
      <div className="card shadow-sm mb-3"><div className="card-body">
        <div className="row g-3">
          <div className="col-md-4"><strong>Booking:</strong> {record.bookingId}</div>
          <div className="col-md-4"><strong>Status:</strong> {record.serviceStatus}</div>
          <div className="col-md-4"><strong>Mileage:</strong> {record.mileage}</div>
          <div className="col-12"><strong>Description:</strong> {record.serviceDescription}</div>
          <div className="col-12"><strong>Complaint:</strong> {record.customerComplaint || "-"}</div>
          <div className="col-md-6"><strong>Technician:</strong> {record.technicianName || "Not assigned"}</div>
          <div className="col-md-6"><strong>Assignment:</strong> {record.assignmentStatus || "-"}</div>
          <div className="col-12"><strong>Task:</strong> {record.taskDescription || "-"}</div>
          <div className="col-12"><strong>Work performed:</strong> {record.workPerformed || "-"}</div>
          <div className="col-12"><strong>Remarks:</strong> {record.remarks || "-"}</div>
        </div>
      </div></div>

      <div className="card shadow-sm"><div className="card-body">
        <h5>Parts Used</h5>
        {record.parts?.length ? (
          <ul className="list-group list-group-flush">
            {record.parts.map((p) => <li className="list-group-item" key={p.usageId}>{p.partName} × {p.quantityUsed}</li>)}
          </ul>
        ) : <p className="text-muted mb-0">No parts recorded.</p>}
      </div></div>
    </div>
  );
}
