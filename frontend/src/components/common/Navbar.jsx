import React from 'react';
import AutoCareLogo from './AutoCareLogo';

export default function Navbar({ activeRole, setActiveRole }) {
  return (
    <nav className="navbar navbar-expand-lg autocare-navbar sticky-top px-3 py-2">
      <div className="container-fluid">
        {/* Brand with Car Logo */}
        <div className="navbar-brand py-0 me-4">
          <AutoCareLogo size={36} />
        </div>

        <button
          className="navbar-toggler border-0"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          style={{ color: '#f8fafc' }}
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarContent">
          <div className="d-flex align-items-center ms-auto gap-3 flex-wrap">
            {/* Role Switcher */}
            <div
              className="btn-group btn-group-sm p-1 rounded-pill"
              style={{ backgroundColor: '#131e35', border: '1px solid #1e293b' }}
              role="group"
            >
              <button
                type="button"
                className={`btn btn-sm rounded-pill px-3 fw-semibold ${
                  activeRole === 'STAFF'
                    ? 'btn-primary'
                    : 'btn-link text-decoration-none text-secondary'
                }`}
                onClick={() => setActiveRole('STAFF')}
                title="Switch to Staff Member View"
              >
                <i className="bi bi-person-badge me-1"></i> Staff
              </button>
              <button
                type="button"
                className={`btn btn-sm rounded-pill px-3 fw-semibold ${
                  activeRole === 'ADMIN'
                    ? 'btn-primary'
                    : 'btn-link text-decoration-none text-secondary'
                }`}
                onClick={() => setActiveRole('ADMIN')}
                title="Switch to Administrator View"
              >
                <i className="bi bi-shield-lock me-1"></i> Admin
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}