const SERVICE_TYPES = [
  {
    id: "OIL_CHANGE",
    title: "Oil Change & Filter",
    desc: "Engine oil replacement, oil filter renewal, and fluid top-up.",
    duration: "45 mins",
    icon: "🛢️",
  },
  {
    id: "TIRE_ROTATION",
    title: "Tire Rotation & Balance",
    desc: "4-wheel rotation, wheel balancing, and tire pressure check.",
    duration: "30 mins",
    icon: "🛞",
  },
  {
    id: "BRAKE_INSPECTION",
    title: "Brake Inspection",
    desc: "Full inspection of brake pads, discs, lines, and fluid level.",
    duration: "45 mins",
    icon: "🛑",
  },
  {
    id: "GENERAL_SERVICE",
    title: "General Maintenance",
    desc: "Multi-point safety inspection, air filter, and battery test.",
    duration: "60 mins",
    icon: "🔧",
  },
  {
    id: "FULL_SERVICE",
    title: "Full Comprehensive Service",
    desc: "Complete bumper-to-bumper engine, transmission, and safety service.",
    duration: "120 mins",
    icon: "⭐",
  },
];

export default function ServiceTypeSelector({
  selectedServiceType,
  onSelectServiceType,
}) {
  return (
    <div>
      <h5 className="fw-semibold mb-3">Step 2: Choose Service Type</h5>
      <p className="text-muted small">
        Select the type of maintenance or service required for your vehicle.
      </p>

      <div className="row g-3">
        {SERVICE_TYPES.map((service) => {
          const isSelected = selectedServiceType === service.id;
          return (
            <div key={service.id} className="col-md-6">
              <div
                className={`card h-100 border-2 transition-all ${
                  isSelected
                    ? "border-primary bg-primary bg-opacity-10 shadow-sm"
                    : "border-light-subtle shadow-sm hover-shadow"
                }`}
                style={{ cursor: "pointer" }}
                onClick={() => onSelectServiceType(service.id)}
              >
                <div className="card-body d-flex align-items-start gap-3">
                  <div className="fs-2">{service.icon}</div>
                  <div className="flex-grow-1">
                    <div className="d-flex justify-content-between align-items-center mb-1">
                      <h6 className="card-title fw-bold mb-0">
                        {service.title}
                      </h6>
                      <span className="badge bg-light text-dark border">
                        {service.duration}
                      </span>
                    </div>
                    <p className="card-text text-muted small mb-0">
                      {service.desc}
                    </p>
                  </div>
                  {isSelected && (
                    <span className="badge bg-primary align-self-center">
                      Selected
                    </span>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
