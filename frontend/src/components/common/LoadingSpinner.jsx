import React from 'react';

export default function LoadingSpinner({ message = 'Loading inventory records...' }) {
  return (
    <div className="text-center py-5">
      <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
        <span className="visually-hidden">Loading...</span>
      </div>
      <div className="fw-semibold mt-3" style={{ color: '#94a3b8' }}>{message}</div>
    </div>
  );
}