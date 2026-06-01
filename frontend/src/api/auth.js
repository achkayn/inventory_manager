import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY, AUTH_USER_KEY } from '../constants';
import { mockStore } from './mockStore';

const unwrapAuthResponse = (payload) => ({
  token: payload.token,
  user: {
    name: payload.username || payload.name,
    email: payload.email,
    role: payload.role,
  },
});

const persistAuth = ({ token, user }) => {
  localStorage.setItem(AUTH_TOKEN_KEY, token);
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
  return { token, user };
};

export const login = async (payload) => {
  if (USE_MOCK) {
    return persistAuth(await mockStore.auth.login(payload));
  }

  const response = await api.post('/auth/login', payload);
  return persistAuth(unwrapAuthResponse(response.data.data));
};

export const register = async (payload) => {
  if (USE_MOCK) {
    return unwrapAuthResponse(await mockStore.auth.register(payload));
  }

  const response = await api.post('/auth/register', {
    username: payload.username || payload.name,
    email: payload.email,
    password: payload.password,
    role: payload.role,
  });
  return unwrapAuthResponse(response.data.data);
};

export const logout = () => {
  localStorage.removeItem(AUTH_TOKEN_KEY);
  localStorage.removeItem(AUTH_USER_KEY);
};

export const getStoredUser = () => {
  const raw = localStorage.getItem(AUTH_USER_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
};
