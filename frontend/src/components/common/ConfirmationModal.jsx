import React from 'react';

export default function ConfirmationModal({ show, title, message, itemName, onConfirm, onCancel, confirmText = 'Delete', loading = false }) {
  if (!show) return null;

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(4, 9, 18, 0.78)', backdropFilter: 'blur(4px)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div
          className="modal-content shadow-lg rounded-4"
          style={{
            backgroundColor: '#0f172a',
            border: '1px solid #1e293b',
            color: '#f8fafc',
          }}
        >
          <div className="modal-header border-0 pb-0">
            <h5 className="modal-title fw-bold text-danger d-flex align-items-center gap-2">
              <i className="bi bi-exclamation-triangle-fill fs-4"></i>
              {title}
            </h5>
            <button
              type="button"
              className="btn-close"
              onClick={onCancel}
              disabled={loading}
              style={{ filter: 'invert(1)' }}
            ></button>
          </div>
          <div className="modal-body py-3">
            <p className="mb-2 text-white">{message}</p>
            {itemName && (
              <div
                className="p-3 rounded-3 text-white fw-bold my-2"
                style={{ backgroundColor: '#151f32', border: '1px solid #1e293b' }}
              >
                {itemName}
              </div>
            )}
            <small style={{ color: '#94a3b8' }}>
              This action cannot be undone and will update the database permanently.
            </small>
          </div>
          <div className="modal-footer border-0 pt-0">
            <button type="button" className="btn btn-outline-secondary px-3" onClick={onCancel} disabled={loading}>
              Cancel
            </button>
            <button type="button" className="btn btn-danger px-4 fw-semibold" onClick={onConfirm} disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                  Processing...
                </>
              ) : (
                <>
                  <i className="bi bi-trash3 me-1"></i> {confirmText}
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}