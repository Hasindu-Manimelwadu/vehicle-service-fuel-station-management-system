import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";

export default function TechnicianJobDetails() {
  const { id } = useParams();
  const [record, setRecord] = useState(null);
  const [error, setError] = useState("");
  const [progress, setProgress] = useState({ workPerformed: "", remarks: "" });
  const [part, setPart] = useState({ partId: "", partName: "", quantityUsed: 1 });

  const load = async () => {
    try {
      const response = await api.get(`/api/service-records/my-jobs/${id}`);
      setRecord(response.data);
      setProgress({
        workPerformed: response.data.workPerformed || "",
        remarks: response.data.remarks || "",
      });
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Unable to load job");
    }
  };

  useEffect(() => { load(); }, [id]);

  const action = async (request) => {
    try {
      await request();
      await load();
    } catch (e) {
      alert(e.response?.data?.message || "Operation failed");
    }
  };

  const saveProgress = (event) => {
    event.preventDefault();
    action(() => api.patch(`/api/service-records/${id}/progress`, progress));
  };

  const addPart = (event) => {
    event.preventDefault();
    action(() => api.post(`/api/service-records/${id}/parts`, {
      partId: part.partId ? Number(part.partId) : null,
      partName: part.partName,
      quantityUsed: Number(part.quantityUsed),
    })).then(() => setPart({ partId: "", partName: "", quantityUsed: 1 }));
  };

  if (error) return <div className="container py-4"><div className="alert alert-danger">{error}</div></div>;
  if (!record) return <div className="container py-4">Loading...</div>;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Service Job #{record.serviceRecordId}</h2>
        <span className="badge text-bg-primary fs-6">{record.serviceStatus}</span>
      </div>

      <div className="card shadow-sm mb-3"><div className="card-body">
        <p><strong>Booking:</strong> {record.bookingId}</p>
        <p><strong>Task:</strong> {record.taskDescription}</p>
        <p><strong>Service:</strong> {record.serviceDescription}</p>
        <p><strong>Complaint:</strong> {record.customerComplaint || "-"}</p>
        <p className="mb-0"><strong>Mileage:</strong> {record.mileage}</p>
      </div></div>

      {record.serviceStatus === "PENDING" && (
        <button className="btn btn-success mb-3" onClick={() => action(() => api.patch(`/api/service-records/${id}/start`))}>
          Start Service
        </button>
      )}

      {record.serviceStatus === "IN_PROGRESS" && (
        <>
          <div className="card shadow-sm mb-3"><div className="card-body">
            <h5>Update Service Progress</h5>
            <form onSubmit={saveProgress}>
              <div className="mb-3">
                <label className="form-label">Work Performed</label>
                <textarea className="form-control" rows="4" value={progress.workPerformed}
                  onChange={(e) => setProgress({ ...progress, workPerformed: e.target.value })} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Remarks</label>
                <textarea className="form-control" rows="2" value={progress.remarks}
                  onChange={(e) => setProgress({ ...progress, remarks: e.target.value })} />
              </div>
              <button className="btn btn-primary">Save Progress</button>
            </form>
          </div></div>

          <div className="card shadow-sm mb-3"><div className="card-body">
            <h5>Record Spare Part Used</h5>
            <form className="row g-2" onSubmit={addPart}>
              <div className="col-md-3"><input className="form-control" type="number" min="1" placeholder="Part ID (optional)" value={part.partId} onChange={(e) => setPart({ ...part, partId: e.target.value })} /></div>
              <div className="col-md-5"><input className="form-control" placeholder="Part name" value={part.partName} onChange={(e) => setPart({ ...part, partName: e.target.value })} required /></div>
              <div className="col-md-2"><input className="form-control" type="number" min="1" value={part.quantityUsed} onChange={(e) => setPart({ ...part, quantityUsed: e.target.value })} required /></div>
              <div className="col-md-2"><button className="btn btn-outline-primary w-100">Add</button></div>
            </form>
          </div></div>

          <div className="card shadow-sm mb-3"><div className="card-body">
            <h5>Parts Used</h5>
            {record.parts?.length ? record.parts.map((p) => (
              <div className="d-flex justify-content-between border-bottom py-2" key={p.usageId}>
                <span>{p.partName} × {p.quantityUsed}</span>
                <button className="btn btn-sm btn-outline-danger" onClick={() => action(() => api.delete(`/api/service-records/${id}/parts/${p.usageId}`))}>Remove</button>
              </div>
            )) : <p className="text-muted">No parts recorded.</p>}
          </div></div>

          <button className="btn btn-danger" onClick={() => action(() => api.patch(`/api/service-records/${id}/complete`))}>
            Complete Service
          </button>
        </>
      )}

      {record.serviceStatus === "COMPLETED" && (
        <div className="alert alert-success">
          Service completed. This record is now read-only for the technician.
        </div>
      )}
    </div>
  );
}
