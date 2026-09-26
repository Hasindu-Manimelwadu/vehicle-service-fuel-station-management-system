import React from 'react';

export default function ToastAlert({ alert, onClose }) {
  if (!alert || !alert.message) return null;

  const isSuccess = alert.type === 'success';

  return (
    <div
      className="alert alert-dismissible fade show shadow-sm mb-4 rounded-3 d-flex align-items-start gap-3"
      role="alert"
      style={{
        backgroundColor: isSuccess ? 'rgba(16, 185, 129, 0.12)' : 'rgba(239, 68, 68, 0.14)',
        border: `1px solid ${isSuccess ? 'rgba(16, 185, 129, 0.35)' : 'rgba(239, 68, 68, 0.38)'}`,
        color: '#f8fafc',
      }}
    >
      <i
        className={`bi ${isSuccess ? 'bi-check-circle-fill text-success' : 'bi-exclamation-octagon-fill text-danger'} fs-5 flex-shrink-0 mt-0.5`}
      ></i>
      <div className="flex-grow-1">
        <div className="fw-semibold">{alert.message}</div>
        {alert.validationErrors && (
          <ul className="mb-0 mt-1 ps-3 small text-secondary">
            {Object.entries(alert.validationErrors).map(([field, err]) => (
              <li key={field}>
                <strong className="text-white">{field}:</strong> {err}
              </li>
            ))}
          </ul>
        )}
      </div>
      <button
        type="button"
        className="btn-close"
        onClick={onClose}
        aria-label="Close"
        style={{ filter: 'invert(1)' }}
      ></button>
    </div>
  );
}