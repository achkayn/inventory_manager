import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);
const unwrap = (response) => response.data.data;

export const listOrders = async (status) => {
  if (USE_MOCK) {
    return mockStore.orders.list(getToken(), status);
  }
  const response = await api.get('/orders', { params: status ? { status } : {} });
  return unwrap(response);
};

export const createOrder = async (payload) => {
  if (USE_MOCK) {
    return mockStore.orders.create(getToken(), payload);
  }
  const response = await api.post('/orders', payload);
  return unwrap(response);
};

export const updateOrderStatus = async (id, status) => {
  if (USE_MOCK) {
    return mockStore.orders.updateStatus(getToken(), id, status);
  }
  const response = await api.patch(`/orders/${id}/status`, { status });
  return unwrap(response);
};

export const deleteOrder = async (id) => {
  if (USE_MOCK) {
    return mockStore.orders.remove(getToken(), id);
  }
  const response = await api.delete(`/orders/${id}`);
  return unwrap(response);
};
