import React, { useState, useEffect } from 'react';
import fuelStockApi from '../api/fuelStockApi';
import FuelStockTable from '../components/fuel/FuelStockTable';
import FuelStockFormModal from '../components/fuel/FuelStockFormModal';
import FuelQuantityModal from '../components/fuel/FuelQuantityModal';
import FuelPriceModal from '../components/fuel/FuelPriceModal';
import FuelDetailsModal from '../components/fuel/FuelDetailsModal';
import ConfirmationModal from '../components/common/ConfirmationModal';
import ToastAlert from '../components/common/ToastAlert';

export default function FuelStockPage({ defaultLowStock = false }) {
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [lowStockFilter, setLowStockFilter] = useState(defaultLowStock);
  const [alert, setAlert] = useState(null);

  const [showAddEdit, setShowAddEdit] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [selectedStock, setSelectedStock] = useState(null);

  const [showDetails, setShowDetails] = useState(false);
  const [showQuantityModal, setShowQuantityModal] = useState(false);
  const [showPriceModal, setShowPriceModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const [actionLoading, setActionLoading] = useState(false);

  const fetchStocks = async () => {
    setLoading(true);
    try {
      const params = {};
      if (search.trim()) params.search = search.trim();
      if (lowStockFilter) params.lowStockOnly = true;

      const res = await fuelStockApi.getAll(params);
      setStocks(res.data || []);
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Error fetching fuel stock data.',
        validationErrors: err.validationErrors,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStocks();
  }, [search, lowStockFilter]);

  const handleOpenAdd = () => {
    setIsEdit(false);
    setSelectedStock(null);
    setShowAddEdit(true);
  };

  const handleOpenEdit = (stock) => {
    setIsEdit(true);
    setSelectedStock(stock);
    setShowAddEdit(true);
  };

  const handleOpenDetails = (stock) => {
    setSelectedStock(stock);
    setShowDetails(true);
  };

  const handleOpenQuantity = (stock) => {
    setSelectedStock(stock);
    setShowQuantityModal(true);
  };

  const handleOpenPrice = (stock) => {
    setSelectedStock(stock);
    setShowPriceModal(true);
  };

  const handleOpenDelete = (stock) => {
    setSelectedStock(stock);
    setShowDeleteModal(true);
  };

  const handleSaveStock = async (formData) => {
    setActionLoading(true);
    try {
      if (isEdit) {
        await fuelStockApi.update(selectedStock.fuelStockId, formData);
        setAlert({ type: 'success', message: 'Fuel stock updated successfully.' });
      } else {
        await fuelStockApi.create(formData);
        setAlert({ type: 'success', message: 'New fuel stock tank registered successfully.' });
      }
      setShowAddEdit(false);
      fetchStocks();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Operation failed.',
        validationErrors: err.validationErrors,
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdateQuantity = async (updatePayload) => {
    setActionLoading(true);
    try {
      await fuelStockApi.updateQuantity(selectedStock.fuelStockId, updatePayload);
      setAlert({ type: 'success', message: 'Fuel quantity adjusted successfully.' });
      setShowQuantityModal(false);
      fetchStocks();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Quantity adjustment failed.',
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdatePrice = async (pricePayload) => {
    setActionLoading(true);
    try {
      await fuelStockApi.updatePrice(selectedStock.fuelStockId, pricePayload);
      setAlert({ type: 'success', message: 'Fuel unit price updated successfully.' });
      setShowPriceModal(false);
      fetchStocks();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Price update failed.',
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    setActionLoading(true);
    try {
      await fuelStockApi.delete(selectedStock.fuelStockId);
      setAlert({ type: 'success', message: 'Fuel stock record deleted successfully.' });
      setShowDeleteModal(false);
      fetchStocks();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Failed to delete fuel stock record.',
      });
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="p-4" style={{ backgroundColor: '#081220', minHeight: '100%' }}>
      {/* Header */}
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
          <div className="d-flex align-items-center gap-2 mb-1">
            <span className="autocare-pill py-0.5 px-2.5" style={{ fontSize: '0.75rem' }}>
              <i className="bi bi-fuel-pump text-primary"></i> Station Storage
            </span>
          </div>
          <h2 className="fw-bold mb-1 text-white">Fuel Stock Management</h2>
          <p className="small mb-0" style={{ color: '#94a3b8' }}>
            Monitor tank storage capacities, manage fuel inventory levels, and update pump retail prices
          </p>
        </div>
        <button
          className="btn btn-primary d-flex align-items-center gap-2 px-3.5 py-2 fw-semibold shadow-sm rounded-3"
          onClick={handleOpenAdd}
        >
          <i className="bi bi-plus-circle-fill"></i> Add Fuel Stock
        </button>
      </div>

      <ToastAlert alert={alert} onClose={() => setAlert(null)} />

      {/* Filter and Search Bar */}
      <div
        className="card border-0 shadow-sm rounded-4 p-3 mb-4"
        style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
      >
        <div className="row g-2 align-items-center">
          <div className="col-12 col-md-6">
            <div className="input-group">
              <span className="input-group-text border-end-0" style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#94a3b8' }}>
                <i className="bi bi-search"></i>
              </span>
              <input
                type="text"
                className="form-control border-start-0"
                placeholder="Search by fuel type (e.g. Octane 92) or tank number..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              {search && (
                <button
                  className="btn btn-outline-secondary"
                  type="button"
                  onClick={() => setSearch('')}
                  style={{ borderColor: '#1e293b' }}
                >
                  <i className="bi bi-x"></i>
                </button>
              )}
            </div>
          </div>

          <div className="col-12 col-md-6 d-flex justify-content-md-end align-items-center gap-2">
            <button
              type="button"
              className={`btn btn-sm d-flex align-items-center gap-2 rounded-pill px-3 py-1.5 ${
                lowStockFilter ? 'btn-danger' : 'btn-outline-secondary'
              }`}
              onClick={() => setLowStockFilter(!lowStockFilter)}
              style={!lowStockFilter ? { borderColor: '#1e293b' } : {}}
            >
              <i className="bi bi-funnel"></i>
              {lowStockFilter ? 'Filtering: Low Stock Only' : 'Show Low Stock Only'}
            </button>
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary rounded-circle d-flex align-items-center justify-content-center"
              style={{ width: '34px', height: '34px', borderColor: '#1e293b' }}
              onClick={fetchStocks}
              title="Refresh table"
            >
              <i className="bi bi-arrow-clockwise"></i>
            </button>
          </div>
        </div>
      </div>

      {/* Main Table */}
      <FuelStockTable
        stocks={stocks}
        loading={loading}
        onView={handleOpenDetails}
        onEdit={handleOpenEdit}
        onUpdateQuantity={handleOpenQuantity}
        onUpdatePrice={handleOpenPrice}
        onDelete={handleOpenDelete}
      />

      {/* Modals */}
      <FuelStockFormModal
        show={showAddEdit}
        isEdit={isEdit}
        initialData={selectedStock}
        onClose={() => setShowAddEdit(false)}
        onSave={handleSaveStock}
        loading={actionLoading}
      />

      <FuelQuantityModal
        show={showQuantityModal}
        stock={selectedStock}
        onClose={() => setShowQuantityModal(false)}
        onSave={handleUpdateQuantity}
        loading={actionLoading}
      />

      <FuelPriceModal
        show={showPriceModal}
        stock={selectedStock}
        onClose={() => setShowPriceModal(false)}
        onSave={handleUpdatePrice}
        loading={actionLoading}
      />

      <FuelDetailsModal
        show={showDetails}
        stock={selectedStock}
        onClose={() => setShowDetails(false)}
      />

      <ConfirmationModal
        show={showDeleteModal}
        title="Delete Fuel Tank Record"
        message="Are you sure you want to delete this fuel storage tank from the database?"
        itemName={selectedStock ? `${selectedStock.fuelType} (${selectedStock.tankNo})` : ''}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setShowDeleteModal(false)}
        loading={actionLoading}
      />
    </div>
  );
}