import React from 'react';

export default function StockBadge({ status, lowStock = false }) {
  if (lowStock || status === 'LOW STOCK' || status === 'LOW_STOCK') {
    return (
      <span className="badge badge-low-stock px-2.5 py-1 rounded-pill" style={{ fontSize: '0.72rem', letterSpacing: '0.04em' }}>
        <i className="bi bi-exclamation-circle-fill me-1"></i> LOW STOCK
      </span>
    );
  }

  if (status === 'EMPTY' || status === 'OUT_OF_STOCK' || status === 'OUT OF STOCK') {
    return (
      <span className="badge badge-out-of-stock px-2.5 py-1 rounded-pill" style={{ fontSize: '0.72rem', letterSpacing: '0.04em' }}>
        <i className="bi bi-x-circle-fill me-1"></i> OUT OF STOCK
      </span>
    );
  }

  if (status === 'DISCONTINUED') {
    return (
      <span className="badge bg-secondary bg-opacity-25 text-secondary border border-secondary border-opacity-25 px-2.5 py-1 rounded-pill" style={{ fontSize: '0.72rem' }}>
        <i className="bi bi-slash-circle me-1"></i> DISCONTINUED
      </span>
    );
  }

  return (
    <span className="badge badge-in-stock px-2.5 py-1 rounded-pill" style={{ fontSize: '0.72rem', letterSpacing: '0.04em' }}>
      <i className="bi bi-check-circle-fill me-1"></i> IN STOCK
    </span>
  );
}