import { createContext, useContext, useEffect, useState } from 'react';
import { api } from '../api/client.js';

const AuthContext = createContext(null);

function loadUser() {
  try {
    return JSON.parse(localStorage.getItem('user'));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUser);
  const [sessionExpired, setSessionExpired] = useState(false);

  useEffect(() => {
    const onExpired = () => {
      setUser(null);
      setSessionExpired(true);
    };
    window.addEventListener('auth-expired', onExpired);
    // Check a saved login on startup; an invalid token triggers 'auth-expired' (see api/client.js).
    if (localStorage.getItem('token')) {
      api.me().catch(() => {});
    }
    return () => window.removeEventListener('auth-expired', onExpired);
  }, []);

  const saveSession = ({ token, user }) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    setUser(user);
    setSessionExpired(false);
    return user;
  };

  const login = async (email, password) => saveSession(await api.login(email, password));
  const register = async (payload) => saveSession(await api.register(payload));

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, sessionExpired, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
