import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import StatusBadge from "../components/StatusBadge";
import PageHeader from "../components/PageHeader";
import ActivityTimeline from "../components/ActivityTimeline";

export default function ServiceRecordDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [record, setRecord] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get(`/api/service-records/${id}`)
      .then((response) => setRecord(response.data))
      .catch((e) => setError(e.response?.data?.message || "Unable to load record"));
  }, [id]);

  if (error) return <div className="page-container"><div className="notice notice-danger"><Icon name="alert" />{error}</div></div>;
  if (!record) return <div className="page-container"><div className="page-loading"><span className="loading-ring" />Loading service record...</div></div>;

  const steps = ["PENDING", "IN_PROGRESS", "COMPLETED"];
  const activeIndex = steps.indexOf(record.serviceStatus);
  const canPrint = record.serviceStatus === "COMPLETED";

  return (
    <div className="page-container service-record-print-root">
      <div className="print-service-header" aria-hidden="true">
        <div><strong>AutoCare</strong><span>Service Station</span></div>
        <small>Service summary • Record #{record.serviceRecordId}</small>
      </div>

      <PageHeader
        eyebrow="Service record"
        title={`Record #${record.serviceRecordId}`}
        subtitle={`Booking reference ${record.bookingId}`}
        actions={
          <div className="header-action-group no-print">
            {canPrint && (
              <button className="btn-soft" onClick={() => window.print()}>
                <Icon name="print" size={17} /> Print summary
              </button>
            )}
            <button className="btn-soft" onClick={() => navigate("/service-records")}>
              <Icon name="arrowLeft" size={17} /> Back to records
            </button>
          </div>
        }
      />

      <section className="record-overview fade-up delay-1 print-keep-together">
        <div className="record-title-row">
          <div><StatusBadge status={record.serviceStatus} /><h2>{record.serviceDescription}</h2></div>
          <div className="record-meta">
            <div><Icon name="mileage" size={18} /><span><small>Mileage</small><strong>{Number(record.mileage).toLocaleString()} km</strong></span></div>
            <div><Icon name="user" size={18} /><span><small>Technician</small><strong>{record.technicianName || "Not assigned"}</strong></span></div>
          </div>
        </div>
        <div className="progress-stepper">
          {steps.map((step, index) => (
            <div className={`progress-step ${index <= activeIndex ? "done" : ""}`} key={step}>
              <span>{index < activeIndex ? <Icon name="check" size={15} /> : index + 1}</span>
              <strong>{step === "IN_PROGRESS" ? "In progress" : step[0] + step.slice(1).toLowerCase()}</strong>
              {index < steps.length - 1 && <i />}
            </div>
          ))}
        </div>
      </section>

      <div className="details-grid fade-up delay-2">
        <section className="detail-card print-keep-together">
          <h3>Service details</h3>
          <dl className="detail-list">
            <div><dt>Customer complaint</dt><dd>{record.customerComplaint || "No complaint recorded"}</dd></div>
            <div><dt>Task description</dt><dd>{record.taskDescription || "No technician task assigned"}</dd></div>
            <div><dt>Work performed</dt><dd>{record.workPerformed || "Work has not been recorded yet"}</dd></div>
            <div><dt>Remarks</dt><dd>{record.remarks || "No remarks"}</dd></div>
          </dl>
        </section>

        <section className="detail-card print-keep-together">
          <div className="panel-head">
            <div><h3>Parts used</h3><p>Parts recorded against this service.</p></div>
            <span className="count-badge">{record.parts?.length || 0}</span>
          </div>
          {record.parts?.length ? (
            <div className="parts-list">
              {record.parts.map((part) => (
                <div className="part-row" key={part.usageId}>
                  <span className="part-icon"><Icon name="parts" size={18} /></span>
                  <div><strong>{part.partName}</strong><small>{part.partId ? `Part ID ${part.partId}` : "Service part"}</small></div>
                  <b>× {part.quantityUsed}</b>
                </div>
              ))}
            </div>
          ) : <div className="empty-inline"><Icon name="parts" size={25} /><span>No parts recorded for this service.</span></div>}
        </section>
      </div>

      <div className="fade-up delay-3"><ActivityTimeline record={record} /></div>

      {canPrint && (
        <div className="print-signoff" aria-hidden="true">
          <div><span>Technician</span><strong>{record.technicianName || "—"}</strong></div>
          <div><span>Status</span><strong>Completed</strong></div>
          <div><span>Booking</span><strong>#{record.bookingId}</strong></div>
        </div>
      )}
    </div>
  );
}
