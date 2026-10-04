import { useEffect, useState } from 'react';

const emptyForm = {
  name: '',
  batchNumber: '',
  categoryId: '',
  quantity: 0,
  reorderLevel: 0,
  expiryDate: '',
  price: '',
  supplier: '',
};

export default function MedicineForm({ categories, initialData, onSubmit, onCancel }) {
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    if (initialData) {
      setForm({
        name: initialData.name || '',
        batchNumber: initialData.batchNumber || '',
        categoryId: initialData.categoryId || '',
        quantity: initialData.quantity ?? 0,
        reorderLevel: initialData.reorderLevel ?? 0,
        expiryDate: initialData.expiryDate || '',
        price: initialData.price ?? '',
        supplier: initialData.supplier || '',
      });
    } else {
      setForm(emptyForm);
    }
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({
      ...form,
      categoryId: Number(form.categoryId),
      quantity: Number(form.quantity),
      reorderLevel: Number(form.reorderLevel),
      price: form.price === '' ? null : Number(form.price),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="card">
      <h3>{initialData ? 'Edit Medicine' : 'Add Medicine'}</h3>
      <div className="form-grid">
        <label>
          Name
          <input name="name" value={form.name} onChange={handleChange} required />
        </label>

        <label>
          Batch Number
          <input name="batchNumber" value={form.batchNumber} onChange={handleChange} />
        </label>

        <label>
          Category
          <select name="categoryId" value={form.categoryId} onChange={handleChange} required>
            <option value="">Select category</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </label>

        <label>
          Supplier
          <input name="supplier" value={form.supplier} onChange={handleChange} />
        </label>

        <label>
          Quantity
          <input type="number" name="quantity" min="0" value={form.quantity} onChange={handleChange} required />
        </label>

        <label>
          Reorder Level
          <input type="number" name="reorderLevel" min="0" value={form.reorderLevel} onChange={handleChange} required />
        </label>

        <label>
          Expiry Date
          <input type="date" name="expiryDate" value={form.expiryDate} onChange={handleChange} required />
        </label>

        <label>
          Price
          <input type="number" step="0.01" name="price" value={form.price} onChange={handleChange} />
        </label>
      </div>

      <div style={{ marginTop: 16, display: 'flex', gap: 10 }}>
        <button type="submit" className="btn btn-primary">
          {initialData ? 'Save Changes' : 'Add Medicine'}
        </button>
        <button type="button" className="btn btn-secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
}
