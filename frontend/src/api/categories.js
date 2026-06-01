import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);
const unwrap = (response) => response.data.data;

export const listCategories = async () => {
  if (USE_MOCK) {
    return mockStore.categories.list(getToken());
  }
  const response = await api.get('/categories');
  return unwrap(response);
};

export const createCategory = async (payload) => {
  if (USE_MOCK) {
    return mockStore.categories.create(getToken(), payload);
  }
  const response = await api.post('/categories', payload);
  return unwrap(response);
};

export const updateCategory = async (id, payload) => {
  if (USE_MOCK) {
    return mockStore.categories.update(getToken(), id, payload);
  }
  const response = await api.put(`/categories/${id}`, payload);
  return unwrap(response);
};

export const deleteCategory = async (id) => {
  if (USE_MOCK) {
    return mockStore.categories.remove(getToken(), id);
  }
  const response = await api.delete(`/categories/${id}`);
  return unwrap(response);
};
