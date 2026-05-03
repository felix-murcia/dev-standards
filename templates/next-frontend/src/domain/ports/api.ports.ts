// templates/next-frontend/src/domain/ports/api.ports.ts
import { User } from '../entities/user';

export interface UserApiPort {
  createUser(payload: { email: string; password: string; name: string }): Promise<User>;
  getUser(id: string): Promise<User>;
}
