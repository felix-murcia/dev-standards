// templates/next-frontend/src/infrastructure/api/user.api.ts
import { UserApiPort } from '@/domain/ports/api.ports';
import { apiClient } from './client';
import { User } from '@/domain/entities/user';

export const userApiAdapter: UserApiPort = {
  createUser: async (payload) => {
    const { data } = await apiClient.post<User>('/users', payload);
    return data;
  },
  getUser: async (id) => {
    const { data } = await apiClient.get<User>(`/users/${id}`);
    return data;
  }
};
