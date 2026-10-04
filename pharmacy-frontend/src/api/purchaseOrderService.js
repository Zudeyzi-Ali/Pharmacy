import apiClient from "./client";

// Every backend response is wrapped as { success, message, data } (ApiResponse<T>),
// so each call unwraps res.data.data instead of res.data.
const purchaseOrderService = {
  getAll: () => apiClient.get("/purchase-orders").then((res) => res.data.data),
  getById: (id) => apiClient.get(`/purchase-orders/${id}`).then((res) => res.data.data),
  create: (order) => apiClient.post("/purchase-orders", order).then((res) => res.data.data),
  updateStatus: (id, status) =>
    apiClient.patch(`/purchase-orders/${id}/status`, { status }).then((res) => res.data.data),
  remove: (id) => apiClient.delete(`/purchase-orders/${id}`).then((res) => res.data.data),
};

export default purchaseOrderService;
