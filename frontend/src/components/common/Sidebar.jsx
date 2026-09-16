import React from 'react';

export default function Sidebar({ activeTab, setActiveTab, lowStockFuelCount = 0, lowStockPartsCount = 0 }) {
  const totalLowStock = lowStockFuelCount + lowStockPartsCount;

  return (
    <div className="autocare-sidebar p-3 d-flex flex-column justify-content-between">
      <div>
        <div className="text-uppercase small fw-bold px-2 mb-3" style={{ color: '#64748b', letterSpacing: '0.08em', fontSize: '0.72rem' }}>
          Inventory Modules
        </div>
        <ul className="nav nav-pills flex-column mb-auto">
          <li className="nav-item">
            <button
              type="button"
              className={`nav-link w-100 text-start border-0 ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => setActiveTab('dashboard')}
            >
              <i className="bi bi-speedometer2"></i>
              <span>Dashboard</span>
            </button>
          </li>
          <li className="nav-item">
            <button
              type="button"
              className={`nav-link w-100 text-start border-0 ${activeTab === 'fuel' ? 'active' : ''}`}
              onClick={() => setActiveTab('fuel')}
            >
              <i className="bi bi-fuel-pump"></i>
              <span className="flex-grow-1">Fuel Stocks</span>
              {lowStockFuelCount > 0 && (
                <span className="badge badge-low-stock rounded-pill ms-auto px-2 py-1">
                  {lowStockFuelCount}
                </span>
              )}
            </button>
          </li>
          <li className="nav-item">
            <button
              type="button"
              className={`nav-link w-100 text-start border-0 ${activeTab === 'spareparts' ? 'active' : ''}`}
              onClick={() => setActiveTab('spareparts')}
            >
              <i className="bi bi-gear-wide-connected"></i>
              <span className="flex-grow-1">Spare Parts</span>
              {lowStockPartsCount > 0 && (
                <span className="badge badge-low-stock rounded-pill ms-auto px-2 py-1">
                  {lowStockPartsCount}
                </span>
              )}
            </button>
          </li>
        </ul>
      </div>

      {/* Inventory Health Status Card */}
      <div
        className="card border-0 p-3 mt-4 rounded-3 text-center"
        style={{
          backgroundColor: '#0f172a',
          border: '1px solid #1e293b',
        }}
      >
        <div className="small fw-semibold mb-1" style={{ color: '#94a3b8', fontSize: '0.78rem' }}>
          Inventory Health
        </div>
        {totalLowStock > 0 ? (
          <div className="d-flex align-items-center justify-content-center gap-1 fw-bold small text-danger">
            <i className="bi bi-exclamation-triangle-fill"></i>
            <span>{totalLowStock} Low Stock Alert{totalLowStock > 1 ? 's' : ''}</span>
          </div>
        ) : (
          <div className="d-flex align-items-center justify-content-center gap-1 fw-bold small text-success">
            <i className="bi bi-check-circle-fill"></i>
            <span>Stock Levels Healthy</span>
          </div>
        )}
      </div>
    </div>
  );
}