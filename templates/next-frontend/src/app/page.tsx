// templates/next-frontend/src/app/page.tsx
import { UserForm } from "@/presentation/components/UserForm";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <h1 className="text-4xl font-bold mb-8">Next.js Clean Architecture</h1>
      <UserForm />
    </main>
  );
}
