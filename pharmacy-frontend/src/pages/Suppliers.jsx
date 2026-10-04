import { useEffect, useState } from "react";
import supplierService from "../api/supplierService";
import { useAuth } from "../context/AuthContext";

const emptyForm = {
  id: null,
  name: "",
  contactPerson: "",
  email: "",
  phone: "",
  address: "",
  active: true,
};

function validate(form) {
  const errors = {};
  if (!form.name.trim()) errors.name = "Supplier name is required";
  else if (form.name.length > 100) errors.name = "Name must be under 100 characters";

  if (!form.email.trim()) errors.email = "Email is required";
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = "Enter a valid email";

  if (form.phone && !/^\+?[0-9\-\s]{7,15}$/.test(form.phone)) errors.phone = "Enter a valid phone number";

  if (form.address && form.address.length > 255) errors.address = "Address must be under 255 characters";

  return errors;
}

export default function Suppliers() {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const [suppliers, setSuppliers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [serverError, setServerError] = useState("");
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    loadSuppliers();
  }, []);

  function loadSuppliers() {
    setLoading(true);
    supplierService
      .getAll()
      .then(setSuppliers)
      .catch(() => setServerError("Failed to load suppliers"))
      .finally(() => setLoading(false));
  }

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
  }

  function handleEdit(supplier) {
    setForm(supplier);
    setErrors({});
    setShowForm(true);
  }

  function handleNew() {
    setForm(emptyForm);
    setErrors({});
    setShowForm(true);
  }

  function handleDelete(id) {
    if (!window.confirm("Delete this supplier?")) return;
    supplierService
      .remove(id)
      .then(loadSuppliers)
      .catch(() => setServerError("Failed to delete supplier"));
  }

  function handleSubmit(e) {
    e.preventDefault();
    const validationErrors = validate(form);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    setServerError("");
    const action = form.id
      ? supplierService.update(form.id, form)
      : supplierService.create(form);

    action
      .then(() => {
        setShowForm(false);
        setForm(emptyForm);
        loadSuppliers();
      })
      .catch((err) => {
        const message = err.response?.data?.message || "Failed to save supplier";
        setServerError(message);
      });
  }

  return (
    <div className="suppliers-page">
      <div className="page-header">
        <h2>Suppliers</h2>
        {isAdmin && <button onClick={handleNew}>Add Supplier</button>}
      </div>

      {serverError && <div className="error-banner">{serverError}</div>}

      {isAdmin && showForm && (
        <form onSubmit={handleSubmit} className="supplier-form">
          <div className="form-field">
            <label>Name</label>
            <input name="name" value={form.name} onChange={handleChange} />
            {errors.name && <span className="field-error">{errors.name}</span>}
          </div>

          <div className="form-field">
            <label>Contact Person</label>
            <input name="contactPerson" value={form.contactPerson || ""} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label>Email</label>
            <input name="email" value={form.email} onChange={handleChange} />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="form-field">
            <label>Phone</label>
            <input name="phone" value={form.phone || ""} onChange={handleChange} />
            {errors.phone && <span className="field-error">{errors.phone}</span>}
          </div>

          <div className="form-field">
            <label>Address</label>
            <textarea name="address" value={form.address || ""} onChange={handleChange} />
            {errors.address && <span className="field-error">{errors.address}</span>}
          </div>

          <div className="form-field checkbox-field">
            <label>
              <input type="checkbox" name="active" checked={form.active} onChange={handleChange} />
              Active
            </label>
          </div>

          <div className="form-actions">
            <button type="submit">Save</button>
            <button type="button" onClick={() => setShowForm(false)}>Cancel</button>
          </div>
        </form>
      )}

      {loading ? (
        <p>Loading suppliers...</p>
      ) : (
        <table className="suppliers-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Contact</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Status</th>
              {isAdmin && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {suppliers.map((s) => (
              <tr key={s.id}>
                <td>{s.name}</td>
                <td>{s.contactPerson}</td>
                <td>{s.email}</td>
                <td>{s.phone}</td>
                <td>{s.active ? "Active" : "Inactive"}</td>
                {isAdmin && (
                  <td>
                    <button onClick={() => handleEdit(s)}>Edit</button>
                    <button onClick={() => handleDelete(s.id)}>Delete</button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
