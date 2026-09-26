export default function BookingSummaryCard({
  vehicle,
  serviceType,
  date,
  slot,
  description,
  onConfirm,
  onCancel,
  submitting,
  errorMessage,
}) {
  const serviceLabels = {
    OIL_CHANGE: "Oil Change & Filter",
    TIRE_ROTATION: "Tire Rotation & Balance",
    BRAKE_INSPECTION: "Brake Inspection",
    GENERAL_SERVICE: "General Maintenance",
    FULL_SERVICE: "Full Comprehensive Service",
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return "";
    const parts = timeStr.split(":");
    const hour = parseInt(parts[0], 10);
    const minute = parts[1] || "00";
    const ampm = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:${minute} ${ampm}`;
  };

  return (
    <div className="card shadow-sm border-0">
      <div className="card-header bg-primary text-white py-3">
        <h5 className="mb-0 fw-bold">Step 5: Review Booking Summary</h5>
      </div>
      <div className="card-body p-4">
        {errorMessage && (
          <div className="alert alert-danger d-flex align-items-center mb-4">
            <span className="me-2">⚠️</span>
            <div>{errorMessage}</div>
          </div>
        )}

        <div className="row g-4">
          <div className="col-md-6">
            <div className="p-3 bg-light rounded-3">
              <span className="text-muted small text-uppercase fw-bold">
                Vehicle Details
              </span>
              <h6 className="fw-bold mt-1 mb-1">
                {vehicle?.make} {vehicle?.model}
              </h6>
              <div className="small text-muted">
                Plate:{" "}
                <span className="badge bg-dark font-monospace">
                  {vehicle?.licensePlateNumber}
                </span>
                {vehicle?.year && ` • ${vehicle?.year}`}
                {vehicle?.color && ` • ${vehicle?.color}`}
              </div>
            </div>
          </div>

          <div className="col-md-6">
            <div className="p-3 bg-light rounded-3">
              <span className="text-muted small text-uppercase fw-bold">
                Service Selected
              </span>
              <h6 className="fw-bold mt-1 mb-1">
                {serviceLabels[serviceType] || serviceType}
              </h6>
              <div className="small text-muted">
                Direct booking status upon confirmation:{" "}
                <span className="badge bg-success">CONFIRMED</span>
              </div>
            </div>
          </div>

          <div className="col-md-6">
            <div className="p-3 bg-light rounded-3">
              <span className="text-muted small text-uppercase fw-bold">
                Date & Time Slot
              </span>
              <h6 className="fw-bold mt-1 mb-1">{date}</h6>
              <div className="small text-muted">
                {formatTime(slot?.startTime)} – {formatTime(slot?.endTime)}
              </div>
            </div>
          </div>

          <div className="col-md-6">
            <div className="p-3 bg-light rounded-3">
              <span className="text-muted small text-uppercase fw-bold">
                Special Instructions / Notes
              </span>
              <p className="mb-0 mt-1 small">
                {description ? description : <em>No additional notes provided</em>}
              </p>
            </div>
          </div>
        </div>

        <hr className="my-4" />

        <div className="d-flex justify-content-between align-items-center">
          <button
            type="button"
            className="btn btn-outline-secondary px-4"
            onClick={onCancel}
            disabled={submitting}
          >
            Cancel Draft
          </button>

          <button
            type="button"
            className="btn btn-success btn-lg px-4 d-flex align-items-center gap-2"
            onClick={onConfirm}
            disabled={submitting}
          >
            {submitting && (
              <span
                className="spinner-border spinner-border-sm"
                role="status"
                aria-hidden="true"
              ></span>
            )}
            <span>Confirm Booking</span>
          </button>
        </div>
      </div>
    </div>
  );
}
