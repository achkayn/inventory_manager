import api from './client';

const unwrap = (response) => response.data.data;

export const listUsers = async () => unwrap(await api.get('/users'));
export const updateUserRole = async (id, role) => unwrap(await api.patch(`/users/${id}/role`, { role }));
