import React from 'react';
import StockBadge from '../common/StockBadge';

export default function SparePartDetailsModal({ show, part, onClose }) {
  if (!show || !part) return null;

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(4, 9, 18, 0.78)', backdropFilter: 'blur(4px)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div
          className="modal-content border-0 shadow-lg rounded-4"
          style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', color: '#f8fafc' }}
        >
          <div className="modal-header border-0 pb-0">
            <h5 className="modal-title fw-bold text-white d-flex align-items-center gap-2">
              <i className="bi bi-gear-wide-connected text-primary fs-4"></i>
              Spare Part Specifications
            </h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
              style={{ filter: 'invert(1)' }}
            ></button>
          </div>

          <div className="modal-body py-3">
            <div
              className="text-center py-3 mb-3 rounded-3"
              style={{ backgroundColor: '#151f32', border: '1px solid #1e293b' }}
            >
              <h5 className="fw-bold mb-1 text-white">{part.partName}</h5>
              <span
                className="badge font-monospace px-3 py-1"
                style={{ backgroundColor: '#1e293b', color: '#94a3b8' }}
              >
                {part.category}
              </span>
              <div className="mt-2">
                <StockBadge status={part.stockStatus || part.status} lowStock={part.lowStock} />
              </div>
            </div>

            <ul className="list-group list-group-flush rounded-3" style={{ border: '1px solid #1e293b' }}>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Part ID:</span>
                <span className="font-monospace fw-semibold text-white">#{part.partId}</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Registered Category:</span>
                <span className="fw-semibold text-white">{part.category}</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Current Stock Quantity:</span>
                <span className="font-monospace fw-bold" style={{ color: '#38bdf8' }}>{part.quantity} units</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Reorder Threshold Level:</span>
                <span className="font-monospace fw-bold text-danger">{part.reorderLevel} units</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Unit Retail Price:</span>
                <span className="font-monospace fw-bold text-success">Rs. {Number(part.unitPrice).toFixed(2)}</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Database Status:</span>
                <span
                  className="badge font-monospace"
                  style={{ backgroundColor: '#1e293b', color: '#cbd5e1' }}
                >
                  {part.status}
                </span>
              </li>
            </ul>
          </div>

          <div className="modal-footer border-0 pt-0">
            <button type="button" className="btn btn-outline-secondary px-4" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}