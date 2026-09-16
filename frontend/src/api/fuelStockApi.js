import axiosClient from './axiosClient';

const fuelStockApi = {
  getAll: (params = {}) => axiosClient.get('/fuel-stocks', { params }),
  getById: (id) => axiosClient.get(`/fuel-stocks/${id}`),
  create: (data) => axiosClient.post('/fuel-stocks', data),
  update: (id, data) => axiosClient.put(`/fuel-stocks/${id}`, data),
  delete: (id) => axiosClient.delete(`/fuel-stocks/${id}`),
  updateQuantity: (id, data) => axiosClient.patch(`/fuel-stocks/${id}/quantity`, data),
  updatePrice: (id, data) => axiosClient.patch(`/fuel-stocks/${id}/price`, data),
  getLowStock: () => axiosClient.get('/fuel-stocks/low-stock'),
};

export default fuelStockApi;