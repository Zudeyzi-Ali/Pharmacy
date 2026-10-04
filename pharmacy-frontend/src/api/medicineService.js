import apiClient from './client';

const medicineService = {
  getAll: (name, categoryId) => {
    const params = {};
    if (name) params.name = name;
    if (categoryId) params.categoryId = categoryId;
    return apiClient.get('/medicines', { params }).then((res) => res.data.data);
  },

  getById: (id) => apiClient.get(`/medicines/${id}`).then((res) => res.data.data),

  create: (medicine) => apiClient.post('/medicines', medicine).then((res) => res.data.data),

  update: (id, medicine) => apiClient.put(`/medicines/${id}`, medicine).then((res) => res.data.data),

  remove: (id) => apiClient.delete(`/medicines/${id}`),

  getCategories: () => apiClient.get('/categories').then((res) => res.data.data),
};

export default medicineService;
