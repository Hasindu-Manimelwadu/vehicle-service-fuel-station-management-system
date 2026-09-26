import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import StatusBadge from "../components/StatusBadge";
import PageHeader from "../components/PageHeader";
import ConfirmDialog from "../components/ConfirmDialog";
import { useToast } from "../components/ToastContext";

export default function ServiceRecordList() {
  const [records, setRecords] = useState([]);
  const [technicians, setTechnicians] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [viewMode, setViewMode] = useState(() => localStorage.getItem("autocare-record-view") || "table");
  const [assigningId, setAssigningId] = useState(null);
  const [technicianId, setTechnicianId] = useState("");
  const [taskDescription, setTaskDescription] = useState("");
  const [deleteId, setDeleteId] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [peekRecord, setPeekRecord] = useState(null);
  const [peekLoading, setPeekLoading] = useState(false);
  const { showToast } = useToast();

  const load = async () => {
    setLoading(true);
    try {
      const [recordsResponse, techniciansResponse] = await Promise.all([api.get("/api/service-records"), api.get("/api/service-records/technicians")]);
      setRecords(recordsResponse.data); setTechnicians(techniciansResponse.data); setError("");
    } catch (e) { setError(e.response?.data?.message || "Unable to load service records"); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);
  useEffect(() => localStorage.setItem("autocare-record-view", viewMode), [viewMode]);

  const filtered = useMemo(() => records.filter((record) => {
    const text = `${record.serviceRecordId} ${record.bookingId} ${record.serviceDescription} ${record.technicianName || ""}`.toLowerCase();
    return text.includes(query.toLowerCase()) && (statusFilter === "ALL" || record.serviceStatus === statusFilter);
  }), [records, query, statusFilter]);

  const stats = useMemo(() => ({ total: records.length, pending: records.filter((r) => r.serviceStatus === "PENDING").length, progress: records.filter((r) => r.serviceStatus === "IN_PROGRESS").length, completed: records.filter((r) => r.serviceStatus === "COMPLETED").length }), [records]);
  const groups = useMemo(() => ({ PENDING: filtered.filter((record) => record.serviceStatus === "PENDING"), IN_PROGRESS: filtered.filter((record) => record.serviceStatus === "IN_PROGRESS"), COMPLETED: filtered.filter((record) => record.serviceStatus === "COMPLETED") }), [filtered]);

  const remove = async () => { setActionLoading(true); try { await api.delete(`/api/service-records/${deleteId}`); showToast("Service record deleted."); setDeleteId(null); await load(); } catch (e) { showToast(e.response?.data?.message || "Delete failed", "error"); } finally { setActionLoading(false); } };
  const openAssignment = (record) => { setAssigningId(record.serviceRecordId); setTechnicianId(record.technicianId ? String(record.technicianId) : ""); setTaskDescription(record.taskDescription || ""); };
  const assign = async (event) => { event.preventDefault(); setActionLoading(true); try { await api.post(`/api/service-records/${assigningId}/assign`, { technicianId: Number(technicianId), taskDescription }); showToast("Technician assigned successfully."); setAssigningId(null); setTechnicianId(""); setTaskDescription(""); await load(); } catch (e) { showToast(e.response?.data?.message || "Technician assignment failed", "error"); } finally { setActionLoading(false); } };
  const openPeek = async (id) => { setPeekRecord({ serviceRecordId: id }); setPeekLoading(true); try { const response = await api.get(`/api/service-records/${id}`); setPeekRecord(response.data); } catch (e) { showToast(e.response?.data?.message || "Unable to preview record", "error"); setPeekRecord(null); } finally { setPeekLoading(false); } };

  const RecordActions = ({ record }) => <div className="row-actions"><button className="table-action" title="Quick preview" onClick={() => openPeek(record.serviceRecordId)}><Icon name="eye" size={17} /></button>{record.serviceStatus === "PENDING" && <><Link className="table-action" title="Edit" to={`/service-records/${record.serviceRecordId}/edit`}><Icon name="edit" size={17} /></Link><button className="table-action" title={record.technicianId ? "Reassign technician" : "Assign technician"} onClick={() => openAssignment(record)}><Icon name="user" size={17} /></button>{!record.technicianId && <button className="table-action danger" title="Delete" onClick={() => setDeleteId(record.serviceRecordId)}><Icon name="trash" size={17} /></button>}</>}</div>;

  return (
    <div className="page-container">
      <PageHeader eyebrow="Workshop management" title="Service Records" subtitle="Create, assign and monitor every service record from one clean workspace." actions={<Link className="btn-app" to="/service-records/new"><Icon name="plus" size={17} /> New service record</Link>} />

      <div className="mini-stats fade-up delay-1">
        <button className={statusFilter === "ALL" ? "mini-stat active" : "mini-stat"} onClick={() => setStatusFilter("ALL")}><span>All records</span><strong>{stats.total}</strong></button>
        <button className={statusFilter === "PENDING" ? "mini-stat active" : "mini-stat"} onClick={() => setStatusFilter("PENDING")}><span>Pending</span><strong>{stats.pending}</strong></button>
        <button className={statusFilter === "IN_PROGRESS" ? "mini-stat active" : "mini-stat"} onClick={() => setStatusFilter("IN_PROGRESS")}><span>In progress</span><strong>{stats.progress}</strong></button>
        <button className={statusFilter === "COMPLETED" ? "mini-stat active" : "mini-stat"} onClick={() => setStatusFilter("COMPLETED")}><span>Completed</span><strong>{stats.completed}</strong></button>
      </div>

      {error && <div className="notice notice-danger"><Icon name="alert" size={18} />{error}</div>}

      <section className="data-card fade-up delay-2">
        <div className="table-toolbar">
          <div className="search-box"><Icon name="search" size={18} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search records, booking or technician..." /></div>
          <div className="toolbar-right"><div className="view-switcher" aria-label="Record view"><button className={viewMode === "table" ? "active" : ""} onClick={() => setViewMode("table")} title="Table view"><Icon name="list" size={17}/></button><button className={viewMode === "board" ? "active" : ""} onClick={() => setViewMode("board")} title="Board view"><Icon name="board" size={17}/></button></div><div className="filter-box"><Icon name="filter" size={17} /><select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}><option value="ALL">All statuses</option><option value="PENDING">Pending</option><option value="IN_PROGRESS">In progress</option><option value="COMPLETED">Completed</option></select></div></div>
        </div>

        {loading ? <div className="table-loading"><span className="loading-ring" /> Loading service records...</div> : viewMode === "table" ? (
          <div className="table-responsive"><table className="modern-table"><thead><tr><th>Record</th><th>Booking</th><th>Service</th><th>Mileage</th><th>Status</th><th>Technician</th><th className="text-end">Actions</th></tr></thead><tbody>
            {filtered.map((record) => <tr key={record.serviceRecordId}><td><span className="record-id">#{record.serviceRecordId}</span></td><td>{record.bookingId}</td><td><div className="cell-main">{record.serviceDescription}</div></td><td>{Number(record.mileage).toLocaleString()} km</td><td><StatusBadge status={record.serviceStatus} /></td><td>{record.technicianName ? <div className="person-cell"><span className="avatar avatar-small">{record.technicianName.split(" ").map((p) => p[0]).join("").slice(0,2)}</span>{record.technicianName}</div> : <span className="muted">Not assigned</span>}</td><td><RecordActions record={record}/></td></tr>)}
            {filtered.length === 0 && <tr><td colSpan="7"><div className="empty-table"><Icon name="clipboard" size={29} /><strong>No matching service records</strong><span>Try a different search or create a new service record.</span></div></td></tr>}
          </tbody></table></div>
        ) : <div className="records-board">{[["PENDING","Pending"],["IN_PROGRESS","In progress"],["COMPLETED","Completed"]].map(([status,label]) => <section className="board-column" key={status}><header><div><StatusBadge status={status}/><strong>{label}</strong></div><span>{groups[status].length}</span></header><div className="board-stack">{groups[status].map((record) => <article className="board-record" key={record.serviceRecordId} onClick={() => openPeek(record.serviceRecordId)}><div className="board-record-top"><span className="record-id">#{record.serviceRecordId}</span><span>Booking {record.bookingId}</span></div><h4>{record.serviceDescription}</h4><div className="board-record-meta"><span><Icon name="mileage" size={15}/>{Number(record.mileage).toLocaleString()} km</span><span><Icon name="user" size={15}/>{record.technicianName || "Unassigned"}</span></div><div className="board-record-foot"><span>Quick preview</span><Icon name="chevronRight" size={16}/></div></article>)}{groups[status].length === 0 && <div className="board-empty">No {label.toLowerCase()} records</div>}</div></section>)}</div>}
      </section>

      {peekRecord && <div className="peek-backdrop" onMouseDown={() => setPeekRecord(null)}><aside className="record-peek" onMouseDown={(event) => event.stopPropagation()}><div className="peek-head"><div><div className="eyebrow">Quick preview</div><h3>Service Record #{peekRecord.serviceRecordId}</h3></div><button className="icon-button" onClick={() => setPeekRecord(null)} aria-label="Close preview">×</button></div>{peekLoading ? <div className="page-loading compact-loading"><span className="loading-ring"/>Loading record...</div> : <><div className="peek-status"><StatusBadge status={peekRecord.serviceStatus}/><span>Booking {peekRecord.bookingId}</span></div><h4 className="peek-title">{peekRecord.serviceDescription}</h4><div className="peek-facts"><div><Icon name="mileage" size={18}/><span><small>Mileage</small><strong>{Number(peekRecord.mileage || 0).toLocaleString()} km</strong></span></div><div><Icon name="user" size={18}/><span><small>Technician</small><strong>{peekRecord.technicianName || "Not assigned"}</strong></span></div></div><div className="peek-section"><small>Customer complaint</small><p>{peekRecord.customerComplaint || "No complaint recorded."}</p></div><div className="peek-section"><small>Work performed</small><p>{peekRecord.workPerformed || "Work has not been recorded yet."}</p></div><div className="peek-section"><small>Parts used</small><p>{peekRecord.parts?.length ? `${peekRecord.parts.length} part record${peekRecord.parts.length === 1 ? "" : "s"} attached` : "No parts recorded."}</p></div><Link className="btn-app peek-open" to={`/service-records/${peekRecord.serviceRecordId}`} onClick={() => setPeekRecord(null)}>Open full record <Icon name="chevronRight" size={16}/></Link></>}</aside></div>}

      {assigningId && <div className="modal-backdrop-custom" onMouseDown={() => setAssigningId(null)}><form className="assignment-dialog" onSubmit={assign} onMouseDown={(e) => e.stopPropagation()}><div className="dialog-top"><span className="feature-icon"><Icon name="user" size={22} /></span><div><h3>Assign technician</h3><p>Service record #{assigningId}</p></div></div><label className="field-group"><span>Technician</span><select value={technicianId} onChange={(e) => setTechnicianId(e.target.value)} required><option value="">Select technician</option>{technicians.map((tech) => <option key={tech.userId} value={tech.userId}>{tech.fullName} — {tech.email}</option>)}</select>{technicians.length === 0 && <small className="field-error">No active TECHNICIAN accounts found.</small>}</label><label className="field-group"><span>Task description</span><textarea rows="3" value={taskDescription} onChange={(e) => setTaskDescription(e.target.value)} placeholder="Describe the work to be completed" required /></label><div className="dialog-actions"><button type="button" className="btn-soft" onClick={() => setAssigningId(null)}>Cancel</button><button className="btn-app" disabled={actionLoading}>{actionLoading ? "Assigning..." : "Assign technician"}</button></div></form></div>}

      <ConfirmDialog open={Boolean(deleteId)} title="Delete this service record?" message="This pending record will be permanently removed. This action cannot be undone." confirmText={actionLoading ? "Deleting..." : "Delete record"} onCancel={() => setDeleteId(null)} onConfirm={remove} />
    </div>
  );
}
