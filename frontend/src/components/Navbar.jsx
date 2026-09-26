import { Link, NavLink, useNavigate } from "react-router-dom";
import Icon from "./Icon";

export default function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName") || "User";
  const initials = fullName
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
  const shortcut = typeof navigator !== "undefined" && /Mac|iPhone|iPad/.test(navigator.platform) ? "⌘K" : "Ctrl K";

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const openCommand = () => window.dispatchEvent(new Event("autocare:command-palette"));

  return (
    <>
      <header className="app-navbar">
        <div className="app-navbar-inner">
          <Link className="brand" to={token ? "/dashboard" : "/login"}>
            <span className="brand-mark"><Icon name="car" size={22} /></span>
            <span><strong>AutoCare</strong><small>Service Station</small></span>
          </Link>

          {token && (
            <nav className="desktop-nav" aria-label="Primary navigation">
              <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-link-app active" : "nav-link-app"}>
                <Icon name="dashboard" size={17} /> Dashboard
              </NavLink>
              {(role === "STAFF" || role === "ADMIN") && (
                <NavLink to="/service-records" className={({ isActive }) => isActive ? "nav-link-app active" : "nav-link-app"}>
                  <Icon name="clipboard" size={17} /> Service Records
                </NavLink>
              )}
              {role === "TECHNICIAN" && (
                <NavLink to="/technician/jobs" className={({ isActive }) => isActive ? "nav-link-app active" : "nav-link-app"}>
                  <Icon name="wrench" size={17} /> My Jobs
                </NavLink>
              )}
            </nav>
          )}

          <div className="nav-actions">
            <button className="command-trigger" onClick={openCommand} title="Open quick commands">
              <Icon name="command" size={16} />
              <span>Quick actions</span>
              <kbd>{shortcut}</kbd>
            </button>

            {token ? (
              <>
                <div className="system-mode-chip" title="AutoCare uses a fixed dark operations theme">
                  <span className="system-mode-dot" />
                  <span>Ops mode</span>
                </div>
                <div className="user-chip">
                  <span className="avatar">{initials}</span>
                  <span className="user-copy"><strong>{fullName}</strong><small>{role}</small></span>
                </div>
                <button className="icon-button" onClick={logout} title="Logout" aria-label="Logout">
                  <Icon name="logout" size={19} />
                </button>
              </>
            ) : (
              <>
                <Link className="nav-text-link" to="/login">Login</Link>
                <Link className="btn-app btn-small" to="/register">Create account</Link>
              </>
            )}
          </div>
        </div>
      </header>

      {token && (
        <nav className="mobile-bottom-nav" aria-label="Mobile navigation">
          <NavLink to="/dashboard" className={({ isActive }) => isActive ? "active" : ""}>
            <Icon name="dashboard" size={19}/><span>Dashboard</span>
          </NavLink>
          {(role === "STAFF" || role === "ADMIN") && (
            <NavLink to="/service-records" className={({ isActive }) => isActive ? "active" : ""}>
              <Icon name="clipboard" size={19}/><span>Records</span>
            </NavLink>
          )}
          {role === "TECHNICIAN" && (
            <NavLink to="/technician/jobs" className={({ isActive }) => isActive ? "active" : ""}>
              <Icon name="wrench" size={19}/><span>My Jobs</span>
            </NavLink>
          )}
          <button onClick={openCommand}><Icon name="command" size={19}/><span>Quick</span></button>
        </nav>
      )}
    </>
  );
}
