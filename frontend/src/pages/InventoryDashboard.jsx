import React, { useState, useEffect } from 'react';
import StatCard from '../components/common/StatCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ToastAlert from '../components/common/ToastAlert';
import dashboardApi from '../api/dashboardApi';
import fuelStockApi from '../api/fuelStockApi';
import sparePartApi from '../api/sparePartApi';
import StockBadge from '../components/common/StockBadge';

export default function InventoryDashboard({ onNavigateToFuel, onNavigateToSpareParts }) {
  const [summary, setSummary] = useState(null);
  const [lowStockFuels, setLowStockFuels] = useState([]);
  const [lowStockParts, setLowStockParts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState(null);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [sumRes, fuelRes, partRes] = await Promise.all([
        dashboardApi.getSummary(),
        fuelStockApi.getLowStock(),
        sparePartApi.getLowStock(),
      ]);

      setSummary(sumRes.data);
      setLowStockFuels(fuelRes.data || []);
      setLowStockParts(partRes.data || []);
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Failed to load dashboard metrics from backend.',
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Connecting to AutoCare database and compiling inventory statistics..." />;
  }

  const totalFuelLiters = summary?.totalFuelCurrentLiters || 0;
  const totalFuelCap = summary?.totalFuelCapacityLiters || 0;
  const overallFuelPct = totalFuelCap > 0 ? ((totalFuelLiters / totalFuelCap) * 100).toFixed(1) : 0;
  const totalLowStockAlerts = (summary?.lowStockFuelCount || 0) + (summary?.lowStockSparePartsCount || 0);

  return (
    <div className="p-4" style={{ backgroundColor: '#081220', minHeight: '100%' }}>
      {/* Top Header */}
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
          <div className="d-flex align-items-center gap-2 mb-1">
            <span className="autocare-pill py-0.5 px-2.5" style={{ fontSize: '0.75rem' }}>
              <i className="bi bi-broadcast text-primary"></i> Live Telemetry
            </span>
          </div>
          <h2 className="fw-bold mb-1 text-white">Inventory Overview</h2>
          <p className="small mb-0" style={{ color: '#94a3b8' }}>
            Real-time telemetry and threshold tracking for Fuel Tanks & Spare Parts Catalog
          </p>
        </div>
        <button
          className="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2 rounded-pill px-3 py-1.5"
          onClick={fetchDashboardData}
          style={{ borderColor: '#1e293b' }}
        >
          <i className="bi bi-arrow-clockwise"></i> Refresh Metrics
        </button>
      </div>

      <ToastAlert alert={alert} onClose={() => setAlert(null)} />

      {/* Threshold Warning Banner in Dark Amber Glassmorphism */}
      {totalLowStockAlerts > 0 && (
        <div
          className="p-3 mb-4 rounded-3 d-flex flex-wrap align-items-center justify-content-between gap-3"
          style={{
            backgroundColor: 'rgba(245, 158, 11, 0.08)',
            border: '1px solid rgba(245, 158, 11, 0.3)',
            boxShadow: '0 4px 20px -2px rgba(245, 158, 11, 0.1)',
          }}
        >
          <div className="d-flex align-items-center gap-3">
            <div
              className="rounded-3 d-flex align-items-center justify-content-center flex-shrink-0"
              style={{ width: '44px', height: '44px', backgroundColor: 'rgba(245, 158, 11, 0.15)', color: '#f59e0b' }}
            >
              <i className="bi bi-exclamation-triangle-fill fs-4"></i>
            </div>
            <div>
              <div className="fw-bold text-white">Stock Threshold Warning</div>
              <div className="small" style={{ color: '#cbd5e1' }}>
                {summary?.lowStockFuelCount > 0 && `${summary.lowStockFuelCount} fuel tank(s) `}
                {summary?.lowStockFuelCount > 0 && summary?.lowStockSparePartsCount > 0 && 'and '}
                {summary?.lowStockSparePartsCount > 0 && `${summary.lowStockSparePartsCount} spare part(s) `}
                are currently at or below their safety reorder level.
              </div>
            </div>
          </div>
          <div className="d-flex gap-2">
            {summary?.lowStockFuelCount > 0 && (
              <button
                className="btn btn-sm fw-semibold rounded-pill px-3"
                style={{ backgroundColor: '#f59e0b', color: '#081220' }}
                onClick={() => onNavigateToFuel(true)}
              >
                Inspect Fuel
              </button>
            )}
            {summary?.lowStockSparePartsCount > 0 && (
              <button
                className="btn btn-sm fw-semibold rounded-pill px-3"
                style={{ backgroundColor: '#f59e0b', color: '#081220' }}
                onClick={() => onNavigateToSpareParts(true)}
              >
                Inspect Parts
              </button>
            )}
          </div>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard
            title="Total Fuel Tanks"
            value={summary?.totalFuelTypes || 0}
            subtitle={`${totalFuelCap.toLocaleString()} L Total Capacity`}
            icon="bi-fuel-pump-fill"
            iconBg="#3b82f6"
            onClick={() => onNavigateToFuel(false)}
          />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard
            title="Total Spare Parts"
            value={summary?.totalSpareParts || 0}
            subtitle={`${summary?.totalSparePartsUnits?.toLocaleString() || 0} Units in Stock`}
            icon="bi-gear-wide-connected"
            iconBg="#8b5cf6"
            onClick={() => onNavigateToSpareParts(false)}
          />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard
            title="Low Stock Fuel"
            value={summary?.lowStockFuelCount || 0}
            subtitle={summary?.lowStockFuelCount > 0 ? 'Requires Immediate Tank Refill' : 'All Tanks Sufficient'}
            icon="bi-droplet-half"
            iconBg="#ef4444"
            alert={summary?.lowStockFuelCount > 0}
            onClick={() => onNavigateToFuel(true)}
          />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard
            title="Low Stock Parts"
            value={summary?.lowStockSparePartsCount || 0}
            subtitle={summary?.lowStockSparePartsCount > 0 ? 'At or Below Reorder Level' : 'Adequate Spare Units'}
            icon="bi-box-seam"
            iconBg="#f59e0b"
            alert={summary?.lowStockSparePartsCount > 0}
            onClick={() => onNavigateToSpareParts(true)}
          />
        </div>
      </div>

      {/* Overall Fuel Storage Meter Card */}
      <div
        className="card border-0 shadow-sm rounded-4 mb-4 p-4"
        style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
      >
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div className="d-flex align-items-center gap-2">
            <div
              className="rounded-2 d-flex align-items-center justify-content-center"
              style={{ width: '32px', height: '32px', backgroundColor: 'rgba(59, 130, 246, 0.15)', color: '#3b82f6' }}
            >
              <i className="bi bi-speedometer fs-5"></i>
            </div>
            <h5 className="fw-bold mb-0 text-white">Station Overall Fuel Storage Meter</h5>
          </div>
          <span className="font-monospace fw-bold px-3 py-1 rounded-pill" style={{ backgroundColor: '#151f32', color: '#3b82f6', border: '1px solid #1e293b' }}>
            {overallFuelPct}% Filled
          </span>
        </div>
        <div className="progress mb-3" style={{ height: '14px', backgroundColor: '#151f32', borderRadius: '7px' }}>
          <div
            className="progress-bar rounded-pill"
            role="progressbar"
            style={{
              width: `${overallFuelPct}%`,
              backgroundColor: overallFuelPct < 25 ? '#ef4444' : overallFuelPct < 50 ? '#f59e0b' : '#10b981',
              boxShadow: `0 0 10px ${overallFuelPct < 25 ? '#ef4444' : overallFuelPct < 50 ? '#f59e0b' : '#10b981'}`,
            }}
          ></div>
        </div>
        <div className="d-flex justify-content-between small font-monospace" style={{ color: '#94a3b8' }}>
          <span>Remaining: <strong className="text-white">{totalFuelLiters.toLocaleString()}</strong> Liters</span>
          <span>Max Storage: <strong className="text-white">{totalFuelCap.toLocaleString()}</strong> Liters</span>
        </div>
      </div>

      {/* Watchlist Section */}
      <div className="row g-4">
        {/* Low Stock Fuel Watchlist */}
        <div className="col-lg-6">
          <div
            className="card border-0 shadow-sm rounded-4 h-100"
            style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
          >
            <div
              className="card-header border-0 pt-3 px-3 d-flex justify-content-between align-items-center"
              style={{ backgroundColor: 'transparent' }}
            >
              <h6 className="fw-bold mb-0 text-danger d-flex align-items-center gap-2">
                <i className="bi bi-exclamation-octagon-fill"></i> Low Stock Fuel Watchlist
              </h6>
              <button
                className="btn btn-sm btn-link text-decoration-none fw-semibold"
                style={{ color: '#3b82f6', fontSize: '0.85rem' }}
                onClick={() => onNavigateToFuel(true)}
              >
                View All
              </button>
            </div>
            <div className="card-body p-0">
              {lowStockFuels.length === 0 ? (
                <div className="text-center py-5 small" style={{ color: '#10b981' }}>
                  <i className="bi bi-check-circle-fill fs-2 d-block mb-2"></i>
                  No fuel tanks below reorder level.
                </div>
              ) : (
                <div className="table-responsive border-0">
                  <table className="table align-middle mb-0 small">
                    <thead>
                      <tr>
                        <th className="ps-3">Fuel Type</th>
                        <th>Tank</th>
                        <th className="text-end">Remaining</th>
                        <th className="text-end">Reorder Level</th>
                        <th className="text-center">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {lowStockFuels.slice(0, 5).map((f) => (
                        <tr key={f.fuelStockId}>
                          <td className="ps-3 fw-semibold text-white">{f.fuelType}</td>
                          <td>
                            <span
                              className="badge font-monospace px-2 py-1"
                              style={{ backgroundColor: '#151f32', color: '#94a3b8', border: '1px solid #1e293b' }}
                            >
                              {f.tankNo}
                            </span>
                          </td>
                          <td className="text-end font-monospace text-danger fw-bold">{f.currentQuantity} L</td>
                          <td className="text-end font-monospace" style={{ color: '#94a3b8' }}>{f.reorderLevel} L</td>
                          <td className="text-center"><StockBadge status={f.status} lowStock={true} /></td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Low Stock Spare Parts Watchlist */}
        <div className="col-lg-6">
          <div
            className="card border-0 shadow-sm rounded-4 h-100"
            style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
          >
            <div
              className="card-header border-0 pt-3 px-3 d-flex justify-content-between align-items-center"
              style={{ backgroundColor: 'transparent' }}
            >
              <h6 className="fw-bold mb-0 text-danger d-flex align-items-center gap-2">
                <i className="bi bi-exclamation-triangle-fill"></i> Low Stock Spare Parts Watchlist
              </h6>
              <button
                className="btn btn-sm btn-link text-decoration-none fw-semibold"
                style={{ color: '#3b82f6', fontSize: '0.85rem' }}
                onClick={() => onNavigateToSpareParts(true)}
              >
                View All
              </button>
            </div>
            <div className="card-body p-0">
              {lowStockParts.length === 0 ? (
                <div className="text-center py-5 small" style={{ color: '#10b981' }}>
                  <i className="bi bi-check-circle-fill fs-2 d-block mb-2"></i>
                  No spare parts below reorder level.
                </div>
              ) : (
                <div className="table-responsive border-0">
                  <table className="table align-middle mb-0 small">
                    <thead>
                      <tr>
                        <th className="ps-3">Part Name</th>
                        <th>Category</th>
                        <th className="text-end">In Stock</th>
                        <th className="text-end">Reorder Level</th>
                        <th className="text-center">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {lowStockParts.slice(0, 5).map((p) => (
                        <tr key={p.partId}>
                          <td className="ps-3 fw-semibold text-white">{p.partName}</td>
                          <td>
                            <span
                              className="badge px-2 py-1"
                              style={{ backgroundColor: '#151f32', color: '#94a3b8', border: '1px solid #1e293b' }}
                            >
                              {p.category}
                            </span>
                          </td>
                          <td className="text-end font-monospace text-danger fw-bold">{p.quantity}</td>
                          <td className="text-end font-monospace" style={{ color: '#94a3b8' }}>{p.reorderLevel}</td>
                          <td className="text-center"><StockBadge status={p.stockStatus} lowStock={true} /></td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}