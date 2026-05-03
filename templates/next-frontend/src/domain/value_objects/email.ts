// templates/next-frontend/src/domain/value_objects/email.ts
import { z } from 'zod';

export const EmailSchema = z.string().email("Invalid email format").transform(val => val.toLowerCase().trim());
export type Email = z.infer<typeof EmailSchema>;
