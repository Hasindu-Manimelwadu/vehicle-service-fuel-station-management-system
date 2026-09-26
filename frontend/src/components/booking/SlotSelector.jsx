import { useEffect, useState } from "react";
import api from "../../api/api";

export default function SlotSelector({
  selectedDate,
  onDateChange,
  selectedSlot,
  onSelectSlot,
}) {
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const minDate = new Date().toISOString().split("T")[0];

  useEffect(() => {
    let isMounted = true;

    const fetchSlots = async () => {
      if (!selectedDate) {
        if (isMounted) {
          setSlots([]);
        }
        return;
      }

      setLoading(true);
      setError("");

      try {
        const res = await api.get(`/api/schedules/available?date=${selectedDate}`);
        if (isMounted) {
          setSlots(Array.isArray(res.data) ? res.data : []);
        }
      } catch (err) {
        if (isMounted) {
          console.error("Failed to load slots:", err);
          setError("Unable to load schedule slots for the selected date.");
          setSlots([]);
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchSlots();

    return () => {
      isMounted = false;
    };
  }, [selectedDate]);

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
    <div>
      <h5 className="fw-semibold mb-3">Step 3: Select Date & Available Slot</h5>
      <p className="text-muted small">
        Pick your preferred service date and choose from the available time slots.
      </p>

      <div className="row mb-4">
        <div className="col-md-5 col-lg-4">
          <label className="form-label fw-semibold">Service Date</label>
          <input
            type="date"
            className="form-control"
            min={minDate}
            value={selectedDate}
            onChange={(e) => onDateChange(e.target.value)}
          />
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div className="text-center py-4">
          <div className="spinner-border spinner-border-sm text-primary me-2"></div>
          <span>Checking available slots...</span>
        </div>
      ) : slots.length === 0 ? (
        <div className="alert alert-info d-flex align-items-center justify-content-between">
          <div>
            <strong>No slots found for {selectedDate}.</strong>
            <p className="mb-0 small text-muted">
              Try choosing another date or contact support to open additional service slots.
            </p>
          </div>
        </div>
      ) : (
        <div>
          <label className="form-label fw-semibold mb-2">
            Available Time Slots ({slots.length} available)
          </label>
          <div className="row g-2">
            {slots.map((slot) => {
              const isSelected = selectedSlot?.scheduleId === slot.scheduleId;
              return (
                <div key={slot.scheduleId} className="col-6 col-md-4 col-lg-3">
                  <button
                    type="button"
                    className={`btn w-100 p-2 text-center border-2 ${
                      isSelected
                        ? "btn-primary shadow-sm"
                        : "btn-outline-secondary"
                    }`}
                    onClick={() => onSelectSlot(slot)}
                  >
                    <div className="fw-bold">{formatTime(slot.startTime)}</div>
                    <small className="d-block text-opacity-75">
                      to {formatTime(slot.endTime)}
                    </small>
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
