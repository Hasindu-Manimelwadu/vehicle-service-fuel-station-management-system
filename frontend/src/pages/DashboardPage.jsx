import { Link } from "react-router-dom";

export default function DashboardPage() {
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");

  return (
    <div className="container py-5">
      <h1 className="h3">Welcome, {fullName}</h1>
      <p className="text-muted">Role: {role}</p>

      <div className="row g-3 mt-2">
        {(role === "STAFF" || role === "ADMIN") && (
          <div className="col-md-4">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h5>Service Records</h5>
                <p>Create, view, update and assign technicians.</p>
                <Link className="btn btn-primary" to="/service-records">Open</Link>
              </div>
            </div>
          </div>
        )}

        {role === "TECHNICIAN" && (
          <div className="col-md-4">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h5>My Assigned Jobs</h5>
                <p>Start services, update work and complete jobs.</p>
                <Link className="btn btn-primary" to="/technician/jobs">Open</Link>
              </div>
            </div>
          </div>
        )}

        {role === "CUSTOMER" && (
          <div className="alert alert-info mt-3">
            Customer dashboard is ready for integration with the Customer/Booking modules.
          </div>
        )}
      </div>
    </div>
  );
}
