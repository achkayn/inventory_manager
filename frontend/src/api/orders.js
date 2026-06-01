import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);

export const listOrders = async () => {
  if (USE_MOCK) {
    return mockStore.orders.list(getToken());
  }
  const response = await api.get('/orders');
  return response.data;
};

export const createOrder = async (payload) => {
  if (USE_MOCK) {
    return mockStore.orders.create(getToken(), payload);
  }
  const response = await api.post('/orders', payload);
  return response.data;
};

export const updateOrderStatus = async (id, status) => {
  if (USE_MOCK) {
    return mockStore.orders.updateStatus(getToken(), id, status);
  }
  const response = await api.patch(`/orders/${id}/status`, { status });
  return response.data;
};

export const deleteOrder = async (id) => {
  if (USE_MOCK) {
    return mockStore.orders.remove(getToken(), id);
  }
  const response = await api.delete(`/orders/${id}`);
  return response.data;
};
