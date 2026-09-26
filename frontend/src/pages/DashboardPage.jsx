import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import StatusBadge from "../components/StatusBadge";

export default function DashboardPage() {
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(role !== "CUSTOMER");
  const [connected, setConnected] = useState(true);

  useEffect(() => {
    const request = role === "STAFF" || role === "ADMIN" ? api.get("/api/service-records") : role === "TECHNICIAN" ? api.get("/api/service-records/my-jobs") : null;
    if (!request) { setLoading(false); return; }
    request.then((response) => { setRecords(response.data); setConnected(true); }).catch(() => setConnected(false)).finally(() => setLoading(false));
  }, [role]);

  const stats = useMemo(() => ({
    total: records.length,
    pending: records.filter((item) => item.serviceStatus === "PENDING").length,
    progress: records.filter((item) => item.serviceStatus === "IN_PROGRESS").length,
    completed: records.filter((item) => item.serviceStatus === "COMPLETED").length,
  }), [records]);
  const completionRate = stats.total ? Math.round((stats.completed / stats.total) * 100) : 0;

  return (
    <div className="page-container">
      <section className="dashboard-welcome fade-up">
        <div><div className="eyebrow">Overview</div><h1>Good to see you, {fullName?.split(" ")[0] || "there"}.</h1><p>{role === "TECHNICIAN" ? "Here are your assigned service jobs and current progress." : role === "CUSTOMER" ? "Your customer workspace is ready for upcoming booking integration." : "Monitor service records and keep workshop operations moving."}</p></div>
        <div className="dashboard-welcome-actions"><span className={`system-signal ${connected ? "online" : "offline"}`}><i />{connected ? "System online" : "Connection issue"}</span><span className="role-pill"><Icon name="shield" size={16} /> {role}</span></div>
      </section>

      {role !== "CUSTOMER" && (loading ? <section className="stats-grid fade-up delay-1">{[1,2,3,4].map((item) => <div className="stat-card skeleton-card" key={item}><span className="skeleton-block icon"/><div><span className="skeleton-line short"/><span className="skeleton-line value"/></div></div>)}</section> : <section className="stats-grid fade-up delay-1">
        <div className="stat-card"><span className="stat-icon"><Icon name="clipboard" /></span><div><small>{role === "TECHNICIAN" ? "Assigned jobs" : "Total records"}</small><strong>{stats.total}</strong></div></div>
        <div className="stat-card"><span className="stat-icon amber"><Icon name="clock" /></span><div><small>Pending</small><strong>{stats.pending}</strong></div></div>
        <div className="stat-card"><span className="stat-icon blue"><Icon name="wrench" /></span><div><small>In progress</small><strong>{stats.progress}</strong></div></div>
        <div className="stat-card"><span className="stat-icon green"><Icon name="check" /></span><div><small>Completed</small><strong>{stats.completed}</strong></div></div>
      </section>)}

      <section className="dashboard-grid fade-up delay-2">
        {(role === "STAFF" || role === "ADMIN") && <Link className="feature-card" to="/service-records"><span className="feature-icon"><Icon name="clipboard" size={24} /></span><div><h3>Service Records</h3><p>Create, assign, monitor and review workshop service records.</p><span className="card-link">Manage records <Icon name="chevronRight" size={16} /></span></div></Link>}
        {role === "TECHNICIAN" && <Link className="feature-card" to="/technician/jobs"><span className="feature-icon"><Icon name="wrench" size={24} /></span><div><h3>My Assigned Jobs</h3><p>Start jobs, update work performed, add parts and complete services.</p><span className="card-link">Open jobs <Icon name="chevronRight" size={16} /></span></div></Link>}
        {role === "CUSTOMER" && <div className="empty-panel"><span className="empty-icon"><Icon name="car" size={30} /></span><h3>Customer workspace ready</h3><p>Vehicle and booking features will appear here when the customer and booking modules are integrated.</p></div>}

        {role !== "CUSTOMER" && <div className="pulse-panel">
          <div className="panel-head"><div><h3>Workshop pulse</h3><p>Live completion snapshot from current records.</p></div><span className="pulse-chip"><Icon name="pulse" size={15}/> Live</span></div>
          <div className="pulse-body"><div className="progress-orb" style={{"--progress": `${completionRate * 3.6}deg`}}><div><strong>{completionRate}%</strong><small>complete</small></div></div><div className="pulse-metrics"><div><span>Active queue</span><strong>{stats.pending + stats.progress}</strong></div><div><span>Finished jobs</span><strong>{stats.completed}</strong></div><div><span>Efficiency signal</span><strong>{completionRate >= 70 ? "Strong" : completionRate >= 40 ? "Steady" : "Building"}</strong></div></div></div>
        </div>}

        {records.slice(0, 4).length > 0 && <div className="recent-panel"><div className="panel-head"><div><h3>Recent activity</h3><p>Latest service records at a glance.</p></div></div><div className="recent-list">{records.slice(0, 4).map((record) => <div className="recent-item" key={record.serviceRecordId}><span className="recent-id">#{record.serviceRecordId}</span><div className="recent-copy"><strong>{record.serviceDescription}</strong><small>Booking {record.bookingId}</small></div><StatusBadge status={record.serviceStatus} /></div>)}</div></div>}
      </section>
    </div>
  );
}
