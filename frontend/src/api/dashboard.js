import api from './client';
import { USE_MOCK, AUTH_TOKEN_KEY } from '../constants';
import { mockStore } from './mockStore';

const getToken = () => localStorage.getItem(AUTH_TOKEN_KEY);
const unwrap = (response) => response.data.data;

export const getDashboardSummary = async () => {
  if (USE_MOCK) {
    return mockStore.dashboard.summary(getToken());
  }
  const response = await api.get('/dashboard/summary');
  return unwrap(response);
};

export const getCategoryStock = async () => {
  if (USE_MOCK) {
    return mockStore.dashboard.categoryStock(getToken());
  }
  const response = await api.get('/dashboard/category-stock');
  return unwrap(response);
};
