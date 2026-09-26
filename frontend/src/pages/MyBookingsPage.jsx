import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [editSlots, setEditSlots] = useState([]);
  const [loadingEditSlots, setLoadingEditSlots] = useState(false);

  const serviceTypes = [
    "OIL_CHANGE",
    "TIRE_ROTATION",
    "BRAKE_INSPECTION",
    "GENERAL_SERVICE",
    "FULL_SERVICE",
  ];

  useEffect(() => {
    let isActive = true;

    const loadBookings = async () => {
      setLoading(true);
      setError("");

      try {
        const res = await api.get("/api/bookings/my");
        if (isActive) {
          setBookings(Array.isArray(res.data) ? res.data : []);
        }
      } catch (err) {
        if (isActive) {
          console.error("Error fetching bookings:", err);
          setError("Failed to load your service bookings. Please try again.");
        }
      } finally {
        if (isActive) {
          setLoading(false);
        }
      }
    };

    loadBookings();

    return () => {
      isActive = false;
    };
  }, []);

  const editingBookingDate = editing?.bookingDate;

  useEffect(() => {
    if (!editingBookingDate) return;

    let isActive = true;

    const loadEditSlots = async () => {
      setLoadingEditSlots(true);
      try {
        const res = await api.get("/api/schedules/available", {
          params: { date: editingBookingDate },
        });
        if (isActive) {
          setEditSlots(Array.isArray(res.data) ? res.data : []);
        }
      } catch {
        if (isActive) {
          setEditSlots([]);
        }
      } finally {
        if (isActive) {
          setLoadingEditSlots(false);
        }
      }
    };

    loadEditSlots();

    return () => {
      isActive = false;
    };
  }, [editingBookingDate]);

  const beginEdit = (booking) => {
    setError("");
    setSuccess("");
    setEditing({ ...booking, description: booking.description || "" });
    setEditSlots([]);
  };

  const saveEdit = async () => {
    if (!editing) return;
    setSaving(true);
    setError("");
    try {
      const payload = {
        vehicleId: editing.vehicleId,
        scheduleId: editing.scheduleId,
        serviceType: editing.serviceType,
        description: editing.description.trim() || null,
      };
      const response = await api.put(`/api/bookings/${editing.bookingId}`, payload);
      setBookings((items) =>
        items.map((item) =>
          item.bookingId === editing.bookingId ? response.data : item
        )
      );
      setEditing(null);
      setSuccess("Booking updated successfully.");
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update the booking.");
    } finally {
      setSaving(false);
    }
  };

  const deleteBooking = async (bookingId) => {
    if (!window.confirm("Permanently delete this booking?")) return;
    setDeletingId(bookingId);
    setError("");
    setSuccess("");
    try {
      await api.delete(`/api/bookings/${bookingId}`);
      setBookings((items) => items.filter((item) => item.bookingId !== bookingId));
      setSuccess("Booking deleted successfully. Its schedule slot is available again.");
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete the booking.");
    } finally {
      setDeletingId(null);
    }
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
    <div className="container py-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold mb-1">My Service Bookings</h2>
          <p className="text-muted mb-0">
            View your scheduled vehicle maintenance and service appointments.
          </p>
        </div>
        <Link to="/book" className="btn btn-primary">
          + Book New Service
        </Link>
      </div>

      {error && <div className="alert alert-danger mb-4">{error}</div>}
      {success && <div className="alert alert-success mb-4">{success}</div>}

      {editing && (
        <div className="card shadow-sm border-primary mb-4">
          <div className="card-body">
            <h5 className="fw-bold">Edit Booking #BK-{String(editing.bookingId).padStart(5, "0")}</h5>
            <div className="row g-3 mt-1">
              <div className="col-md-5">
                <label className="form-label">Service Type</label>
                <select
                  className="form-select"
                  value={editing.serviceType}
                  onChange={(e) => setEditing({ ...editing, serviceType: e.target.value })}
                >
                  {serviceTypes.map((type) => (
                    <option key={type} value={type}>{type.replaceAll("_", " ")}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-7">
                <label className="form-label">Notes</label>
                <textarea
                  className="form-control"
                  rows="2"
                  maxLength="500"
                  value={editing.description}
                  onChange={(e) => setEditing({ ...editing, description: e.target.value })}
                />
              </div>
              <div className="col-md-5">
                <label className="form-label">Service Date</label>
                <input
                  type="date"
                  className="form-control"
                  min={new Date().toISOString().split("T")[0]}
                  value={editing.bookingDate}
                  onChange={(e) =>
                    setEditing({ ...editing, bookingDate: e.target.value, scheduleId: "" })
                  }
                />
              </div>
              <div className="col-md-7">
                <label className="form-label">Time Slot</label>
                <select
                  className="form-select"
                  value={editing.scheduleId}
                  onChange={(e) => setEditing({ ...editing, scheduleId: Number(e.target.value) })}
                  disabled={loadingEditSlots}
                >
                  {editing.scheduleId && (
                    <option value={editing.scheduleId}>
                      Current: {formatTime(editing.bookingTime)}
                    </option>
                  )}
                  {!editing.scheduleId && <option value="">Select an available slot</option>}
                  {editSlots.map((slot) => (
                    <option key={slot.scheduleId} value={slot.scheduleId}>
                      {formatTime(slot.startTime)} - {formatTime(slot.endTime)}
                    </option>
                  ))}
                </select>
                {loadingEditSlots && <div className="form-text">Loading available slots...</div>}
              </div>
            </div>
            <div className="d-flex gap-2 justify-content-end mt-3">
              <button className="btn btn-outline-secondary" onClick={() => setEditing(null)} disabled={saving}>Close</button>
              <button className="btn btn-primary" onClick={saveEdit} disabled={saving || !editing.scheduleId}>
                {saving ? "Saving..." : "Save Changes"}
              </button>
            </div>
          </div>
        </div>
      )}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading bookings...</span>
          </div>
          <p className="text-muted mt-2">Loading your bookings...</p>
        </div>
      ) : bookings.length === 0 ? (
        <div className="card shadow-sm border-0 text-center py-5">
          <div className="card-body">
            <div className="fs-1 mb-3">📅</div>
            <h4 className="fw-semibold">No Service Bookings Yet</h4>
            <p className="text-muted">
              You do not have any confirmed service bookings at the moment.
            </p>
            <Link to="/book" className="btn btn-primary px-4 mt-2">
              Book Your First Service
            </Link>
          </div>
        </div>
      ) : (
        <div className="row g-4">
          {bookings.map((booking) => (
            <div key={booking.bookingId} className="col-md-6 col-lg-4">
              <div className="card shadow-sm border-0 h-100">
                <div className="card-header bg-white border-bottom-0 pt-3 pb-0 d-flex justify-content-between align-items-center">
                  <span className="font-monospace text-primary fw-bold">
                    #BK-{String(booking.bookingId).padStart(5, "0")}
                  </span>
                  <span className="badge bg-success px-2 py-1">
                    {booking.bookingStatus || "CONFIRMED"}
                  </span>
                </div>

                <div className="card-body">
                  <h5 className="card-title fw-bold mb-1">
                    {booking.serviceType}
                  </h5>

                  <div className="text-muted small mb-3">
                    Vehicle ID: #{booking.vehicleId}
                  </div>

                  <div className="p-2 bg-light rounded mb-3">
                    <div className="d-flex justify-content-between text-muted small">
                      <span>Date:</span>
                      <strong className="text-dark">
                        {booking.bookingDate}
                      </strong>
                    </div>
                    <div className="d-flex justify-content-between text-muted small mt-1">
                      <span>Time:</span>
                      <strong className="text-dark">
                        {formatTime(booking.bookingTime)}
                      </strong>
                    </div>
                  </div>

                  {booking.description && (
                    <div className="small text-muted mb-2">
                      <strong className="text-dark d-block">Notes:</strong>
                      <span>{booking.description}</span>
                    </div>
                  )}
                </div>

                <div className="card-footer bg-light border-0 py-2">
                  <div className="d-flex justify-content-between align-items-center gap-2">
                    <span className="text-muted small">Slot #{booking.scheduleId}</span>
                    <div className="d-flex gap-2">
                      <button className="btn btn-sm btn-outline-primary" onClick={() => beginEdit(booking)}>
                        Edit
                      </button>
                      <button
                        className="btn btn-sm btn-outline-danger"
                        onClick={() => deleteBooking(booking.bookingId)}
                        disabled={deletingId === booking.bookingId}
                      >
                        {deletingId === booking.bookingId ? "Deleting..." : "Delete"}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
