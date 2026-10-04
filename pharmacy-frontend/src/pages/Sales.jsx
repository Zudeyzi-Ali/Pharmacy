import { useEffect, useState } from 'react';
import apiClient from '../api/client';

const emptyItem = () => ({ medicineId: '', quantity: 1 });

export default function Sales() {
  const [medicines, setMedicines] = useState([]);
  const [items, setItems] = useState([emptyItem()]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [lastSale, setLastSale] = useState(null);

  useEffect(() => {
    apiClient
      .get('/medicines')
      .then((res) => setMedicines(res.data.data))
      .catch(() => setError('Could not load the medicine list.'));
  }, []);

  function medicineById(id) {
    return medicines.find((m) => String(m.id) === String(id));
  }

  function updateItem(index, field, value) {
    setItems((prev) => prev.map((item, i) => (i === index ? { ...item, [field]: value } : item)));
  }

  function addItemRow() {
    setItems((prev) => [...prev, emptyItem()]);
  }

  function removeItemRow(index) {
    setItems((prev) => prev.filter((_, i) => i !== index));
  }

  const runningTotal = items.reduce((sum, item) => {
    const medicine = medicineById(item.medicineId);
    const qty = Number(item.quantity) || 0;
    const price = medicine ? Number(medicine.price) || 0 : 0;
    return sum + qty * price;
  }, 0);

  async function handleCheckout(e) {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    setLastSale(null);
    try {
      const res = await apiClient.post('/sales', {
        items: items.map((i) => ({
          medicineId: Number(i.medicineId),
          quantity: Number(i.quantity),
        })),
      });
      setLastSale(res.data.data);
      setItems([emptyItem()]);
    } catch (err) {
      setError(err.response?.data?.message || 'Could not complete the sale.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="module-page">
      <h1>Sales</h1>

      {lastSale && (
        <div className="sale-confirmation">
          Sale recorded — total <strong>${lastSale.totalAmount}</strong> ({lastSale.items.length} item(s))
        </div>
      )}

      <form className="card-form" onSubmit={handleCheckout}>
        <h2>New sale</h2>

        {error && <div className="auth-error">{error}</div>}

        {items.map((item, index) => {
          const medicine = medicineById(item.medicineId);
          return (
            <div className="item-row" key={index}>
              <select
                value={item.medicineId}
                onChange={(e) => updateItem(index, 'medicineId', e.target.value)}
                required
              >
                <option value="" disabled>
                  Select medicine
                </option>
                {medicines.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.name} (in stock: {m.quantity})
                  </option>
                ))}
              </select>
              <input
                type="number"
                min="1"
                max={medicine?.quantity ?? undefined}
                placeholder="Qty"
                value={item.quantity}
                onChange={(e) => updateItem(index, 'quantity', e.target.value)}
                required
              />
              <span className="item-unit-price">
                {medicine ? `$${Number(medicine.price).toFixed(2)} each` : '—'}
              </span>
              {items.length > 1 && (
                <button type="button" className="item-remove" onClick={() => removeItemRow(index)}>
                  &times;
                </button>
              )}
            </div>
          );
        })}
        <button type="button" className="add-row-btn" onClick={addItemRow}>
          + Add item
        </button>

        <div className="sale-total">
          Total: <strong>${runningTotal.toFixed(2)}</strong>
        </div>

        <button type="submit" className="auth-submit" disabled={submitting}>
          {submitting ? 'Processing…' : 'Complete sale'}
        </button>
      </form>
    </div>
  );
}
