import React, { useState, useEffect } from 'react';

export default function FuelStockFormModal({ show, isEdit = false, initialData = null, onClose, onSave, loading = false }) {
  const [formData, setFormData] = useState({
    fuelType: '',
    tankNo: '',
    tankCapacity: '',
    currentQuantity: '',
    reorderLevel: '',
    unitPrice: '',
  });

  const [clientErrors, setClientErrors] = useState({});

  useEffect(() => {
    if (initialData && isEdit) {
      setFormData({
        fuelType: initialData.fuelType || '',
        tankNo: initialData.tankNo || '',
        tankCapacity: initialData.tankCapacity || '',
        currentQuantity: initialData.currentQuantity || '',
        reorderLevel: initialData.reorderLevel || '',
        unitPrice: initialData.unitPrice || '',
      });
    } else {
      setFormData({
        fuelType: '',
        tankNo: '',
        tankCapacity: '',
        currentQuantity: '',
        reorderLevel: '',
        unitPrice: '',
      });
    }
    setClientErrors({});
  }, [initialData, isEdit, show]);

  if (!show) return null;

  const validate = () => {
    const errors = {};
    if (!formData.fuelType.trim()) errors.fuelType = 'Fuel type cannot be blank';
    if (!formData.tankNo.trim()) errors.tankNo = 'Tank number cannot be blank';

    const cap = parseFloat(formData.tankCapacity);
    if (isNaN(cap) || cap <= 0) errors.tankCapacity = 'Tank capacity must be greater than 0';

    const qty = parseFloat(formData.currentQuantity);
    if (isNaN(qty) || qty < 0) {
      errors.currentQuantity = 'Current quantity cannot be negative';
    } else if (!isNaN(cap) && qty > cap) {
      errors.currentQuantity = `Current quantity (${qty} L) cannot exceed tank capacity (${cap} L)`;
    }

    const reorder = parseFloat(formData.reorderLevel);
    if (isNaN(reorder) || reorder < 0) errors.reorderLevel = 'Reorder level cannot be negative';

    const price = parseFloat(formData.unitPrice);
    if (isNaN(price) || price < 0) errors.unitPrice = 'Unit price cannot be negative';

    setClientErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    onSave({
      fuelType: formData.fuelType.trim(),
      tankNo: formData.tankNo.trim(),
      tankCapacity: parseFloat(formData.tankCapacity),
      currentQuantity: parseFloat(formData.currentQuantity),
      reorderLevel: parseFloat(formData.reorderLevel),
      unitPrice: parseFloat(formData.unitPrice),
    });
  };

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(4, 9, 18, 0.78)', backdropFilter: 'blur(4px)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div
          className="modal-content border-0 shadow-lg rounded-4"
          style={{
            backgroundColor: '#0f172a',
            border: '1px solid #1e293b',
            color: '#f8fafc',
          }}
        >
          <form onSubmit={handleSubmit}>
            <div className="modal-header border-0 pb-0">
              <h5 className="modal-title fw-bold text-white d-flex align-items-center gap-2">
                <i className="bi bi-fuel-pump text-primary fs-4"></i>
                {isEdit ? 'Edit Fuel Tank Record' : 'Add New Fuel Stock Tank'}
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
              <div className="mb-3">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  Fuel Type <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  className={`form-control ${clientErrors.fuelType ? 'is-invalid' : ''}`}
                  placeholder="e.g. Petrol Octane 92, Auto Diesel"
                  value={formData.fuelType}
                  onChange={(e) => setFormData({ ...formData, fuelType: e.target.value })}
                  disabled={loading}
                />
                {clientErrors.fuelType && <div className="invalid-feedback">{clientErrors.fuelType}</div>}
              </div>

              <div className="row g-2 mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Tank Number <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className={`form-control ${clientErrors.tankNo ? 'is-invalid' : ''}`}
                    placeholder="e.g. Tank-01"
                    value={formData.tankNo}
                    onChange={(e) => setFormData({ ...formData, tankNo: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.tankNo && <div className="invalid-feedback">{clientErrors.tankNo}</div>}
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Tank Capacity (Liters) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    className={`form-control ${clientErrors.tankCapacity ? 'is-invalid' : ''}`}
                    placeholder="e.g. 30000"
                    value={formData.tankCapacity}
                    onChange={(e) => setFormData({ ...formData, tankCapacity: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.tankCapacity && <div className="invalid-feedback">{clientErrors.tankCapacity}</div>}
                </div>
              </div>

              <div className="row g-2 mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Current Quantity (Liters) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    className={`form-control ${clientErrors.currentQuantity ? 'is-invalid' : ''}`}
                    placeholder="e.g. 15000"
                    value={formData.currentQuantity}
                    onChange={(e) => setFormData({ ...formData, currentQuantity: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.currentQuantity && <div className="invalid-feedback">{clientErrors.currentQuantity}</div>}
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Reorder Level (Liters) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    className={`form-control ${clientErrors.reorderLevel ? 'is-invalid' : ''}`}
                    placeholder="e.g. 5000"
                    value={formData.reorderLevel}
                    onChange={(e) => setFormData({ ...formData, reorderLevel: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.reorderLevel && <div className="invalid-feedback">{clientErrors.reorderLevel}</div>}
                </div>
              </div>

              <div className="mb-2">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  Unit Price per Liter (LKR) <span className="text-danger">*</span>
                </label>
                <div className="input-group">
                  <span className="input-group-text" style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#94a3b8' }}>
                    Rs.
                  </span>
                  <input
                    type="number"
                    step="0.01"
                    className={`form-control ${clientErrors.unitPrice ? 'is-invalid' : ''}`}
                    placeholder="e.g. 368.00"
                    value={formData.unitPrice}
                    onChange={(e) => setFormData({ ...formData, unitPrice: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.unitPrice && <div className="invalid-feedback">{clientErrors.unitPrice}</div>}
                </div>
              </div>
            </div>

            <div className="modal-footer border-0 pt-0">
              <button type="button" className="btn btn-outline-secondary px-3" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary px-4 fw-semibold" disabled={loading}>
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Saving...
                  </>
                ) : (
                  <>
                    <i className="bi bi-check2 me-1"></i> {isEdit ? 'Update Fuel Stock' : 'Save Fuel Stock'}
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}