import axios from 'axios';

const axiosClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 10000,
});

axiosClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    let errorPayload = {
      message: 'Network error or server unreachable. Please make sure the Spring Boot backend is running on port 8080.',
      status: 500,
      validationErrors: null,
    };

    if (error.response && error.response.data) {
      const data = error.response.data;
      errorPayload = {
        message: data.message || 'Operation failed.',
        status: error.response.status,
        validationErrors: data.validationErrors || null,
        error: data.error || 'Error',
      };
    } else if (error.message) {
      errorPayload.message = error.message;
    }

    return Promise.reject(errorPayload);
  }
);

export default axiosClient;