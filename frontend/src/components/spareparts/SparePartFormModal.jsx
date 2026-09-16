import React, { useState, useEffect } from 'react';

const PRESET_CATEGORIES = [
  'Filters',
  'Brakes',
  'Lubricants',
  'Electrical',
  'Engine',
  'Suspension',
  'Wipers',
  'Transmission',
  'Tires',
  'Other',
];

export default function SparePartFormModal({
  show,
  isEdit = false,
  initialData = null,
  categories = [],
  onClose,
  onSave,
  loading = false,
}) {
  const [formData, setFormData] = useState({
    partName: '',
    category: 'Filters',
    status: 'AVAILABLE',
    quantity: '',
    unitPrice: '',
    reorderLevel: '',
  });

  const [clientErrors, setClientErrors] = useState({});

  useEffect(() => {
    if (initialData && isEdit) {
      setFormData({
        partName: initialData.partName || '',
        category: initialData.category || 'Filters',
        status: initialData.status || 'AVAILABLE',
        quantity: initialData.quantity !== undefined ? initialData.quantity : '',
        unitPrice: initialData.unitPrice || '',
        reorderLevel: initialData.reorderLevel !== undefined ? initialData.reorderLevel : '',
      });
    } else {
      setFormData({
        partName: '',
        category: 'Filters',
        status: 'AVAILABLE',
        quantity: '',
        unitPrice: '',
        reorderLevel: '',
      });
    }
    setClientErrors({});
  }, [initialData, isEdit, show]);

  if (!show) return null;

  const validate = () => {
    const errors = {};
    if (!formData.partName.trim()) errors.partName = 'Part name cannot be blank';
    if (!formData.category.trim()) errors.category = 'Category is required';
    if (!formData.status.trim()) errors.status = 'Status is required';

    const qty = parseInt(formData.quantity, 10);
    if (isNaN(qty) || qty < 0) errors.quantity = 'Quantity cannot be negative';

    const reorder = parseInt(formData.reorderLevel, 10);
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
      partName: formData.partName.trim(),
      category: formData.category.trim(),
      status: formData.status,
      quantity: parseInt(formData.quantity, 10),
      unitPrice: parseFloat(formData.unitPrice),
      reorderLevel: parseInt(formData.reorderLevel, 10),
    });
  };

  const allCategories = Array.from(new Set([...PRESET_CATEGORIES, ...categories]));

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
                <i className="bi bi-gear-wide-connected text-primary fs-4"></i>
                {isEdit ? 'Edit Spare Part Record' : 'Add New Spare Part'}
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
                  Part Name <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  className={`form-control ${clientErrors.partName ? 'is-invalid' : ''}`}
                  placeholder="e.g. Toyota Genuine Oil Filter"
                  value={formData.partName}
                  onChange={(e) => setFormData({ ...formData, partName: e.target.value })}
                  disabled={loading}
                />
                {clientErrors.partName && <div className="invalid-feedback">{clientErrors.partName}</div>}
              </div>

              <div className="row g-2 mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Category <span className="text-danger">*</span>
                  </label>
                  <select
                    className={`form-select ${clientErrors.category ? 'is-invalid' : ''}`}
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    disabled={loading}
                    style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#f8fafc' }}
                  >
                    {allCategories.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                  {clientErrors.category && <div className="invalid-feedback">{clientErrors.category}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Status <span className="text-danger">*</span>
                  </label>
                  <select
                    className={`form-select ${clientErrors.status ? 'is-invalid' : ''}`}
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    disabled={loading}
                    style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#f8fafc' }}
                  >
                    <option value="AVAILABLE">AVAILABLE</option>
                    <option value="LOW_STOCK">LOW_STOCK</option>
                    <option value="OUT_OF_STOCK">OUT_OF_STOCK</option>
                    <option value="DISCONTINUED">DISCONTINUED</option>
                  </select>
                  {clientErrors.status && <div className="invalid-feedback">{clientErrors.status}</div>}
                </div>
              </div>

              <div className="row g-2 mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Quantity in Stock <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="1"
                    min="0"
                    className={`form-control ${clientErrors.quantity ? 'is-invalid' : ''}`}
                    placeholder="e.g. 25"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.quantity && <div className="invalid-feedback">{clientErrors.quantity}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                    Reorder Threshold <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    step="1"
                    min="0"
                    className={`form-control ${clientErrors.reorderLevel ? 'is-invalid' : ''}`}
                    placeholder="e.g. 10"
                    value={formData.reorderLevel}
                    onChange={(e) => setFormData({ ...formData, reorderLevel: e.target.value })}
                    disabled={loading}
                  />
                  {clientErrors.reorderLevel && <div className="invalid-feedback">{clientErrors.reorderLevel}</div>}
                </div>
              </div>

              <div className="mb-2">
                <label className="form-label small fw-semibold" style={{ color: '#cbd5e1' }}>
                  Unit Price (LKR) <span className="text-danger">*</span>
                </label>
                <div className="input-group">
                  <span className="input-group-text" style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#94a3b8' }}>
                    Rs.
                  </span>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    className={`form-control ${clientErrors.unitPrice ? 'is-invalid' : ''}`}
                    placeholder="e.g. 2450.00"
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
                {loading ? 'Saving...' : isEdit ? 'Update Part' : 'Save Part'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}