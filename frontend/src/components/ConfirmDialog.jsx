import Icon from "./Icon";

export default function ConfirmDialog({ open, title, message, confirmText = "Confirm", tone = "danger", onConfirm, onCancel }) {
  if (!open) return null;

  return (
    <div className="modal-backdrop-custom" role="presentation" onMouseDown={onCancel}>
      <div className="confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="confirm-title" onMouseDown={(e) => e.stopPropagation()}>
        <div className={`confirm-icon confirm-${tone}`}><Icon name={tone === "danger" ? "alert" : "check"} size={24} /></div>
        <h3 id="confirm-title">{title}</h3>
        <p>{message}</p>
        <div className="dialog-actions">
          <button type="button" className="btn-soft" onClick={onCancel}>Cancel</button>
          <button type="button" className={`btn-app ${tone === "danger" ? "btn-danger-app" : ""}`} onClick={onConfirm}>{confirmText}</button>
        </div>
      </div>
    </div>
  );
}
