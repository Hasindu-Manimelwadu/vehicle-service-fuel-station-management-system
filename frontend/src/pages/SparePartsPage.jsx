import React, { useState, useEffect } from 'react';
import sparePartApi from '../api/sparePartApi';
import SparePartTable from '../components/spareparts/SparePartTable';
import SparePartFormModal from '../components/spareparts/SparePartFormModal';
import SparePartQuantityModal from '../components/spareparts/SparePartQuantityModal';
import SparePartDetailsModal from '../components/spareparts/SparePartDetailsModal';
import ConfirmationModal from '../components/common/ConfirmationModal';
import ToastAlert from '../components/common/ToastAlert';

export default function SparePartsPage({ defaultLowStock = false }) {
  const [parts, setParts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [lowStockFilter, setLowStockFilter] = useState(defaultLowStock);
  const [alert, setAlert] = useState(null);

  const [showAddEdit, setShowAddEdit] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [selectedPart, setSelectedPart] = useState(null);

  const [showDetails, setShowDetails] = useState(false);
  const [showQuantityModal, setShowQuantityModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const [actionLoading, setActionLoading] = useState(false);

  const fetchCategories = async () => {
    try {
      const res = await sparePartApi.getCategories();
      setCategories(res.data || []);
    } catch (err) {
      console.error('Failed to load categories', err);
    }
  };

  const fetchParts = async () => {
    setLoading(true);
    try {
      const params = {};
      if (search.trim()) params.search = search.trim();
      if (categoryFilter) params.category = categoryFilter;
      if (statusFilter) params.status = statusFilter;
      if (lowStockFilter) params.lowStockOnly = true;

      const res = await sparePartApi.getAll(params);
      setParts(res.data || []);
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Error fetching spare parts catalog.',
        validationErrors: err.validationErrors,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchParts();
  }, [search, categoryFilter, statusFilter, lowStockFilter]);

  const handleOpenAdd = () => {
    setIsEdit(false);
    setSelectedPart(null);
    setShowAddEdit(true);
  };

  const handleOpenEdit = (part) => {
    setIsEdit(true);
    setSelectedPart(part);
    setShowAddEdit(true);
  };

  const handleOpenDetails = (part) => {
    setSelectedPart(part);
    setShowDetails(true);
  };

  const handleOpenQuantity = (part) => {
    setSelectedPart(part);
    setShowQuantityModal(true);
  };

  const handleOpenDelete = (part) => {
    setSelectedPart(part);
    setShowDeleteModal(true);
  };

  const handleSavePart = async (formData) => {
    setActionLoading(true);
    try {
      if (isEdit) {
        await sparePartApi.update(selectedPart.partId, formData);
        setAlert({ type: 'success', message: 'Spare part updated successfully.' });
      } else {
        await sparePartApi.create(formData);
        setAlert({ type: 'success', message: 'New spare part added to catalog.' });
      }
      setShowAddEdit(false);
      fetchParts();
      fetchCategories();
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
      await sparePartApi.updateQuantity(selectedPart.partId, updatePayload);
      setAlert({ type: 'success', message: 'Spare part inventory count adjusted.' });
      setShowQuantityModal(false);
      fetchParts();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Quantity adjustment failed.',
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    setActionLoading(true);
    try {
      await sparePartApi.delete(selectedPart.partId);
      setAlert({ type: 'success', message: 'Spare part removed from catalog.' });
      setShowDeleteModal(false);
      fetchParts();
    } catch (err) {
      setAlert({
        type: 'danger',
        message: err.message || 'Failed to remove spare part.',
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
              <i className="bi bi-gear-wide-connected text-primary"></i> Parts Catalog
            </span>
          </div>
          <h2 className="fw-bold mb-1 text-white">Spare Parts Management</h2>
          <p className="small mb-0" style={{ color: '#94a3b8' }}>
            Maintain automotive replacement parts, track unit counts, and enforce reorder thresholds
          </p>
        </div>
        <button
          className="btn btn-primary d-flex align-items-center gap-2 px-3.5 py-2 fw-semibold shadow-sm rounded-3"
          onClick={handleOpenAdd}
        >
          <i className="bi bi-plus-circle-fill"></i> Add Spare Part
        </button>
      </div>

      <ToastAlert alert={alert} onClose={() => setAlert(null)} />

      {/* Filter and Search Bar */}
      <div
        className="card border-0 shadow-sm rounded-4 p-3 mb-4"
        style={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }}
      >
        <div className="row g-2 align-items-center">
          <div className="col-12 col-md-4">
            <div className="input-group">
              <span className="input-group-text border-end-0" style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#94a3b8' }}>
                <i className="bi bi-search"></i>
              </span>
              <input
                type="text"
                className="form-control border-start-0"
                placeholder="Search by part name or keyword..."
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

          <div className="col-6 col-md-3">
            <select
              className="form-select"
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#f8fafc' }}
            >
              <option value="">All Categories</option>
              {categories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </div>

          <div className="col-6 col-md-2">
            <select
              className="form-select"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              style={{ backgroundColor: '#151f32', borderColor: '#1e293b', color: '#f8fafc' }}
            >
              <option value="">All Statuses</option>
              <option value="AVAILABLE">AVAILABLE</option>
              <option value="LOW_STOCK">LOW STOCK</option>
              <option value="OUT_OF_STOCK">OUT OF STOCK</option>
              <option value="DISCONTINUED">DISCONTINUED</option>
            </select>
          </div>

          <div className="col-12 col-md-3 d-flex justify-content-md-end align-items-center gap-2">
            <button
              type="button"
              className={`btn btn-sm d-flex align-items-center gap-2 rounded-pill px-3 py-1.5 ${
                lowStockFilter ? 'btn-danger' : 'btn-outline-secondary'
              }`}
              onClick={() => setLowStockFilter(!lowStockFilter)}
              style={!lowStockFilter ? { borderColor: '#1e293b' } : {}}
            >
              <i className="bi bi-funnel"></i>
              {lowStockFilter ? 'Low Stock Only' : 'Show Low Stock'}
            </button>
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary rounded-circle d-flex align-items-center justify-content-center"
              style={{ width: '34px', height: '34px', borderColor: '#1e293b' }}
              onClick={fetchParts}
              title="Refresh list"
            >
              <i className="bi bi-arrow-clockwise"></i>
            </button>
          </div>
        </div>
      </div>

      {/* Main Table */}
      <SparePartTable
        parts={parts}
        loading={loading}
        onView={handleOpenDetails}
        onEdit={handleOpenEdit}
        onUpdateQuantity={handleOpenQuantity}
        onDelete={handleOpenDelete}
      />

      {/* Modals */}
      <SparePartFormModal
        show={showAddEdit}
        isEdit={isEdit}
        initialData={selectedPart}
        categories={categories}
        onClose={() => setShowAddEdit(false)}
        onSave={handleSavePart}
        loading={actionLoading}
      />

      <SparePartQuantityModal
        show={showQuantityModal}
        part={selectedPart}
        onClose={() => setShowQuantityModal(false)}
        onSave={handleUpdateQuantity}
        loading={actionLoading}
      />

      <SparePartDetailsModal
        show={showDetails}
        part={selectedPart}
        onClose={() => setShowDetails(false)}
      />

      <ConfirmationModal
        show={showDeleteModal}
        title="Remove Spare Part"
        message="Are you sure you want to remove this spare part from inventory records?"
        itemName={selectedPart ? `${selectedPart.partName} (#${selectedPart.partId})` : ''}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setShowDeleteModal(false)}
        loading={actionLoading}
      />
    </div>
  );
}