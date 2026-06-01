import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);
const unwrap = (response) => response.data.data;

export const listProducts = async () => {
  if (USE_MOCK) {
    return mockStore.products.list(getToken());
  }
  const response = await api.get('/products');
  return unwrap(response);
};

export const createProduct = async (payload) => {
  if (USE_MOCK) {
    return mockStore.products.create(getToken(), payload);
  }
  const response = await api.post('/products', payload);
  return unwrap(response);
};

export const updateProduct = async (id, payload) => {
  if (USE_MOCK) {
    return mockStore.products.update(getToken(), id, payload);
  }
  const response = await api.put(`/products/${id}`, payload);
  return unwrap(response);
};

export const deleteProduct = async (id) => {
  if (USE_MOCK) {
    return mockStore.products.remove(getToken(), id);
  }
  const response = await api.delete(`/products/${id}`);
  return unwrap(response);
};

export const listLowStockProducts = async (threshold = 25) => {
  if (USE_MOCK) {
    return mockStore.products.lowStock(getToken(), threshold);
  }
  const response = await api.get('/products/low-stock', { params: { threshold } });
  return unwrap(response);
};
