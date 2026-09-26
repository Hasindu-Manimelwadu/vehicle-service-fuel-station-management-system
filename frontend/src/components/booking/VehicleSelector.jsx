import { useEffect, useState } from "react";
import api from "../../api/api";


export default function VehicleSelector({
  selectedVehicle,
  onSelectVehicle,
}) {
  const customerId = localStorage.getItem("userId");

  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isActive = true;

    const loadCustomerVehicles = async () => {
      if (!customerId) {
        if (isActive) {
          setVehicles([]);
          setError(
            "Customer account information is unavailable. Please sign in again."
          );
          setLoading(false);
        }

        return;
      }

      setLoading(true);
      setError("");

      try {
        const response = await api.get("/api/vehicles/my");

        if (!isActive) {
          return;
        }

        const customerVehicles = Array.isArray(response.data)
          ? response.data
          : [];

        setVehicles(customerVehicles);
      } catch (requestError) {
        if (!isActive) {
          return;
        }

        console.error("Failed to load customer vehicles:", requestError);

        setVehicles([]);

        setError(
          requestError.response?.data?.message ||
            "Unable to load your registered vehicles. Please try again later."
        );
      } finally {
        if (isActive) {
          setLoading(false);
        }
      }
    };

    loadCustomerVehicles();

    return () => {
      isActive = false;
    };
  }, [customerId]);

  const handleVehicleKeyDown = (event, vehicle) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      onSelectVehicle(vehicle);
    }
  };

  return (
    <>
      <style>{vehicleSelectorStyles}</style>

      <section
        className="vehicle-selector"
        aria-labelledby="vehicle-selection-heading"
      >
        <div className="vehicle-selector-header">
          <ServiceStepIcon />

          <div>
            <h5
              id="vehicle-selection-heading"
              className="fw-bold mb-1"
            >
              Step 1: Select Your Vehicle
            </h5>

            <p className="text-muted small mb-0">
              Only vehicles registered to your customer account are
              displayed.
            </p>
          </div>
        </div>

        {loading && (
          <div
            className="vehicle-loading"
            role="status"
            aria-live="polite"
          >
            <div
              className="spinner-border spinner-border-sm text-primary"
              aria-hidden="true"
            />

            <span>Loading registered vehicles...</span>
          </div>
        )}

        {!loading && error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}

        {!loading && !error && vehicles.length === 0 && (
          <div className="alert alert-warning" role="alert">
            No vehicles are registered for your account. Please register
            a vehicle before creating a service booking.
          </div>
        )}

        {!loading && !error && vehicles.length > 0 && (
          <div className="row g-3">
            {vehicles.map((vehicle) => {
              const isSelected =
                String(selectedVehicle?.id) === String(vehicle.id);

              return (
                <div
                  key={vehicle.id}
                  className="col-md-6 col-lg-4"
                >
                  <div
                    className={`vehicle-selection-card ${
                      isSelected
                        ? "vehicle-selection-card-selected"
                        : ""
                    }`}
                    role="button"
                    tabIndex={0}
                    aria-pressed={isSelected}
                    aria-label={`Select ${vehicle.make} ${vehicle.model}, registration ${vehicle.licensePlateNumber}`}
                    onClick={() => onSelectVehicle(vehicle)}
                    onKeyDown={(event) =>
                      handleVehicleKeyDown(event, vehicle)
                    }
                  >
                    <div className="vehicle-card-top">
                      <span className="vehicle-number-badge">
                        {vehicle.licensePlateNumber ||
                          "Registration unavailable"}
                      </span>

                      {isSelected && (
                        <span className="vehicle-selected-badge">
                          Selected
                        </span>
                      )}
                    </div>

                    <div className="vehicle-card-icon">
                      <svg
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                      >
                        <path d="M5 17H3v-4l2-1 2-5h10l2 5 2 1v4h-2" />
                        <path d="M7 17h10" />
                        <circle cx="7" cy="17" r="2" />
                        <circle cx="17" cy="17" r="2" />
                        <path d="M7 12h10" />
                      </svg>
                    </div>

                    <h6 className="vehicle-name">
                      {vehicle.make || "Unknown make"}{" "}
                      {vehicle.model || "Unknown model"}
                    </h6>

                    <div className="vehicle-details">
                      <span>Year: {vehicle.year || "N/A"}</span>

                      {vehicle.color && (
                        <span>Color: {vehicle.color}</span>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>
    </>
  );
}

function ServiceStepIcon() {
  return (
    <div className="vehicle-step-icon" aria-hidden="true">
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M5 17H3v-4l2-1 2-5h10l2 5 2 1v4h-2" />
        <path d="M7 17h10" />
        <circle cx="7" cy="17" r="2" />
        <circle cx="17" cy="17" r="2" />
        <path d="M7 12h10" />
      </svg>
    </div>
  );
}

const vehicleSelectorStyles = `
  .vehicle-selector {
    width: 100%;
  }

  .vehicle-selector-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }

  .vehicle-step-icon {
    display: flex;
    flex: 0 0 58px;
    align-items: center;
    justify-content: center;
    width: 58px;
    height: 58px;
    color: #4fa3ff;
    background:
      linear-gradient(
        145deg,
        rgba(79, 163, 255, 0.24),
        rgba(79, 163, 255, 0.06)
      );
    border: 1px solid rgba(79, 163, 255, 0.45);
    border-radius: 16px;
    box-shadow:
      0 10px 25px rgba(79, 163, 255, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.05);
  }

  .vehicle-step-icon svg {
    width: 30px;
    height: 30px;
  }

  .vehicle-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
    padding: 3rem 1rem;
    color: #a7b3c5;
  }

  .vehicle-selection-card {
    height: 100%;
    padding: 1.5rem;
    color: #f4f7fb;
    background:
      linear-gradient(
        145deg,
        rgba(23, 37, 54, 0.98),
        rgba(17, 28, 41, 0.98)
      );
    border: 1px solid #26364a;
    border-radius: 17px;
    outline: none;
    cursor: pointer;
    box-shadow: 0 10px 26px rgba(0, 0, 0, 0.2);
    transition:
      transform 0.22s ease,
      border-color 0.22s ease,
      box-shadow 0.22s ease,
      background 0.22s ease;
  }

  .vehicle-selection-card:hover,
  .vehicle-selection-card:focus-visible {
    transform: translateY(-4px);
    border-color: rgba(82, 128, 255, 0.6);
    box-shadow: 0 17px 36px rgba(0, 0, 0, 0.32);
  }

  .vehicle-selection-card:focus-visible {
    box-shadow:
      0 0 0 4px rgba(82, 128, 255, 0.18),
      0 17px 36px rgba(0, 0, 0, 0.32);
  }

  .vehicle-selection-card-selected {
    background:
      linear-gradient(
        145deg,
        rgba(82, 128, 255, 0.2),
        rgba(17, 28, 41, 0.98)
      );
    border-color: #5280ff;
    box-shadow:
      0 0 0 2px rgba(82, 128, 255, 0.1),
      0 16px 36px rgba(0, 0, 0, 0.3);
  }

  .vehicle-card-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    margin-bottom: 1.25rem;
  }

  .vehicle-number-badge {
    display: inline-flex;
    padding: 0.45rem 0.75rem;
    color: #bcd0ff;
    font-family: monospace;
    font-size: 0.8rem;
    font-weight: 700;
    background: rgba(82, 128, 255, 0.12);
    border: 1px solid rgba(82, 128, 255, 0.3);
    border-radius: 8px;
  }

  .vehicle-selected-badge {
    display: inline-flex;
    padding: 0.45rem 0.75rem;
    color: #ffffff;
    font-size: 0.75rem;
    font-weight: 700;
    background: #5280ff;
    border-radius: 8px;
  }

  .vehicle-card-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    margin-bottom: 1rem;
    color: #78a0ff;
    background: rgba(82, 128, 255, 0.12);
    border: 1px solid rgba(82, 128, 255, 0.28);
    border-radius: 13px;
  }

  .vehicle-card-icon svg {
    width: 26px;
    height: 26px;
  }

  .vehicle-name {
    margin-bottom: 0.65rem;
    color: #f4f7fb;
    font-size: 1.05rem;
    font-weight: 800;
  }

  .vehicle-details {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem 1rem;
    color: #a7b3c5;
    font-size: 0.85rem;
  }

  @media (max-width: 576px) {
    .vehicle-selector-header {
      align-items: flex-start;
    }

    .vehicle-step-icon {
      flex-basis: 52px;
      width: 52px;
      height: 52px;
    }

    .vehicle-step-icon svg {
      width: 27px;
      height: 27px;
    }

    .vehicle-selection-card {
      padding: 1.25rem;
    }
  }
`;