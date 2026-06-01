import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);

export const listProducts = async () => {
  if (USE_MOCK) {
    return mockStore.products.list(getToken());
  }
  const response = await api.get('/products');
  return response.data;
};

export const createProduct = async (payload) => {
  if (USE_MOCK) {
    return mockStore.products.create(getToken(), payload);
  }
  const response = await api.post('/products', payload);
  return response.data;
};

export const updateProduct = async (id, payload) => {
  if (USE_MOCK) {
    return mockStore.products.update(getToken(), id, payload);
  }
  const response = await api.put(`/products/${id}`, payload);
  return response.data;
};

export const deleteProduct = async (id) => {
  if (USE_MOCK) {
    return mockStore.products.remove(getToken(), id);
  }
  const response = await api.delete(`/products/${id}`);
  return response.data;
};
