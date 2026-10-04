import { useEffect, useState } from 'react';
import apiClient from '../api/client';

const emptyItem = () => ({ medicineId: '', quantity: 1, dosageInstructions: '' });

export default function Prescriptions() {
  const [prescriptions, setPrescriptions] = useState([]);
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const [patientName, setPatientName] = useState('');
  const [doctorName, setDoctorName] = useState('');
  const [dateIssued, setDateIssued] = useState(() => new Date().toISOString().slice(0, 10));
  const [items, setItems] = useState([emptyItem()]);

  async function loadPrescriptions() {
    setLoading(true);
    try {
      const res = await apiClient.get('/prescriptions');
      setPrescriptions(res.data.data);
    } catch {
      setError('Could not load prescriptions.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPrescriptions();
    apiClient
      .get('/medicines')
      .then((res) => setMedicines(res.data.data))
      .catch(() => setError('Could not load the medicine list.'));
  }, []);

  function updateItem(index, field, value) {
    setItems((prev) => prev.map((item, i) => (i === index ? { ...item, [field]: value } : item)));
  }

  function addItemRow() {
    setItems((prev) => [...prev, emptyItem()]);
  }

  function removeItemRow(index) {
    setItems((prev) => prev.filter((_, i) => i !== index));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await apiClient.post('/prescriptions', {
        patientName,
        doctorName,
        dateIssued,
        items: items.map((i) => ({
          medicineId: Number(i.medicineId),
          quantity: Number(i.quantity),
          dosageInstructions: i.dosageInstructions,
        })),
      });
      setPatientName('');
      setDoctorName('');
      setItems([emptyItem()]);
      await loadPrescriptions();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save the prescription.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="module-page">
      <h1>Prescriptions</h1>

      <form className="card-form" onSubmit={handleSubmit}>
        <h2>New prescription</h2>

        {error && <div className="auth-error">{error}</div>}

        <div className="form-row">
          <div className="form-field">
            <label>Patient name</label>
            <input value={patientName} onChange={(e) => setPatientName(e.target.value)} required />
          </div>
          <div className="form-field">
            <label>Doctor name</label>
            <input value={doctorName} onChange={(e) => setDoctorName(e.target.value)} required />
          </div>
          <div className="form-field">
            <label>Date issued</label>
            <input type="date" value={dateIssued} onChange={(e) => setDateIssued(e.target.value)} required />
          </div>
        </div>

        <h3>Medicines</h3>
        {items.map((item, index) => (
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
                  {m.name}
                </option>
              ))}
            </select>
            <input
              type="number"
              min="1"
              placeholder="Qty"
              value={item.quantity}
              onChange={(e) => updateItem(index, 'quantity', e.target.value)}
              required
            />
            <input
              placeholder="Dosage instructions"
              value={item.dosageInstructions}
              onChange={(e) => updateItem(index, 'dosageInstructions', e.target.value)}
            />
            {items.length > 1 && (
              <button type="button" className="item-remove" onClick={() => removeItemRow(index)}>
                &times;
              </button>
            )}
          </div>
        ))}
        <button type="button" className="add-row-btn" onClick={addItemRow}>
          + Add medicine
        </button>

        <button type="submit" className="auth-submit" disabled={submitting} style={{ marginTop: 20 }}>
          {submitting ? 'Saving…' : 'Save prescription'}
        </button>
      </form>

      <h2 style={{ marginTop: 40 }}>All prescriptions</h2>
      {loading ? (
        <p className="muted">Loading…</p>
      ) : prescriptions.length === 0 ? (
        <p className="muted">No prescriptions recorded yet.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Patient</th>
              <th>Doctor</th>
              <th>Date issued</th>
              <th>Items</th>
              <th>Recorded by</th>
            </tr>
          </thead>
          <tbody>
            {prescriptions.map((p) => (
              <tr key={p.id}>
                <td>{p.patientName}</td>
                <td>{p.doctorName}</td>
                <td>{p.dateIssued}</td>
                <td>{p.items.map((i) => i.medicineName).join(', ')}</td>
                <td>{p.pharmacistUsername}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
