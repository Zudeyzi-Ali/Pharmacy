import { useState } from 'react';
import apiClient from '../api/client';

const monthNames = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

export default function Reports() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function loadReport(e) {
    e?.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await apiClient.get('/sales/reports/monthly', { params: { year, month } });
      setReport(res.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Could not load the report.');
      setReport(null);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="module-page">
      <h1>Monthly Sales Report</h1>

      <form className="card-form report-filter" onSubmit={loadReport}>
        <div className="form-row">
          <div className="form-field">
            <label>Year</label>
            <input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))} />
          </div>
          <div className="form-field">
            <label>Month</label>
            <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
              {monthNames.map((name, i) => (
                <option key={name} value={i + 1}>
                  {name}
                </option>
              ))}
            </select>
          </div>
          <button type="submit" className="auth-submit" style={{ marginTop: 20 }} disabled={loading}>
            {loading ? 'Loading…' : 'Run report'}
          </button>
        </div>
        {error && <div className="auth-error">{error}</div>}
      </form>

      {report && (
        <div className="report-results">
          <div className="report-stats">
            <div className="stat-card">
              <span className="stat-label">Total sales</span>
              <span className="stat-value">{report.totalSalesCount}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label">Total revenue</span>
              <span className="stat-value">${report.totalRevenue}</span>
            </div>
          </div>

          <h2>Top-selling medicines</h2>
          {report.topSellingMedicines.length === 0 ? (
            <p className="muted">No sales recorded for this month.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Medicine</th>
                  <th>Quantity sold</th>
                </tr>
              </thead>
              <tbody>
                {report.topSellingMedicines.map((m) => (
                  <tr key={m.medicineId}>
                    <td>{m.medicineName}</td>
                    <td>{m.totalQuantitySold}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
}
