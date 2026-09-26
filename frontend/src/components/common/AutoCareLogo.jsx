import React from 'react';

export default function AutoCareLogo({ size = 40, showText = true, subtitle = 'Service Station' }) {
  return (
    <div className="d-flex align-items-center gap-3">
      <div
        className="d-flex align-items-center justify-content-center flex-shrink-0"
        style={{
          width: `${size}px`,
          height: `${size}px`,
          backgroundColor: '#ffffff',
          borderRadius: `${Math.round(size * 0.28)}px`,
          boxShadow: '0 4px 14px rgba(0, 0, 0, 0.3)',
        }}
      >
        <svg
          width={Math.round(size * 0.58)}
          height={Math.round(size * 0.58)}
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          stroke="#081220"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="m21 8-2 2-1.5-3.7A2 2 0 0 0 15.646 5H8.4a2 2 0 0 0-1.903 1.257L5 10 3 8" />
          <path d="M7 14h.01" />
          <path d="M17 14h.01" />
          <rect width="18" height="8" x="3" y="10" rx="2" />
          <path d="M5 18v2" />
          <path d="M19 18v2" />
        </svg>
      </div>

      {showText && (
        <div className="d-flex flex-column text-start">
          <span className="fw-bold text-white lh-1" style={{ fontSize: size > 40 ? '1.45rem' : '1.15rem' }}>
            AutoCare
          </span>
          {subtitle && (
            <span className="text-secondary small mt-1" style={{ fontSize: '0.78rem', letterSpacing: '0.02em' }}>
              {subtitle}
            </span>
          )}
        </div>
      )}
    </div>
  );
}
