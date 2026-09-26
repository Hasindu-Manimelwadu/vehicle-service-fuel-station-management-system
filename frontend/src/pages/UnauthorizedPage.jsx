import { Link } from "react-router-dom";
import Icon from "../components/Icon";

export default function UnauthorizedPage() {
  return <div className="center-state"><span className="center-state-icon"><Icon name="lock" size={31} /></span><h1>Access restricted</h1><p>Your current role does not have permission to open this page.</p><Link className="btn-app" to="/dashboard">Return to dashboard</Link></div>;
}
