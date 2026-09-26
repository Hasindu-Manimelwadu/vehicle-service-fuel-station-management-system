import React from 'react';
import StockBadge from '../common/StockBadge';

export default function SparePartTable({
  parts = [],
  loading = false,
  onView,
  onEdit,
  onUpdateQuantity,
  onDelete,
}) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status"></div>
        <div className="text-muted mt-2">Loading spare parts catalog...</div>
      </div>
    );
  }

  if (parts.length === 0) {
    return (
      <div
        className="text-center py-5 rounded-4 p-4"
        style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
      >
        <div
          className="rounded-circle d-inline-flex p-3 mb-3"
          style={{ backgroundColor: '#151f32', color: '#64748b' }}
        >
          <i className="bi bi-gear-wide-connected fs-1"></i>
        </div>
        <h5 className="text-white fw-bold">No Spare Parts Found</h5>
        <p className="small mb-0" style={{ color: '#94a3b8' }}>
          No parts match your selected category, status, or search query.
        </p>
      </div>
    );
  }

  return (
    <div className="table-responsive shadow-sm" style={{ border: '1px solid #1e293b', borderRadius: '0.85rem' }}>
      <table className="table align-middle mb-0">
        <thead>
          <tr>
            <th scope="col" className="ps-3">Part ID</th>
            <th scope="col">Part Name</th>
            <th scope="col">Category</th>
            <th scope="col" className="text-end">Units in Stock</th>
            <th scope="col" className="text-end">Reorder Level</th>
            <th scope="col" className="text-end">Unit Price (LKR)</th>
            <th scope="col" className="text-center">Status</th>
            <th scope="col" className="text-end pe-3">Actions</th>
          </tr>
        </thead>
        <tbody>
          {parts.map((part) => {
            const isLow = part.lowStock;

            return (
              <tr
                key={part.partId}
                style={{
                  backgroundColor: isLow ? 'rgba(239, 68, 68, 0.05)' : 'transparent',
                  borderLeft: isLow ? '3px solid #ef4444' : '3px solid transparent',
                }}
              >
                <td className="ps-3 font-monospace text-muted small">#{part.partId}</td>
                <td className="fw-bold text-white">{part.partName}</td>
                <td>
                  <span
                    className="badge px-2.5 py-1"
                    style={{ backgroundColor: '#151f32', color: '#94a3b8', border: '1px solid #1e293b' }}
                  >
                    {part.category}
                  </span>
                </td>
                <td className="text-end font-monospace fw-bold text-white">
                  {part.quantity} units
                </td>
                <td className="text-end font-monospace" style={{ color: '#94a3b8' }}>
                  {part.reorderLevel} units
                </td>
                <td className="text-end font-monospace fw-semibold" style={{ color: '#38bdf8' }}>
                  Rs. {Number(part.unitPrice).toFixed(2)}
                </td>
                <td className="text-center">
                  <StockBadge status={part.stockStatus || part.status} lowStock={part.lowStock} />
                </td>
                <td className="text-end pe-3">
                  <div className="btn-group btn-group-sm" style={{ backgroundColor: '#151f32', borderRadius: '0.4rem', border: '1px solid #1e293b' }}>
                    <button
                      type="button"
                      className="btn btn-sm text-secondary"
                      onClick={() => onView(part)}
                      title="View Part Specifications"
                    >
                      <i className="bi bi-eye"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-primary"
                      onClick={() => onEdit(part)}
                      title="Edit Part Details"
                    >
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-info"
                      onClick={() => onUpdateQuantity(part)}
                      title="Adjust Units (Add/Deduct/Set)"
                    >
                      <i className="bi bi-plus-slash-minus"></i>
                    </button>
                    <button
                      type="button"
                      className="btn btn-sm text-danger"
                      onClick={() => onDelete(part)}
                      title="Remove Obsolete Part"
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