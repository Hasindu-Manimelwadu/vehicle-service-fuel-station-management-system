import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import StatusBadge from "../components/StatusBadge";
import PageHeader from "../components/PageHeader";
import ConfirmDialog from "../components/ConfirmDialog";
import { useToast } from "../components/ToastContext";

function validateProgress(progress) {
  const errors = {};
  if (!progress.workPerformed.trim()) errors.workPerformed = "Work performed is required.";
  else if (progress.workPerformed.length > 5000) errors.workPerformed = "Maximum 5000 characters allowed.";
  if (progress.remarks.length > 1000) errors.remarks = "Maximum 1000 characters allowed.";
  return errors;
}

function validatePart(part) {
  const errors = {};
  if (part.partId) {
    const id = Number(part.partId);
    if (!Number.isInteger(id) || id <= 0) errors.partId = "Part ID must be a positive whole number.";
  }
  if (!part.partName.trim()) errors.partName = "Part name is required.";
  else if (part.partName.length > 120) errors.partName = "Maximum 120 characters allowed.";
  const quantity = Number(part.quantityUsed);
  if (!Number.isInteger(quantity) || quantity <= 0) errors.quantityUsed = "Quantity must be greater than zero.";
  return errors;
}

export default function TechnicianJobDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [record, setRecord] = useState(null);
  const [error, setError] = useState("");
  const [progress, setProgress] = useState({ workPerformed: "", remarks: "" });
  const [part, setPart] = useState({ partId: "", partName: "", quantityUsed: 1 });
  const [progressErrors, setProgressErrors] = useState({});
  const [partErrors, setPartErrors] = useState({});
  const [loadingAction, setLoadingAction] = useState("");
  const [confirmComplete, setConfirmComplete] = useState(false);

  const load = async () => {
    try {
      const response = await api.get(`/api/service-records/my-jobs/${id}`);
      setRecord(response.data);
      setProgress({ workPerformed: response.data.workPerformed || "", remarks: response.data.remarks || "" });
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Unable to load job");
    }
  };

  useEffect(() => { load(); }, [id]);

  const action = async (name, request, message) => {
    setLoadingAction(name);
    try {
      await request();
      await load();
      if (message) showToast(message);
    } catch (e) {
      showToast(e.response?.data?.message || "Operation failed", "error");
    } finally {
      setLoadingAction("");
    }
  };

  const saveProgress = (event) => {
    event.preventDefault();
    const errors = validateProgress(progress);
    setProgressErrors(errors);
    if (Object.keys(errors).length) {
      document.querySelector('[name="workPerformed"]')?.focus();
      showToast("Please fix the highlighted progress fields.", "error");
      return;
    }
    action(
      "progress",
      () => api.patch(`/api/service-records/${id}/progress`, {
        workPerformed: progress.workPerformed.trim(),
        remarks: progress.remarks.trim(),
      }),
      "Service progress updated."
    );
  };

  const addPart = async (event) => {
    event.preventDefault();
    const errors = validatePart(part);
    setPartErrors(errors);
    if (Object.keys(errors).length) {
      const first = Object.keys(errors)[0];
      document.querySelector(`[name="${first}"]`)?.focus();
      showToast("Please fix the highlighted part fields.", "error");
      return;
    }

    await action(
      "part",
      () => api.post(`/api/service-records/${id}/parts`, {
        partId: part.partId ? Number(part.partId) : null,
        partName: part.partName.trim(),
        quantityUsed: Number(part.quantityUsed),
      }),
      "Part usage recorded."
    );
    setPart({ partId: "", partName: "", quantityUsed: 1 });
    setPartErrors({});
  };

  const complete = async () => {
    setConfirmComplete(false);
    await action("complete", () => api.patch(`/api/service-records/${id}/complete`), "Service completed successfully.");
  };

  if (error) return <div className="page-container"><div className="notice notice-danger"><Icon name="alert" />{error}</div></div>;
  if (!record) return <div className="page-container"><div className="page-loading"><span className="loading-ring" />Loading service job...</div></div>;

  const activeIndex = record.serviceStatus === "PENDING" ? 0 : record.serviceStatus === "IN_PROGRESS" ? 1 : 2;

  return (
    <div className="page-container">
      <PageHeader
        eyebrow="Technician job"
        title={`Service Job #${record.serviceRecordId}`}
        subtitle={`Booking ${record.bookingId} • ${record.serviceDescription}`}
        actions={<button className="btn-soft" onClick={() => navigate("/technician/jobs")}><Icon name="arrowLeft" size={17} /> Back to jobs</button>}
      />

      <section className="job-hero fade-up delay-1">
        <div className="job-hero-title"><StatusBadge status={record.serviceStatus} /><h2>{record.taskDescription}</h2><p>{record.customerComplaint || "No customer complaint recorded."}</p></div>
        <div className="job-facts">
          <div><Icon name="mileage" size={19} /><span><small>Mileage</small><strong>{Number(record.mileage).toLocaleString()} km</strong></span></div>
          <div><Icon name="clipboard" size={19} /><span><small>Service</small><strong>{record.serviceDescription}</strong></span></div>
        </div>
        <div className="progress-stepper compact">
          {["Assigned", "In progress", "Completed"].map((label, index) => (
            <div className={`progress-step ${index <= activeIndex ? "done" : ""}`} key={label}>
              <span>{index < activeIndex ? <Icon name="check" size={15} /> : index + 1}</span><strong>{label}</strong>{index < 2 && <i />}
            </div>
          ))}
        </div>
      </section>

      {record.serviceStatus === "PENDING" && (
        <section className="action-panel fade-up delay-2">
          <div><h3>Ready to begin?</h3><p>Start the service when the vehicle and work area are ready.</p></div>
          <button className="btn-app" disabled={loadingAction === "start"} onClick={() => action("start", () => api.patch(`/api/service-records/${id}/start`), "Service started.")}>
            <Icon name="wrench" size={17} /> {loadingAction === "start" ? "Starting..." : "Start service"}
          </button>
        </section>
      )}

      {record.serviceStatus === "IN_PROGRESS" && (
        <div className="technician-work-grid fade-up delay-2">
          <section className="form-card">
            <div className="panel-head"><div><h3>Update service progress</h3><p>Record exactly what has been completed.</p></div></div>
            <form onSubmit={saveProgress} className="form-stack" noValidate>
              <label className={`field-group ${progressErrors.workPerformed ? "field-invalid" : ""}`}>
                <span>Work performed *</span>
                <textarea name="workPerformed" rows="5" maxLength="5000" value={progress.workPerformed} onChange={(e) => { setProgress({ ...progress, workPerformed: e.target.value }); setProgressErrors((current) => ({ ...current, workPerformed: "" })); }} placeholder="Describe completed inspection, repairs and maintenance..." aria-invalid={Boolean(progressErrors.workPerformed)} />
                <div className="field-meta-row">{progressErrors.workPerformed ? <small className="field-error">{progressErrors.workPerformed}</small> : <span />}<small className="char-count">{progress.workPerformed.length}/5000</small></div>
              </label>
              <label className={`field-group ${progressErrors.remarks ? "field-invalid" : ""}`}>
                <span>Technician remarks</span>
                <textarea name="progressRemarks" rows="3" maxLength="1000" value={progress.remarks} onChange={(e) => { setProgress({ ...progress, remarks: e.target.value }); setProgressErrors((current) => ({ ...current, remarks: "" })); }} placeholder="Optional follow-up notes" />
                <div className="field-meta-row">{progressErrors.remarks ? <small className="field-error">{progressErrors.remarks}</small> : <span />}<small className="char-count">{progress.remarks.length}/1000</small></div>
              </label>
              <button className="btn-app align-self-start" disabled={loadingAction === "progress"}>{loadingAction === "progress" ? "Saving..." : "Save progress"}</button>
            </form>
          </section>

          <section className="form-card">
            <div className="panel-head"><div><h3>Spare parts used</h3><p>Keep part usage attached to this service record.</p></div><span className="count-badge">{record.parts?.length || 0}</span></div>
            <form className="part-form enhanced-part-form" onSubmit={addPart} noValidate>
              <div className={partErrors.partId ? "part-field-invalid" : ""}><input name="partId" type="number" min="1" step="1" placeholder="Part ID" value={part.partId} onChange={(e) => { setPart({ ...part, partId: e.target.value }); setPartErrors((current) => ({ ...current, partId: "" })); }} />{partErrors.partId && <small className="field-error">{partErrors.partId}</small>}</div>
              <div className={partErrors.partName ? "part-field-invalid" : ""}><input name="partName" maxLength="120" placeholder="Part name" value={part.partName} onChange={(e) => { setPart({ ...part, partName: e.target.value }); setPartErrors((current) => ({ ...current, partName: "" })); }} />{partErrors.partName && <small className="field-error">{partErrors.partName}</small>}</div>
              <div className={partErrors.quantityUsed ? "part-field-invalid" : ""}><input name="quantityUsed" type="number" min="1" step="1" aria-label="Quantity used" value={part.quantityUsed} onChange={(e) => { setPart({ ...part, quantityUsed: e.target.value }); setPartErrors((current) => ({ ...current, quantityUsed: "" })); }} />{partErrors.quantityUsed && <small className="field-error">{partErrors.quantityUsed}</small>}</div>
              <button className="btn-soft" disabled={loadingAction === "part"}><Icon name="plus" size={16} /> Add</button>
            </form>

            <div className="parts-list compact-parts">
              {record.parts?.length ? record.parts.map((p) => (
                <div className="part-row" key={p.usageId}>
                  <span className="part-icon"><Icon name="parts" size={18} /></span>
                  <div><strong>{p.partName}</strong><small>Quantity used: {p.quantityUsed}</small></div>
                  <button className="table-action danger" title="Remove part" onClick={() => action(`remove-${p.usageId}`, () => api.delete(`/api/service-records/${id}/parts/${p.usageId}`), "Part removed.")}><Icon name="trash" size={16} /></button>
                </div>
              )) : <div className="empty-inline"><Icon name="parts" size={24} /><span>No parts recorded yet.</span></div>}
            </div>
          </section>

          <section className="complete-panel full-span">
            <div><span className="complete-icon"><Icon name="check" size={23} /></span><div><h3>Finish this service</h3><p>Complete the job only after the work performed is fully recorded.</p></div></div>
            <button className="btn-app" onClick={() => setConfirmComplete(true)}><Icon name="check" size={17} /> Complete service</button>
          </section>
        </div>
      )}

      {record.serviceStatus === "COMPLETED" && (
        <section className="completed-panel fade-up delay-2">
          <span className="completed-icon"><Icon name="check" size={30} /></span>
          <div><h3>Service completed</h3><p>This record is locked for technician editing and preserved as completed service history.</p>{record.workPerformed && <div className="completed-work"><small>Work performed</small><span>{record.workPerformed}</span></div>}</div>
        </section>
      )}

      <ConfirmDialog
        open={confirmComplete}
        tone="success"
        title="Complete this service?"
        message="Confirm that all work performed and spare parts used have been recorded. The technician record will become read-only after completion."
        confirmText={loadingAction === "complete" ? "Completing..." : "Complete service"}
        onCancel={() => setConfirmComplete(false)}
        onConfirm={complete}
      />
    </div>
  );
}
