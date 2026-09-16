import { Link } from "react-router-dom";

export default function UnauthorizedPage() {
  return (
    <div className="container py-5 text-center">
      <h2>403 - Unauthorized</h2>
      <p>You do not have permission to access this page.</p>
      <Link className="btn btn-primary" to="/dashboard">Back to Dashboard</Link>
    </div>
  );
}
