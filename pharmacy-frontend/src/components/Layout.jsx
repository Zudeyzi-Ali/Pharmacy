import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="app-shell">
      <div className="app-topbar">
        <strong>Pharmacy Drug Inventory System</strong>

        <nav className="app-nav">
          <NavLink to="/dashboard" className={({ isActive }) => (isActive ? 'active' : '')}>
            Dashboard
          </NavLink>
          <NavLink to="/medicines" className={({ isActive }) => (isActive ? 'active' : '')}>
            Medicines
          </NavLink>
          <NavLink to="/suppliers" className={({ isActive }) => (isActive ? 'active' : '')}>
            Suppliers
          </NavLink>
          <NavLink to="/purchase-orders" className={({ isActive }) => (isActive ? 'active' : '')}>
            Purchase Orders
          </NavLink>
          <NavLink to="/prescriptions" className={({ isActive }) => (isActive ? 'active' : '')}>
            Prescriptions
          </NavLink>
          <NavLink to="/sales" className={({ isActive }) => (isActive ? 'active' : '')}>
            Sales
          </NavLink>
          {user?.role === 'ADMIN' && (
            <NavLink to="/reports" className={({ isActive }) => (isActive ? 'active' : '')}>
              Reports
            </NavLink>
          )}
        </nav>

        <div className="app-user">
          <span>{user?.username}</span>
          <span className="dashboard-role-badge">{user?.role}</span>
          <button className="dashboard-logout" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </div>

      <div className="app-content">
        <Outlet />
      </div>
    </div>
  );
}
