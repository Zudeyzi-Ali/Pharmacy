import { useEffect, useState } from "react";
import purchaseOrderService from "../api/purchaseOrderService";
import supplierService from "../api/supplierService";
import apiClient from "../api/client";
import { useAuth } from "../context/AuthContext";

const emptyItem = { medicineId: "", quantity: 1, unitPrice: "" };

const STATUS_TRANSITIONS = {
  PENDING: ["APPROVED", "CANCELLED"],
  APPROVED: ["DELIVERED", "CANCELLED"],
  DELIVERED: [],
  CANCELLED: [],
};

function validateOrder(supplierId, items) {
  const errors = {};
  if (!supplierId) errors.supplierId = "Supplier is required";

  const itemErrors = items.map((item) => {
    const err = {};
    if (!item.medicineId) err.medicineId = "Medicine is required";
    if (!item.quantity || item.quantity < 1) err.quantity = "Quantity must be at least 1";
    if (!item.unitPrice || Number(item.unitPrice) <= 0) err.unitPrice = "Unit price must be greater than 0";
    return err;
  });

  if (itemErrors.some((e) => Object.keys(e).length > 0)) {
    errors.items = itemErrors;
  }
  if (items.length === 0) {
    errors.general = "At least one item is required";
  }

  return errors;
}

export default function PurchaseOrders() {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const [orders, setOrders] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [serverError, setServerError] = useState("");
  const [showForm, setShowForm] = useState(false);

  const [supplierId, setSupplierId] = useState("");
  const [expectedDeliveryDate, setExpectedDeliveryDate] = useState("");
  const [items, setItems] = useState([{ ...emptyItem }]);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    loadOrders();
    supplierService.getAll().then(setSuppliers).catch(() => {});
    // Every response is wrapped in ApiResponse<T>, so the list lives at
    // res.data.data.
    apiClient
      .get("/medicines")
      .then((res) => setMedicines(res.data.data))
      .catch(() => {});
  }, []);

  function loadOrders() {
    setLoading(true);
    purchaseOrderService
      .getAll()
      .then(setOrders)
      .catch(() => setServerError("Failed to load purchase orders"))
      .finally(() => setLoading(false));
  }

  function resetForm() {
    setSupplierId("");
    setExpectedDeliveryDate("");
    setItems([{ ...emptyItem }]);
    setErrors({});
  }

  function handleItemChange(index, field, value) {
    setItems((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [field]: value };
      return next;
    });
  }

  function addItemRow() {
    setItems((prev) => [...prev, { ...emptyItem }]);
  }

  function removeItemRow(index) {
    setItems((prev) => prev.filter((_, i) => i !== index));
  }

  function handleSubmit(e) {
    e.preventDefault();
    const validationErrors = validateOrder(supplierId, items);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    setServerError("");
    const payload = {
      supplierId: Number(supplierId),
      expectedDeliveryDate: expectedDeliveryDate || null,
      items: items.map((item) => ({
        medicineId: Number(item.medicineId),
        quantity: Number(item.quantity),
        unitPrice: Number(item.unitPrice),
      })),
    };

    purchaseOrderService
      .create(payload)
      .then(() => {
        setShowForm(false);
        resetForm();
        loadOrders();
      })
      .catch((err) => {
        const message = err.response?.data?.message || "Failed to create purchase order";
        setServerError(message);
      });
  }

  function handleStatusChange(order, newStatus) {
    if (newStatus === "CANCELLED" && !window.confirm("Cancel this purchase order?")) return;
    if (newStatus === "DELIVERED" && !window.confirm("Mark as delivered? This will update stock levels.")) return;

    purchaseOrderService
      .updateStatus(order.id, newStatus)
      .then(loadOrders)
      .catch((err) => {
        const message = err.response?.data?.message || "Failed to update status";
        setServerError(message);
      });
  }

  return (
    <div className="purchase-orders-page">
      <div className="page-header">
        <h2>Purchase Orders</h2>
        <button onClick={() => { resetForm(); setShowForm(true); }}>New Purchase Order</button>
      </div>

      {serverError && <div className="error-banner">{serverError}</div>}

      {showForm && (
        <form onSubmit={handleSubmit} className="po-form">
          <div className="form-field">
            <label>Supplier</label>
            <select value={supplierId} onChange={(e) => setSupplierId(e.target.value)}>
              <option value="">Select a supplier</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id}>{s.name}</option>
              ))}
            </select>
            {errors.supplierId && <span className="field-error">{errors.supplierId}</span>}
          </div>

          <div className="form-field">
            <label>Expected Delivery Date</label>
            <input
              type="date"
              value={expectedDeliveryDate}
              onChange={(e) => setExpectedDeliveryDate(e.target.value)}
            />
          </div>

          <h4>Items</h4>
          {errors.general && <span className="field-error">{errors.general}</span>}

          {items.map((item, index) => (
            <div className="item-row" key={index}>
              <select
                value={item.medicineId}
                onChange={(e) => handleItemChange(index, "medicineId", e.target.value)}
              >
                <option value="">Select a medicine</option>
                {medicines.map((m) => (
                  <option key={m.id} value={m.id}>{m.name}</option>
                ))}
              </select>
              {errors.items?.[index]?.medicineId && (
                <span className="field-error">{errors.items[index].medicineId}</span>
              )}

              <input
                type="number"
                min="1"
                placeholder="Quantity"
                value={item.quantity}
                onChange={(e) => handleItemChange(index, "quantity", e.target.value)}
              />
              {errors.items?.[index]?.quantity && (
                <span className="field-error">{errors.items[index].quantity}</span>
              )}

              <input
                type="number"
                min="0"
                step="0.01"
                placeholder="Unit Price"
                value={item.unitPrice}
                onChange={(e) => handleItemChange(index, "unitPrice", e.target.value)}
              />
              {errors.items?.[index]?.unitPrice && (
                <span className="field-error">{errors.items[index].unitPrice}</span>
              )}

              <button type="button" onClick={() => removeItemRow(index)}>Remove</button>
            </div>
          ))}

          <button type="button" onClick={addItemRow}>Add Item</button>

          <div className="form-actions">
            <button type="submit">Create Order</button>
            <button type="button" onClick={() => setShowForm(false)}>Cancel</button>
          </div>
        </form>
      )}

      {loading ? (
        <p>Loading purchase orders...</p>
      ) : (
        <table className="purchase-orders-table">
          <thead>
            <tr>
              <th>Supplier</th>
              <th>Order Date</th>
              <th>Expected Delivery</th>
              <th>Status</th>
              <th>Total</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{order.supplierName}</td>
                <td>{order.orderDate}</td>
                <td>{order.expectedDeliveryDate || "-"}</td>
                <td>{order.status}</td>
                <td>{order.totalAmount}</td>
                <td>
                  {isAdmin
                    ? STATUS_TRANSITIONS[order.status]?.map((nextStatus) => (
                        <button key={nextStatus} onClick={() => handleStatusChange(order, nextStatus)}>
                          Mark {nextStatus}
                        </button>
                      ))
                    : <span className="muted">—</span>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
