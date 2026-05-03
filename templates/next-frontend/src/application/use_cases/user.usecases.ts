// templates/next-frontend/src/application/use_cases/user.usecases.ts
import { UserApiPort } from '@/domain/ports/api.ports';
import { User } from '@/domain/entities/user';

export const createUserUseCase = async (api: UserApiPort, payload: any): Promise<User> => {
  return api.createUser(payload);
};
