import { createContext, useContext, useState, useEffect } from 'react';
import apiClient from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // On first load, restore the session from localStorage if we have one.
  useEffect(() => {
    const token = localStorage.getItem('pharmacy_token');
    const storedUser = localStorage.getItem('pharmacy_user');
    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  async function login(username, password) {
    const response = await apiClient.post('/auth/login', { username, password });
    const { token, username: returnedUsername, role } = response.data.data;

    const loggedInUser = { username: returnedUsername, role };
    localStorage.setItem('pharmacy_token', token);
    localStorage.setItem('pharmacy_user', JSON.stringify(loggedInUser));
    setUser(loggedInUser);
    return loggedInUser;
  }

  async function register(username, email, password, role) {
    const response = await apiClient.post('/auth/register', { username, email, password, role });
    const { token, username: returnedUsername, role: returnedRole } = response.data.data;

    const registeredUser = { username: returnedUsername, role: returnedRole };
    localStorage.setItem('pharmacy_token', token);
    localStorage.setItem('pharmacy_user', JSON.stringify(registeredUser));
    setUser(registeredUser);
    return registeredUser;
  }

  function logout() {
    localStorage.removeItem('pharmacy_token');
    localStorage.removeItem('pharmacy_user');
    setUser(null);
  }

  const value = {
    user,
    isAuthenticated: !!user,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
