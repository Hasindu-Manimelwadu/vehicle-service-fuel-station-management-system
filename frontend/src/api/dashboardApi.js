import axiosClient from './axiosClient';

const dashboardApi = {
  getSummary: () => axiosClient.get('/inventory/dashboard-summary'),
};

export default dashboardApi;