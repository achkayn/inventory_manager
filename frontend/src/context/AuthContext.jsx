import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { getStoredUser, login as apiLogin, logout as apiLogout, register as apiRegister } from '../api/auth';
import { AUTH_TOKEN_KEY, AUTH_USER_KEY } from '../constants';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem(AUTH_TOKEN_KEY));
  const [user, setUser] = useState(() => getStoredUser());
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const handleUnauthorized = () => {
      setToken(null);
      setUser(null);
    };

    const handleStorage = () => {
      setToken(localStorage.getItem(AUTH_TOKEN_KEY));
      setUser(getStoredUser());
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    window.addEventListener('storage', handleStorage);
    setReady(true);

    return () => {
      window.removeEventListener('auth:unauthorized', handleUnauthorized);
      window.removeEventListener('storage', handleStorage);
    };
  }, []);

  const login = async (payload) => {
    const result = await apiLogin(payload);
    setToken(result.token);
    setUser(result.user);
    return result;
  };

  const register = async (payload) => {
    return apiRegister(payload);
  };

  const logout = () => {
    apiLogout();
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({
      token,
      user,
      ready,
      isAuthenticated: Boolean(token),
      login,
      register,
      logout,
    }),
    [token, user, ready],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
