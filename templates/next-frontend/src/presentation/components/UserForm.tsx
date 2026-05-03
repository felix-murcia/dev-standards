// templates/next-frontend/src/presentation/components/UserForm.tsx
'use client';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { createUserUseCase } from '@/application/use_cases/user.usecases';
import { userApiAdapter } from '@/infrastructure/api/user.api';

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
  name: z.string().min(2)
});

type FormData = z.infer<typeof schema>;

export function UserForm() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema)
  });

  const mutation = useMutation({
    mutationFn: (data: FormData) => createUserUseCase(userApiAdapter, data),
    onSuccess: () => alert('User created!'),
    onError: (err) => alert(`Error: ${err.message}`)
  });

  const onSubmit = handleSubmit((data) => mutation.mutate(data));

  return (
    <form onSubmit={onSubmit} className="max-w-md mx-auto p-6 space-y-4 bg-white shadow rounded-lg">
      <div>
        <label className="block text-sm font-medium text-gray-700">Email</label>
        <input {...register('email')} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
        {errors.email && <p className="text-red-500 text-xs">{errors.email.message}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Name</label>
        <input {...register('name')} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
        {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">Password</label>
        <input type="password" {...register('password')} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
        {errors.password && <p className="text-red-500 text-xs">{errors.password.message}</p>}
      </div>

      <button 
        type="submit" 
        disabled={isSubmitting}
        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
      >
        {isSubmitting ? 'Creating...' : 'Create User'}
      </button>
    </form>
  );
}
