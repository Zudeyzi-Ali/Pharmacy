import apiClient from "./client";

// Every backend response is wrapped as { success, message, data } (ApiResponse<T>),
// so each call unwraps res.data.data instead of res.data.
const supplierService = {
  getAll: () => apiClient.get("/suppliers").then((res) => res.data.data),
  getById: (id) => apiClient.get(`/suppliers/${id}`).then((res) => res.data.data),
  create: (supplier) => apiClient.post("/suppliers", supplier).then((res) => res.data.data),
  update: (id, supplier) => apiClient.put(`/suppliers/${id}`, supplier).then((res) => res.data.data),
  remove: (id) => apiClient.delete(`/suppliers/${id}`).then((res) => res.data.data),
};

export default supplierService;
