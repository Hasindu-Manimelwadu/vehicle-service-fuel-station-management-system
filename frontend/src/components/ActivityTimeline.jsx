import Icon from "./Icon";

function formatDateTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

function latestPartDate(parts = []) {
  const dates = parts
    .map((part) => part.recordedAt)
    .filter(Boolean)
    .map((value) => new Date(value))
    .filter((date) => !Number.isNaN(date.getTime()));

  if (!dates.length) return null;
  return new Date(Math.max(...dates.map((date) => date.getTime()))).toISOString();
}

export default function ActivityTimeline({ record }) {
  const partDate = latestPartDate(record.parts);
  const events = [
    {
      title: "Service record created",
      description: `Booking reference ${record.bookingId} was added to the workshop queue.`,
      time: record.createdAt || record.serviceDate,
      done: true,
      icon: "clipboard",
    },
    {
      title: "Technician assigned",
      description: record.technicianName
        ? `${record.technicianName} is responsible for this service.`
        : "A technician has not been assigned yet.",
      done: Boolean(record.technicianName),
      icon: "user",
    },
    {
      title: "Service started",
      description: record.startDate
        ? "The technician moved the job into active work."
        : "Waiting for the technician to start the service.",
      time: record.startDate,
      done: Boolean(record.startDate),
      icon: "wrench",
    },
    {
      title: "Work and parts recorded",
      description: record.parts?.length
        ? `${record.parts.length} part ${record.parts.length === 1 ? "entry" : "entries"} attached to this service.`
        : record.workPerformed
          ? "Work performed details have been recorded."
          : "No completed work or parts usage has been recorded yet.",
      time: partDate || (record.workPerformed ? record.updatedAt : null),
      done: Boolean(record.workPerformed || record.parts?.length),
      icon: "parts",
    },
    {
      title: "Service completed",
      description: record.completionDate
        ? "The service was completed and the technician record became read-only."
        : "Completion will lock the technician workflow for this record.",
      time: record.completionDate,
      done: Boolean(record.completionDate),
      icon: "check",
    },
  ];

  const firstPending = events.findIndex((event) => !event.done);

  return (
    <section className="detail-card timeline-card print-keep-together">
      <div className="panel-head">
        <div>
          <h3>Service activity</h3>
          <p>A clear operational history of this service record.</p>
        </div>
        <span className="timeline-live-pill"><span /> Workflow</span>
      </div>

      <div className="activity-timeline">
        {events.map((event, index) => {
          const current = !event.done && index === firstPending;
          return (
            <div
              className={`timeline-event ${event.done ? "done" : "pending"} ${current ? "current" : ""}`}
              key={event.title}
            >
              <div className="timeline-rail" aria-hidden="true">
                <span className="timeline-node"><Icon name={event.icon} size={15} /></span>
                {index < events.length - 1 && <i />}
              </div>
              <div className="timeline-copy">
                <div className="timeline-title-row">
                  <strong>{event.title}</strong>
                  {event.time && <time>{formatDateTime(event.time)}</time>}
                  {current && <span className="timeline-now">Current</span>}
                </div>
                <p>{event.description}</p>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
