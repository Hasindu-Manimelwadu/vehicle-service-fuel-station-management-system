import React, { useState, useEffect } from 'react';
import Navbar from './components/common/Navbar';
import Sidebar from './components/common/Sidebar';
import InventoryDashboard from './pages/InventoryDashboard';
import FuelStockPage from './pages/FuelStockPage';
import SparePartsPage from './pages/SparePartsPage';
import dashboardApi from './api/dashboardApi';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [activeRole, setActiveRole] = useState('STAFF');

  const [lowStockFuelCount, setLowStockFuelCount] = useState(0);
  const [lowStockPartsCount, setLowStockPartsCount] = useState(0);
  const [fuelLowStockFilter, setFuelLowStockFilter] = useState(false);
  const [partsLowStockFilter, setPartsLowStockFilter] = useState(false);

  const fetchBadgeCounts = async () => {
    try {
      const res = await dashboardApi.getSummary();
      if (res && res.data) {
        setLowStockFuelCount(res.data.lowStockFuelCount || 0);
        setLowStockPartsCount(res.data.lowStockSparePartsCount || 0);
      }
    } catch (err) {
      console.warn('Backend telemetry polling notice:', err.message);
    }
  };

  useEffect(() => {
    fetchBadgeCounts();
    const interval = setInterval(fetchBadgeCounts, 15000);
    return () => clearInterval(interval);
  }, []);

  const handleNavigateToFuel = (filterLowStock = false) => {
    setFuelLowStockFilter(filterLowStock);
    setActiveTab('fuel');
  };

  const handleNavigateToSpareParts = (filterLowStock = false) => {
    setPartsLowStockFilter(filterLowStock);
    setActiveTab('spareparts');
  };

  return (
    <div className="d-flex flex-column min-vh-100" style={{ backgroundColor: '#081220' }}>
      <Navbar
        activeRole={activeRole}
        setActiveRole={setActiveRole}
      />

      <div className="container-fluid flex-grow-1 p-0">
        <div className="row g-0">
          <div className="col-12 col-md-3 col-lg-2 d-none d-md-block">
            <Sidebar
              activeTab={activeTab}
              setActiveTab={(tab) => {
                if (tab === 'fuel') setFuelLowStockFilter(false);
                if (tab === 'spareparts') setPartsLowStockFilter(false);
                setActiveTab(tab);
              }}
              lowStockFuelCount={lowStockFuelCount}
              lowStockPartsCount={lowStockPartsCount}
            />
          </div>

          {/* Mobile Tab Navigation */}
          <div
            className="col-12 d-md-none p-2 border-bottom shadow-sm"
            style={{ backgroundColor: '#0b132b', borderColor: '#1e293b' }}
          >
            <div className="btn-group w-100" role="group">
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'dashboard' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => setActiveTab('dashboard')}
              >
                Dashboard
              </button>
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'fuel' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => {
                  setFuelLowStockFilter(false);
                  setActiveTab('fuel');
                }}
              >
                Fuel Stocks
              </button>
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'spareparts' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => {
                  setPartsLowStockFilter(false);
                  setActiveTab('spareparts');
                }}
              >
                Spare Parts
              </button>
            </div>
          </div>

          <div className="col-12 col-md-9 col-lg-10">
            {activeTab === 'dashboard' && (
              <InventoryDashboard
                onNavigateToFuel={handleNavigateToFuel}
                onNavigateToSpareParts={handleNavigateToSpareParts}
              />
            )}
            {activeTab === 'fuel' && (
              <FuelStockPage defaultLowStock={fuelLowStockFilter} />
            )}
            {activeTab === 'spareparts' && (
              <SparePartsPage defaultLowStock={partsLowStockFilter} />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}