import { useEffect, useState, useCallback } from 'react';
import alertService from '../api/alertService';

export default function AlertsWidget() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadAlerts = useCallback(() => {
    setLoading(true);
    alertService
      .getActiveAlerts()
      .then(setAlerts)
      .catch(() => setAlerts([]))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    loadAlerts();
  }, [loadAlerts]);

  const handleResolve = async (id) => {
    await alertService.resolveAlert(id);
    loadAlerts();
  };

  return (
    <div className="card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3>Low-Stock & Expiry Alerts</h3>
        <button className="btn btn-secondary" onClick={loadAlerts}>Refresh</button>
      </div>

      {loading && <p>Loading alerts...</p>}
      {!loading && alerts.length === 0 && <p>No active alerts. Everything looks good.</p>}

      {alerts.map((alert) => (
        <div className="alert-item" key={alert.id}>
          <div>
            <span
              className={`badge ${alert.alertType === 'LOW_STOCK' ? 'badge-low' : 'badge-expiry'}`}
              style={{ marginRight: 8 }}
            >
              {alert.alertType === 'LOW_STOCK' ? 'Low Stock' : 'Near Expiry'}
            </span>
            {alert.message}
          </div>
          <button className="btn btn-primary" onClick={() => handleResolve(alert.id)}>
            Resolve
          </button>
        </div>
      ))}
    </div>
  );
}
