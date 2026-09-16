import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

export default function TechnicianJobs() {
  const [jobs, setJobs] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/api/service-records/my-jobs")
      .then((r) => setJobs(r.data))
      .catch((e) => setError(e.response?.data?.message || "Unable to load assigned jobs"));
  }, []);

  return (
    <div className="container py-4">
      <h2>My Assigned Service Jobs</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="row g-3 mt-1">
        {jobs.map((job) => (
          <div className="col-md-6" key={job.serviceRecordId}>
            <div className="card shadow-sm h-100"><div className="card-body">
              <div className="d-flex justify-content-between">
                <h5>Record #{job.serviceRecordId}</h5>
                <span className="badge text-bg-secondary">{job.serviceStatus}</span>
              </div>
              <p className="mb-1"><strong>Booking:</strong> {job.bookingId}</p>
              <p className="mb-1"><strong>Task:</strong> {job.taskDescription}</p>
              <p>{job.serviceDescription}</p>
              <Link className="btn btn-primary" to={`/technician/jobs/${job.serviceRecordId}`}>Open Job</Link>
            </div></div>
          </div>
        ))}
        {jobs.length === 0 && <p className="text-muted">No assigned jobs.</p>}
      </div>
    </div>
  );
}
