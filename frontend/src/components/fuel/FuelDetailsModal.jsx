import React from 'react';
import StockBadge from '../common/StockBadge';

export default function FuelDetailsModal({ show, stock, onClose }) {
  if (!show || !stock) return null;

  const pct = stock.percentageFilled || 0;
  const progressColor = pct < 20 ? '#ef4444' : pct < 40 ? '#f59e0b' : '#10b981';

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(4, 9, 18, 0.78)', backdropFilter: 'blur(4px)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div
          className="modal-content border-0 shadow-lg rounded-4"
          style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', color: '#f8fafc' }}
        >
          <div className="modal-header border-0 pb-0">
            <h5 className="modal-title fw-bold text-white d-flex align-items-center gap-2">
              <i className="bi bi-info-circle-fill text-primary fs-4"></i>
              Fuel Tank Specifications
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
              <h4 className="fw-bold mb-1 text-white">{stock.fuelType}</h4>
              <span
                className="badge font-monospace px-3 py-1 fs-6"
                style={{ backgroundColor: '#1e293b', color: '#94a3b8' }}
              >
                {stock.tankNo}
              </span>
              <div className="mt-2">
                <StockBadge status={stock.status} lowStock={stock.lowStock} />
              </div>
            </div>

            <div className="mb-3">
              <div className="d-flex justify-content-between small mb-1" style={{ color: '#94a3b8' }}>
                <span>Tank Fill Level</span>
                <span className="fw-bold font-monospace text-white">{pct}%</span>
              </div>
              <div className="progress" style={{ height: '12px', backgroundColor: '#151f32', borderRadius: '6px' }}>
                <div
                  className="progress-bar rounded-pill"
                  role="progressbar"
                  style={{
                    width: `${Math.min(pct, 100)}%`,
                    backgroundColor: progressColor,
                  }}
                ></div>
              </div>
            </div>

            <ul className="list-group list-group-flush rounded-3" style={{ border: '1px solid #1e293b' }}>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Database ID:</span>
                <span className="font-monospace fw-semibold text-white">#{stock.fuelStockId}</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Total Tank Capacity:</span>
                <span className="font-monospace fw-bold text-white">{stock.tankCapacity?.toLocaleString()} Liters</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Current Remaining Fuel:</span>
                <span className="font-monospace fw-bold" style={{ color: '#38bdf8' }}>{stock.currentQuantity?.toLocaleString()} Liters</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Reorder Threshold Level:</span>
                <span className="font-monospace fw-bold text-danger">{stock.reorderLevel?.toLocaleString()} Liters</span>
              </li>
              <li
                className="list-group-item d-flex justify-content-between align-items-center"
                style={{ backgroundColor: '#131e35', borderColor: '#1e293b', color: '#94a3b8' }}
              >
                <span>Unit Retail Price:</span>
                <span className="font-monospace fw-bold text-success">Rs. {Number(stock.unitPrice).toFixed(2)} / L</span>
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