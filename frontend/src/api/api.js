import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// ----- Customer endpoints -----
export const registerCustomer = (customer) => api.post("/customers/register", customer);
export const getCustomer = (id) => api.get(`/customers/${id}`);
export const updateCustomer = (id, customer) => api.put(`/customers/${id}`, customer);
export const deleteCustomer = (id) => api.delete(`/customers/${id}`);

// ----- Vehicle endpoints -----
export const registerVehicle = (customerId, vehicle) =>
  api.post(`/vehicles/customer/${customerId}`, vehicle);
export const getVehiclesByCustomer = (customerId) =>
  api.get(`/vehicles/customer/${customerId}`);
export const updateVehicle = (id, vehicle) => api.put(`/vehicles/${id}`, vehicle);
export const deleteVehicle = (id) => api.delete(`/vehicles/${id}`);
export const getVehicleServiceHistory = (id) => api.get(`/vehicles/${id}/service-history`);

export default api;
