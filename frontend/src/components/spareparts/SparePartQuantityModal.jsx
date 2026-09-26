import React, { useState } from 'react';

export default function SparePartQuantityModal({ show, part, onClose, onSave, loading = false }) {
  const [action, setAction] = useState('ADD');
  const [amount, setAmount] = useState('');
  const [error, setError] = useState('');

  if (!show || !part) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const qty = parseInt(amount, 10);
    if (isNaN(qty) || qty <= 0) {
      setError('Please enter a positive whole number');
      return;
    }

    if (action === 'DEDUCT' && qty > part.quantity) {
      setError(`Cannot deduct ${qty} units. Only ${part.quantity} units available`);
      return;
    }

    setError('');
    onSave({ action, quantity: qty });
  };

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(4, 9, 18, 0.78)', backdropFilter: 'blur(4px)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div
          className="modal-content border-0 shadow-lg rounded-4"
          style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', color: '#f8fafc' }}
        >
          <form onSubmit={handleSubmit}>
            <div className="modal-header border-0 pb-0">
              <h5 className="modal-title fw-bold text-white d-flex align-items-center gap-2">
                <i className="bi bi-plus-slash-minus text-info fs-4"></i>
                Update Spare Part Quantity
              </h5>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
                disabled={loading}
                style={{ filter: 'invert(1)' }}
              ></button>
            </div>

            <div className="modal-body py-3">
              <div
                className="p-3 rounded-3 mb-3"
                style={{ backgroundColor: '#151f32', border: '1px solid #1e293b' }}
              >
                <div className="fw-bold text-white">{part.partName}</div>
                <div className="small" style={{ color: '#94a3b8' }}>{part.category} &bull; ID #{part.partId}</div>
                <div className="mt-2 pt-2 d-flex justify-content-between align-items-center" style={{ borderTop: '1px solid #1e293b' }}>
                  <span className="small" style={{ color: '#94a3b8' }}>Current Stock:</span>
                  <span className="fw-bold font-monospace" style={{ color: '#38bdf8' }}>{part.quantity} units</span>
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>Action Mode</label>
                <div className="btn-group w-100" role="group">
                  <input
                    type="radio"
                    className="btn-check"
                    name="spareActionRadio"
                    id="spareAdd"
                    checked={action === 'ADD'}
                    onChange={() => setAction('ADD')}
                  />
                  <label className="btn btn-outline-success" htmlFor="spareAdd">
                    <i className="bi bi-plus-lg me-1"></i> Add / Receive
                  </label>

                  <input
                    type="radio"
                    className="btn-check"
                    name="spareActionRadio"
                    id="spareDeduct"
                    checked={action === 'DEDUCT'}
                    onChange={() => setAction('DEDUCT')}
                  />
                  <label className="btn btn-outline-danger" htmlFor="spareDeduct">
                    <i className="bi bi-dash-lg me-1"></i> Deduct / Issue
                  </label>

                  <input
                    type="radio"
                    className="btn-check"
                    name="spareActionRadio"
                    id="spareSet"
                    checked={action === 'SET'}
                    onChange={() => setAction('SET')}
                  />
                  <label className="btn btn-outline-primary" htmlFor="spareSet">
                    <i className="bi bi-arrow-repeat me-1"></i> Set Count
                  </label>
                </div>
              </div>

              <div className="mb-2">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  Units Amount <span className="text-danger">*</span>
                </label>
                <input
                  type="number"
                  step="1"
                  min="1"
                  className={`form-control ${error ? 'is-invalid' : ''}`}
                  placeholder="Enter number of units..."
                  value={amount}
                  onChange={(e) => {
                    setAmount(e.target.value);
                    setError('');
                  }}
                  disabled={loading}
                />
                {error && <div className="invalid-feedback">{error}</div>}
              </div>
            </div>

            <div className="modal-footer border-0 pt-0">
              <button type="button" className="btn btn-outline-secondary px-3" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button
                type="submit"
                className="btn px-4 fw-semibold text-white"
                style={{ backgroundColor: '#0284c7' }}
                disabled={loading}
              >
                {loading ? 'Updating...' : 'Apply Adjustment'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}