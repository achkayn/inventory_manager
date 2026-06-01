import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);
const unwrap = (response) => response.data.data;

export const listSuppliers = async () => {
  if (USE_MOCK) {
    return mockStore.suppliers.list(getToken());
  }
  const response = await api.get('/suppliers');
  return unwrap(response);
};

export const createSupplier = async (payload) => {
  if (USE_MOCK) {
    return mockStore.suppliers.create(getToken(), payload);
  }
  const response = await api.post('/suppliers', payload);
  return unwrap(response);
};

export const updateSupplier = async (id, payload) => {
  if (USE_MOCK) {
    return mockStore.suppliers.update(getToken(), id, payload);
  }
  const response = await api.put(`/suppliers/${id}`, payload);
  return unwrap(response);
};

export const deleteSupplier = async (id) => {
  if (USE_MOCK) {
    return mockStore.suppliers.remove(getToken(), id);
  }
  const response = await api.delete(`/suppliers/${id}`);
  return unwrap(response);
};
