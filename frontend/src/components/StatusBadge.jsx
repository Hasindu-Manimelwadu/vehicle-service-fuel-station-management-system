import Icon from "./Icon";

const labels = {
  PENDING: "Pending",
  IN_PROGRESS: "In progress",
  COMPLETED: "Completed",
  ASSIGNED: "Assigned",
};

export default function StatusBadge({ status }) {
  const normalized = status || "PENDING";
  const icon = normalized === "COMPLETED" ? "check" : normalized === "IN_PROGRESS" ? "wrench" : "clock";
  return (
    <span className={`status-badge status-${normalized.toLowerCase()}`}>
      <Icon name={icon} size={14} />
      {labels[normalized] || normalized.replaceAll("_", " ")}
    </span>
  );
}
