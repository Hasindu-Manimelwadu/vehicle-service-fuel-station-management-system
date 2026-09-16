import React, { useState } from 'react';

export default function FuelQuantityModal({ show, stock, onClose, onSave, loading = false }) {
  const [action, setAction] = useState('ADD');
  const [amount, setAmount] = useState('');
  const [error, setError] = useState('');

  if (!show || !stock) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const qty = parseFloat(amount);
    if (isNaN(qty) || qty <= 0) {
      setError('Please enter a valid positive quantity');
      return;
    }

    if (action === 'ADD' && stock.currentQuantity + qty > stock.tankCapacity) {
      const available = stock.tankCapacity - stock.currentQuantity;
      setError(`Adding ${qty} L exceeds tank capacity. Maximum room is ${available.toFixed(1)} L`);
      return;
    }

    if (action === 'DEDUCT' && qty > stock.currentQuantity) {
      setError(`Cannot deduct ${qty} L. Only ${stock.currentQuantity} L currently in tank`);
      return;
    }

    if (action === 'SET' && qty > stock.tankCapacity) {
      setError(`Target quantity cannot exceed capacity of ${stock.tankCapacity} L`);
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
                <i className="bi bi-droplet-half text-info fs-4"></i>
                Update Fuel Stock Level
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
                <div className="row text-center mt-3 pt-2" style={{ borderTop: '1px solid #1e293b' }}>
                  <div className="col-6">
                    <div className="small" style={{ color: '#94a3b8' }}>Current Quantity</div>
                    <div className="fw-bold font-monospace mt-0.5" style={{ color: '#38bdf8' }}>
                      {stock.currentQuantity?.toLocaleString()} L
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="small" style={{ color: '#94a3b8' }}>Tank Capacity</div>
                    <div className="fw-bold font-monospace text-white mt-0.5">
                      {stock.tankCapacity?.toLocaleString()} L
                    </div>
                  </div>
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>Operation Mode</label>
                <div className="btn-group w-100" role="group">
                  <input
                    type="radio"
                    className="btn-check"
                    name="actionRadio"
                    id="radioAdd"
                    checked={action === 'ADD'}
                    onChange={() => setAction('ADD')}
                  />
                  <label className="btn btn-outline-success" htmlFor="radioAdd">
                    <i className="bi bi-plus-lg me-1"></i> Add / Restock
                  </label>

                  <input
                    type="radio"
                    className="btn-check"
                    name="actionRadio"
                    id="radioDeduct"
                    checked={action === 'DEDUCT'}
                    onChange={() => setAction('DEDUCT')}
                  />
                  <label className="btn btn-outline-danger" htmlFor="radioDeduct">
                    <i className="bi bi-dash-lg me-1"></i> Deduct / Dispense
                  </label>

                  <input
                    type="radio"
                    className="btn-check"
                    name="actionRadio"
                    id="radioSet"
                    checked={action === 'SET'}
                    onChange={() => setAction('SET')}
                  />
                  <label className="btn btn-outline-primary" htmlFor="radioSet">
                    <i className="bi bi-arrow-repeat me-1"></i> Set Exact
                  </label>
                </div>
              </div>

              <div className="mb-2">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  Quantity (Liters) <span className="text-danger">*</span>
                </label>
                <input
                  type="number"
                  step="0.1"
                  className={`form-control ${error ? 'is-invalid' : ''}`}
                  placeholder="Enter volume in liters..."
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
                {loading ? 'Updating...' : 'Update Volume'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}