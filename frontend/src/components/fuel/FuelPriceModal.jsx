import React, { useState, useEffect } from 'react';

export default function FuelPriceModal({ show, stock, onClose, onSave, loading = false }) {
  const [price, setPrice] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (stock) {
      setPrice(stock.unitPrice || '');
      setError('');
    }
  }, [stock, show]);

  if (!show || !stock) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const p = parseFloat(price);
    if (isNaN(p) || p < 0) {
      setError('Unit price must be 0 or greater');
      return;
    }
    setError('');
    onSave({ unitPrice: p });
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
                <i className="bi bi-tag-fill text-success fs-4"></i>
                Update Fuel Unit Price
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
                <div className="d-flex justify-content-between align-items-center">
                  <span className="fw-bold text-white">{stock.fuelType}</span>
                  <span
                    className="badge font-monospace px-2.5 py-1"
                    style={{ backgroundColor: '#1e293b', color: '#94a3b8' }}
                  >
                    {stock.tankNo}
                  </span>
                </div>
                <div className="small mt-2" style={{ color: '#94a3b8' }}>Current Retail Price:</div>
                <div className="h5 font-monospace fw-bold mb-0 mt-0.5" style={{ color: '#38bdf8' }}>
                  Rs. {Number(stock.unitPrice).toFixed(2)} / Liter
                </div>
              </div>

              <div className="mb-2">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  New Unit Price (LKR per Liter) <span className="text-danger">*</span>
                </label>
                <div className="input-group">
                  <span className="input-group-text" style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#94a3b8' }}>
                    Rs.
                  </span>
                  <input
                    type="number"
                    step="0.01"
                    className={`form-control ${error ? 'is-invalid' : ''}`}
                    placeholder="Enter new price..."
                    value={price}
                    onChange={(e) => {
                      setPrice(e.target.value);
                      setError('');
                    }}
                    disabled={loading}
                  />
                  {error && <div className="invalid-feedback">{error}</div>}
                </div>
              </div>
            </div>

            <div className="modal-footer border-0 pt-0">
              <button type="button" className="btn btn-outline-secondary px-3" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-success px-4 fw-semibold"
                disabled={loading}
              >
                {loading ? 'Saving...' : 'Apply Price'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}