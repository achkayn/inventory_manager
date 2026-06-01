import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);

export const listCategories = async () => {
  if (USE_MOCK) {
    return mockStore.categories.list(getToken());
  }
  const response = await api.get('/categories');
  return response.data;
};

export const createCategory = async (payload) => {
  if (USE_MOCK) {
    return mockStore.categories.create(getToken(), payload);
  }
  const response = await api.post('/categories', payload);
  return response.data;
};

export const updateCategory = async (id, payload) => {
  if (USE_MOCK) {
    return mockStore.categories.update(getToken(), id, payload);
  }
  const response = await api.put(`/categories/${id}`, payload);
  return response.data;
};

export const deleteCategory = async (id) => {
  if (USE_MOCK) {
    return mockStore.categories.remove(getToken(), id);
  }
  const response = await api.delete(`/categories/${id}`);
  return response.data;
};
