import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

export default function ServiceRecordList() {
  const [records, setRecords] = useState([]);
  const [technicians, setTechnicians] = useState([]);
  const [error, setError] = useState("");
  const [assigningId, setAssigningId] = useState(null);
  const [technicianId, setTechnicianId] = useState("");
  const [taskDescription, setTaskDescription] = useState("");

  const load = async () => {
    try {
      const [recordsResponse, techniciansResponse] = await Promise.all([
        api.get("/api/service-records"),
        api.get("/api/service-records/technicians"),
      ]);

      setRecords(recordsResponse.data);
      setTechnicians(techniciansResponse.data);
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Unable to load service records");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const remove = async (id) => {
    if (!window.confirm("Delete this pending service record?")) return;

    try {
      await api.delete(`/api/service-records/${id}`);
      await load();
    } catch (e) {
      alert(e.response?.data?.message || "Delete failed");
    }
  };

  const openAssignment = (record) => {
    setAssigningId(record.serviceRecordId);
    setTechnicianId(record.technicianId ? String(record.technicianId) : "");
    setTaskDescription(record.taskDescription || "");
  };

  const assign = async (event) => {
    event.preventDefault();

    try {
      await api.post(`/api/service-records/${assigningId}/assign`, {
        technicianId: Number(technicianId),
        taskDescription,
      });

      setAssigningId(null);
      setTechnicianId("");
      setTaskDescription("");
      await load();
    } catch (e) {
      alert(e.response?.data?.message || "Technician assignment failed");
    }
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 className="mb-1">Service Records</h2>
          <small className="text-muted">Staff / Admin management</small>
        </div>
        <Link className="btn btn-primary" to="/service-records/new">
          + New Service Record
        </Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {assigningId && (
        <div className="card mb-3 border-primary">
          <div className="card-body">
            <h5>Assign Technician to Record #{assigningId}</h5>
            <form className="row g-2" onSubmit={assign}>
              <div className="col-md-4">
                <select
                  className="form-select"
                  value={technicianId}
                  onChange={(e) => setTechnicianId(e.target.value)}
                  required
                >
                  <option value="">Select technician</option>
                  {technicians.map((technician) => (
                    <option key={technician.userId} value={technician.userId}>
                      {technician.fullName} ({technician.email})
                    </option>
                  ))}
                </select>
                {technicians.length === 0 && (
                  <small className="text-danger">
                    No active TECHNICIAN accounts found. Create a demo user and change its role to TECHNICIAN.
                  </small>
                )}
              </div>
              <div className="col-md-5">
                <input
                  className="form-control"
                  placeholder="Task description"
                  value={taskDescription}
                  onChange={(e) => setTaskDescription(e.target.value)}
                  required
                />
              </div>
              <div className="col-md-3 d-flex gap-2">
                <button className="btn btn-primary" type="submit">
                  Assign
                </button>
                <button
                  className="btn btn-outline-secondary"
                  type="button"
                  onClick={() => setAssigningId(null)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="table-responsive">
        <table className="table table-hover align-middle">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Booking</th>
              <th>Description</th>
              <th>Mileage</th>
              <th>Status</th>
              <th>Technician</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {records.map((record) => (
              <tr key={record.serviceRecordId}>
                <td>{record.serviceRecordId}</td>
                <td>{record.bookingId}</td>
                <td>{record.serviceDescription}</td>
                <td>{record.mileage}</td>
                <td>
                  <span className="badge text-bg-secondary">
                    {record.serviceStatus}
                  </span>
                </td>
                <td>{record.technicianName || "Not assigned"}</td>
                <td className="d-flex flex-wrap gap-1">
                  <Link
                    className="btn btn-sm btn-outline-primary"
                    to={`/service-records/${record.serviceRecordId}`}
                  >
                    View
                  </Link>

                  {record.serviceStatus === "PENDING" && (
                    <>
                      <Link
                        className="btn btn-sm btn-outline-warning"
                        to={`/service-records/${record.serviceRecordId}/edit`}
                      >
                        Edit
                      </Link>
                      <button
                        className="btn btn-sm btn-outline-success"
                        onClick={() => openAssignment(record)}
                      >
                        {record.technicianId ? "Reassign" : "Assign"}
                      </button>
                      {!record.technicianId && (
                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => remove(record.serviceRecordId)}
                        >
                          Delete
                        </button>
                      )}
                    </>
                  )}
                </td>
              </tr>
            ))}

            {records.length === 0 && (
              <tr>
                <td colSpan="7" className="text-center text-muted py-4">
                  No service records yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
