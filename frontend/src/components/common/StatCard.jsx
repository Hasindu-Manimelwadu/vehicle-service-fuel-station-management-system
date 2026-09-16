import React from 'react';

export default function StatCard({ title, value, subtitle, icon, iconBg, alert = false, onClick }) {
  return (
    <div
      className={`stat-card p-4 h-100 ${alert ? 'border-danger' : ''} ${onClick ? 'cursor-pointer' : ''}`}
      onClick={onClick}
      style={{
        cursor: onClick ? 'pointer' : 'default',
        backgroundColor: '#0f172a',
        border: alert ? '1px solid rgba(239, 68, 68, 0.5)' : '1px solid #1e293b',
        boxShadow: alert ? '0 4px 20px -2px rgba(239, 68, 68, 0.15)' : '0 4px 20px -2px rgba(0, 0, 0, 0.35)',
      }}
    >
      <div className="d-flex align-items-center justify-content-between">
        <div>
          <span className="small text-uppercase fw-semibold" style={{ color: '#94a3b8', fontSize: '0.75rem', letterSpacing: '0.05em' }}>
            {title}
          </span>
          <h2 className={`mb-0 fw-bold mt-1 ${alert ? 'text-danger' : 'text-white'}`} style={{ fontSize: '1.85rem' }}>
            {value}
          </h2>
          {subtitle && (
            <div className="small mt-1" style={{ color: alert ? '#f87171' : '#64748b', fontSize: '0.82rem' }}>
              {subtitle}
            </div>
          )}
        </div>
        <div
          className="rounded-3 d-flex align-items-center justify-content-center flex-shrink-0"
          style={{
            width: '54px',
            height: '54px',
            backgroundColor: iconBg ? `${iconBg}20` : '#16233b',
            border: `1px solid ${iconBg ? `${iconBg}40` : '#1e293b'}`,
            color: iconBg || '#3b82f6',
          }}
        >
          <i className={`bi ${icon} fs-4`}></i>
        </div>
      </div>
    </div>
  );
}