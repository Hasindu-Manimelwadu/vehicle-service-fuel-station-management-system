import { useLocation, Link, Navigate } from "react-router-dom";

export default function BookingConfirmationPage() {
  const location = useLocation();
  const booking = location.state?.booking;
  const vehicle = location.state?.vehicle;
  const slot = location.state?.slot;

  if (!booking) {
    return <Navigate to="/my-bookings" replace />;
  }

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
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-8">
          <div className="card shadow border-0 text-center overflow-hidden">
            <div className="bg-success text-white py-5 px-4">
              <div
                className="rounded-circle bg-white text-success d-inline-flex align-items-center justify-content-center mb-3 shadow-sm"
                style={{ width: "80px", height: "80px", fontSize: "36px" }}
              >
                ✓
              </div>
              <h2 className="fw-bold mb-2">Booking Confirmed!</h2>
              <p className="lead mb-0 text-white-50">
                Your service appointment has been successfully scheduled.
              </p>
            </div>

            <div className="card-body p-4 text-start">
              <div className="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom">
                <div>
                  <span className="text-muted small text-uppercase">
                    Booking Reference
                  </span>
                  <h4 className="fw-bold text-primary mb-0 font-monospace">
                    #BK-{String(booking.bookingId).padStart(5, "0")}
                  </h4>
                </div>
                <div className="text-end">
                  <span className="text-muted small text-uppercase d-block mb-1">
                    Booking Status
                  </span>
                  <span className="badge bg-success fs-6 px-3 py-2">
                    {booking.bookingStatus || "CONFIRMED"}
                  </span>
                </div>
              </div>

              <div className="row g-3 mb-4">
                <div className="col-sm-6">
                  <div className="p-3 bg-light rounded">
                    <span className="text-muted small">Vehicle</span>
                    <div className="fw-bold">
                      {vehicle
                        ? `${vehicle.make} ${vehicle.model} (${vehicle.licensePlateNumber})`
                        : `Vehicle #${booking.vehicleId}`}
                    </div>
                  </div>
                </div>

                <div className="col-sm-6">
                  <div className="p-3 bg-light rounded">
                    <span className="text-muted small">Service Type</span>
                    <div className="fw-bold">{booking.serviceType}</div>
                  </div>
                </div>

                <div className="col-sm-6">
                  <div className="p-3 bg-light rounded">
                    <span className="text-muted small">Scheduled Date</span>
                    <div className="fw-bold">{booking.bookingDate}</div>
                  </div>
                </div>

                <div className="col-sm-6">
                  <div className="p-3 bg-light rounded">
                    <span className="text-muted small">Time Slot</span>
                    <div className="fw-bold">
                      {formatTime(booking.bookingTime)}
                      {slot ? ` – ${formatTime(slot.endTime)}` : ""}
                    </div>
                  </div>
                </div>

                {booking.description && (
                  <div className="col-12">
                    <div className="p-3 bg-light rounded">
                      <span className="text-muted small">Notes / Symptoms</span>
                      <div>{booking.description}</div>
                    </div>
                  </div>
                )}
              </div>

              <div className="alert alert-info d-flex align-items-center small">
                <span className="me-2 fs-5">ℹ️</span>
                <div>
                  Please arrive 10 minutes prior to your appointment time. Our
                  service technicians will be ready to inspect your vehicle.
                </div>
              </div>

              <div className="d-flex justify-content-center gap-3 mt-4">
                <Link to="/book" className="btn btn-outline-primary px-4">
                  Book Another Service
                </Link>
                <Link to="/my-bookings" className="btn btn-primary px-4">
                  View My Bookings
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
