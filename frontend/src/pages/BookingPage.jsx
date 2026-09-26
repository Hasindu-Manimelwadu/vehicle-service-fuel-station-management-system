import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import VehicleSelector from "../components/booking/VehicleSelector";
import ServiceTypeSelector from "../components/booking/ServiceTypeSelector";
import SlotSelector from "../components/booking/SlotSelector";
import BookingSummaryCard from "../components/booking/BookingSummaryCard";

export default function BookingPage() {
  const navigate = useNavigate();

  // Step state (1: Vehicle, 2: Service, 3: Slot, 4: Notes, 5: Summary)
  const [currentStep, setCurrentStep] = useState(1);

  // Form draft state
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [selectedServiceType, setSelectedServiceType] = useState("");
  const [selectedDate, setSelectedDate] = useState(() => {
    const today = new Date();
    return today.toISOString().split("T")[0];
  });
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [description, setDescription] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Handler for requirement 11: Cancel before confirmation
  // - Do not call a cancellation API.
  // - Do not save a booking.
  // - Clear the draft and return to the booking page.
  const handleCancelDraft = () => {
    setSelectedVehicle(null);
    setSelectedServiceType("");
    setSelectedSlot(null);
    setDescription("");
    setErrorMessage("");
    setCurrentStep(1);
  };

  // Handler for requirement 12: Confirm Booking
  const handleConfirmBooking = async () => {
    if (!selectedVehicle || !selectedServiceType || !selectedSlot) {
      setErrorMessage("Please complete all required booking steps.");
      return;
    }

    setSubmitting(true);
    setErrorMessage("");

    try {
      const payload = {
        vehicleId: selectedVehicle.id,
        scheduleId: selectedSlot.scheduleId,
        serviceType: selectedServiceType,
        description: description.trim() || null,
      };

      const response = await api.post("/api/bookings", payload);

      // On successful creation directly as CONFIRMED:
      // Navigate to confirmation page passing the created booking data
      navigate("/confirmation", {
        state: {
          booking: response.data,
          vehicle: selectedVehicle,
          slot: selectedSlot,
        },
      });
    } catch (err) {
      console.error("Booking error:", err);
      if (err.response?.status === 409) {
        setErrorMessage(
          err.response.data?.message ||
            "The selected slot was just booked by another customer. Please choose another slot."
        );
      } else if (err.response?.status === 403) {
        setErrorMessage(
          err.response.data?.message ||
            "Vehicle ownership validation failed. You can only book for your own vehicles."
        );
      } else {
        setErrorMessage(
          err.response?.data?.message ||
            "An error occurred while confirming your booking. Please try again."
        );
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-10">
          {/* Header */}
          <div className="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 className="fw-bold mb-1">Book a Vehicle Service</h2>
              <p className="text-muted mb-0">
                Reserve your inspection, maintenance, or service appointment online.
              </p>
            </div>
            {currentStep > 1 && (
              <button
                className="btn btn-outline-secondary btn-sm"
                onClick={handleCancelDraft}
              >
                Reset Draft
              </button>
            )}
          </div>

          {/* Wizard Progress Steps Indicator */}
          <div className="card shadow-sm border-0 mb-4">
            <div className="card-body p-3">
              <div className="d-flex justify-content-between align-items-center position-relative">
                <div
                  className={`text-center flex-fill ${
                    currentStep >= 1 ? "text-primary fw-bold" : "text-muted"
                  }`}
                >
                  <span
                    className={`badge rounded-pill mb-1 ${
                      currentStep >= 1 ? "bg-primary" : "bg-secondary"
                    }`}
                  >
                    1
                  </span>
                  <div className="small">Vehicle</div>
                </div>

                <div
                  className={`text-center flex-fill ${
                    currentStep >= 2 ? "text-primary fw-bold" : "text-muted"
                  }`}
                >
                  <span
                    className={`badge rounded-pill mb-1 ${
                      currentStep >= 2 ? "bg-primary" : "bg-secondary"
                    }`}
                  >
                    2
                  </span>
                  <div className="small">Service</div>
                </div>

                <div
                  className={`text-center flex-fill ${
                    currentStep >= 3 ? "text-primary fw-bold" : "text-muted"
                  }`}
                >
                  <span
                    className={`badge rounded-pill mb-1 ${
                      currentStep >= 3 ? "bg-primary" : "bg-secondary"
                    }`}
                  >
                    3
                  </span>
                  <div className="small">Date & Slot</div>
                </div>

                <div
                  className={`text-center flex-fill ${
                    currentStep >= 4 ? "text-primary fw-bold" : "text-muted"
                  }`}
                >
                  <span
                    className={`badge rounded-pill mb-1 ${
                      currentStep >= 4 ? "bg-primary" : "bg-secondary"
                    }`}
                  >
                    4
                  </span>
                  <div className="small">Notes</div>
                </div>

                <div
                  className={`text-center flex-fill ${
                    currentStep >= 5 ? "text-primary fw-bold" : "text-muted"
                  }`}
                >
                  <span
                    className={`badge rounded-pill mb-1 ${
                      currentStep >= 5 ? "bg-primary" : "bg-secondary"
                    }`}
                  >
                    5
                  </span>
                  <div className="small">Review & Confirm</div>
                </div>
              </div>
            </div>
          </div>

          {/* Wizard Step Content */}
          <div className="card shadow-sm border-0 mb-4">
            <div className="card-body p-4">
              {currentStep === 1 && (
                <div>
                  <VehicleSelector
                    selectedVehicle={selectedVehicle}
                    onSelectVehicle={(v) => setSelectedVehicle(v)}
                  />
                  <div className="d-flex justify-content-end mt-4">
                    <button
                      type="button"
                      className="btn btn-primary px-4"
                      disabled={!selectedVehicle}
                      onClick={() => setCurrentStep(2)}
                    >
                      Continue to Service Type →
                    </button>
                  </div>
                </div>
              )}

              {currentStep === 2 && (
                <div>
                  <ServiceTypeSelector
                    selectedServiceType={selectedServiceType}
                    onSelectServiceType={(type) => setSelectedServiceType(type)}
                  />
                  <div className="d-flex justify-content-between mt-4">
                    <button
                      type="button"
                      className="btn btn-outline-secondary px-4"
                      onClick={() => setCurrentStep(1)}
                    >
                      ← Back
                    </button>
                    <button
                      type="button"
                      className="btn btn-primary px-4"
                      disabled={!selectedServiceType}
                      onClick={() => setCurrentStep(3)}
                    >
                      Continue to Date & Slot →
                    </button>
                  </div>
                </div>
              )}

              {currentStep === 3 && (
                <div>
                  <SlotSelector
                    selectedDate={selectedDate}
                    onDateChange={(d) => {
                      setSelectedDate(d);
                      setSelectedSlot(null);
                    }}
                    selectedSlot={selectedSlot}
                    onSelectSlot={(slot) => setSelectedSlot(slot)}
                  />
                  <div className="d-flex justify-content-between mt-4">
                    <button
                      type="button"
                      className="btn btn-outline-secondary px-4"
                      onClick={() => setCurrentStep(2)}
                    >
                      ← Back
                    </button>
                    <button
                      type="button"
                      className="btn btn-primary px-4"
                      disabled={!selectedSlot}
                      onClick={() => setCurrentStep(4)}
                    >
                      Continue to Notes →
                    </button>
                  </div>
                </div>
              )}

              {currentStep === 4 && (
                <div>
                  <h5 className="fw-semibold mb-3">Step 4: Additional Notes (Optional)</h5>
                  <p className="text-muted small">
                    Provide any specific requests, strange sounds, or symptoms you would like our technicians to investigate.
                  </p>
                  <div className="mb-3">
                    <textarea
                      className="form-control"
                      rows="4"
                      maxLength="500"
                      placeholder="e.g. Please check squeaking sound when braking above 40km/h..."
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                    ></textarea>
                    <div className="form-text text-end">
                      {description.length}/500 characters
                    </div>
                  </div>

                  <div className="d-flex justify-content-between mt-4">
                    <button
                      type="button"
                      className="btn btn-outline-secondary px-4"
                      onClick={() => setCurrentStep(3)}
                    >
                      ← Back
                    </button>
                    <button
                      type="button"
                      className="btn btn-primary px-4"
                      onClick={() => setCurrentStep(5)}
                    >
                      Review Booking Summary →
                    </button>
                  </div>
                </div>
              )}

              {currentStep === 5 && (
                <BookingSummaryCard
                  vehicle={selectedVehicle}
                  serviceType={selectedServiceType}
                  date={selectedDate}
                  slot={selectedSlot}
                  description={description}
                  onConfirm={handleConfirmBooking}
                  onCancel={handleCancelDraft}
                  submitting={submitting}
                  errorMessage={errorMessage}
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
