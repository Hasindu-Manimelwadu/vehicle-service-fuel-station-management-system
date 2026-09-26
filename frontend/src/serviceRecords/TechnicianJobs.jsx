import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import StatusBadge from "../components/StatusBadge";
import PageHeader from "../components/PageHeader";

export default function TechnicianJobs() {
  const [jobs, setJobs] = useState([]); const [error, setError] = useState(""); const [query, setQuery] = useState(""); const [filter, setFilter] = useState("ALL"); const [loading, setLoading] = useState(true);
  useEffect(() => { api.get("/api/service-records/my-jobs").then((r) => setJobs(r.data)).catch((e) => setError(e.response?.data?.message || "Unable to load assigned jobs")).finally(() => setLoading(false)); }, []);
  const filtered = useMemo(() => jobs.filter((job) => (`${job.serviceRecordId} ${job.bookingId} ${job.taskDescription || ""} ${job.serviceDescription || ""}`).toLowerCase().includes(query.toLowerCase()) && (filter === "ALL" || job.serviceStatus === filter)), [jobs, query, filter]);
  return <div className="page-container"><PageHeader eyebrow="Technician workspace" title="My Assigned Jobs" subtitle="Review your assigned work, update progress and complete services." />{error && <div className="notice notice-danger"><Icon name="alert" size={18}/>{error}</div>}
    <div className="jobs-toolbar fade-up delay-1"><div className="search-box"><Icon name="search" size={18}/><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search assigned jobs..."/></div><div className="filter-tabs">{["ALL","PENDING","IN_PROGRESS","COMPLETED"].map((item) => <button className={filter === item ? "active" : ""} key={item} onClick={() => setFilter(item)}>{item === "IN_PROGRESS" ? "In progress" : item[0] + item.slice(1).toLowerCase()}</button>)}</div></div>
    {loading ? <div className="page-loading"><span className="loading-ring"/>Loading assigned jobs...</div> : <div className="job-grid fade-up delay-2">{filtered.map((job) => <Link className="job-card" to={`/technician/jobs/${job.serviceRecordId}`} key={job.serviceRecordId}><div className="job-card-top"><span className="record-id">#{job.serviceRecordId}</span><StatusBadge status={job.serviceStatus}/></div><h3>{job.serviceDescription}</h3><p>{job.taskDescription || "No task description provided."}</p><div className="job-meta"><span><Icon name="clipboard" size={16}/> Booking {job.bookingId}</span><span><Icon name="mileage" size={16}/> {Number(job.mileage || 0).toLocaleString()} km</span></div><div className="job-card-foot"><span>Open service job</span><Icon name="chevronRight" size={18}/></div></Link>)}{filtered.length === 0 && <div className="empty-panel full-span"><span className="empty-icon"><Icon name="wrench" size={29}/></span><h3>No jobs found</h3><p>There are no assigned jobs matching your current filter.</p></div>}</div>}
  </div>;
}
