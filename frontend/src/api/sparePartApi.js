import axiosClient from './axiosClient';

const sparePartApi = {
  getAll: (params = {}) => axiosClient.get('/spare-parts', { params }),
  getById: (id) => axiosClient.get(`/spare-parts/${id}`),
  create: (data) => axiosClient.post('/spare-parts', data),
  update: (id, data) => axiosClient.put(`/spare-parts/${id}`, data),
  delete: (id) => axiosClient.delete(`/spare-parts/${id}`),
  updateQuantity: (id, data) => axiosClient.patch(`/spare-parts/${id}/quantity`, data),
  getLowStock: () => axiosClient.get('/spare-parts/low-stock'),
  getCategories: () => axiosClient.get('/spare-parts/categories'),
};

export default sparePartApi;