import React from 'react';
import StockBadge from '../common/StockBadge';

export default function FuelStockTable({
  stocks = [],
  loading = false,
  onView,
  onEdit,
  onUpdateQuantity,
  onUpdatePrice,
  onDelete,
}) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status"></div>
        <div className="text-muted mt-2">Loading fuel inventory...</div>
      </div>
    );
  }

  if (stocks.length === 0) {
    return (
      <div
        className="text-center py-5 rounded-4 p-4"
        style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
      >
        <div
          className="rounded-circle d-inline-flex p-3 mb-3"
          style={{ backgroundColor: '#151f32', color: '#64748b' }}
        >
          <i className="bi bi-fuel-pump fs-1"></i>
        </div>
        <h5 className="text-white fw-bold">No Fuel Tanks Found</h5>
        <p className="small mb-0" style={{ color: '#94a3b8' }}>
          No fuel records match your search or filter criteria.
        </p>
      </div>
    );
  }

  return (
    <div className="table-responsive shadow-sm" style={{ border: '1px solid #1e293b', borderRadius: '0.85rem' }}>
      <table className="table align-middle mb-0">
        <thead>
          <tr>
            <th scope="col" className="ps-3">Fuel Type</th>
            <th scope="col">Tank No</th>
            <th scope="col" className="text-end">Capacity (L)</th>
            <th scope="col" className="text-end">Current (L)</th>
            <th scope="col" style={{ minWidth: '130px' }}>Fill Level</th>
            <th scope="col" className="text-end">Reorder Level</th>
            <th scope="col" className="text-end">Unit Price (LKR)</th>
            <th scope="col" className="text-center">Status</th>
            <th scope="col" className="text-end pe-3">Actions</th>
          </tr>
        </thead>
        <tbody>
          {stocks.map((stock) => {
            const isLow = stock.lowStock;
            const pct = stock.percentageFilled || 0;
            const progressColor = pct < 20 ? '#ef4444' : pct < 40 ? '#f59e0b' : '#10b981';

            return (
              <tr
                key={stock.fuelStockId}
                style={{
                  backgroundColor: isLow ? 'rgba(239, 68, 68, 0.05)' : 'transparent',
                  borderLeft: isLow ? '3px solid #ef4444' : '3px solid transparent',
                }}
              >
                <td className="ps-3 fw-bold text-white">{stock.fuelType}</td>
                <td>
                  <span
                    className="badge font-monospace px-2 py-1"
                    style={{ backgroundColor: '#151f32', color: '#94a3b8', border: '1px solid #1e293b' }}
                  >
                    {stock.tankNo}
                  </span>
                </td>
                <td className="text-end font-monospace text-white">
                  {stock.tankCapacity?.toLocaleString()} L
                </td>
                <td className="text-end font-monospace fw-bold text-white">
                  {stock.currentQuantity?.toLocaleString()} L
                </td>
                <td>
                  <div className="d-flex align-items-center gap-2">
                    <div className="progress flex-grow-1" style={{ height: '8px', backgroundColor: '#151f32', borderRadius: '4px' }}>
                      <div
                        className="progress-bar rounded-pill"
                        role="progressbar"
                        style={{
                          width: `${Math.min(pct, 100)}%`,
                          backgroundColor: progressColor,
                        }}
                        aria-valuenow={pct}
                        aria-valuemin="0"
                        aria-valuemax="100"
                      ></div>
                    </div>
                    <span className="small font-monospace" style={{ minWidth: '40px', color: '#94a3b8' }}>
                      {pct}%
                    </span>
                  </div>
                </td>
                <td className="text-end font-monospace" style={{ color: '#94a3b8' }}>
                  {stock.reorderLevel?.toLocaleString()} L
                </td>
                <td className="text-end font-monospace fw-semibold" style={{ color: '#38bdf8' }}>
                  Rs. {Number(stock.unitPrice).toFixed(2)}
                </td>
                <td className="text-center">
                  <StockBadge status={stock.status} lowStock={stock.lowStock} />
                </td>
                <td className="text-end pe-3">
                  <div className="btn-group btn-group-sm" style={{ backgroundColor: '#151f32', borderRadius: '0.4rem', border: '1px solid #1e293b' }}>
                    <button
                      type="button"
                      className="btn btn-sm text-secondary"
                      onClick={() => onView(stock)}
                      title="View Tank Details"
                    >
                      <i className="bi bi-eye"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-primary"
                      onClick={() => onEdit(stock)}
                      title="Edit Tank Record"
                    >
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-info"
                      onClick={() => onUpdateQuantity(stock)}
                      title="Update Fuel Volume (Add/Deduct/Set)"
                    >
                      <i className="bi bi-droplet-half"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-success"
                      onClick={() => onUpdatePrice(stock)}
                      title="Update Unit Price"
                    >
                      <i className="bi bi-currency-dollar"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-danger"
                      onClick={() => onDelete(stock)}
                      title="Delete Fuel Tank"
                    >
                      <i className="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}