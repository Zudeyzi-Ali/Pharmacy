import { useAuth } from '../context/AuthContext';
import AlertsWidget from '../components/AlertsWidget';

export default function Dashboard() {
  const { user } = useAuth();

  return (
    <div className="module-page">
      <h1>Welcome, {user?.username}</h1>
      <p className="muted">
        Use the navigation above to manage medicines, suppliers, purchase orders,
        prescriptions, sales, and reports.
      </p>
      <AlertsWidget />
    </div>
  );
}
