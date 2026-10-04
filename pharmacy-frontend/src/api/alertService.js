import apiClient from './client';

const alertService = {
  getActiveAlerts: () => apiClient.get('/alerts').then((res) => res.data.data),

  resolveAlert: (id) => apiClient.put(`/alerts/${id}/resolve`).then((res) => res.data.data),

  runCheckNow: () => apiClient.post('/alerts/check-now'),
};

export default alertService;
