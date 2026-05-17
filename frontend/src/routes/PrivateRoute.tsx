import { Navigate, useLocation } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuth } from '../contexts/AuthContext';
import type { Perfil } from '../types/api';

interface Props {
  children: ReactNode;
  roles?: Perfil[];
}

export default function PrivateRoute({ children, roles }: Props) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center text-slate-500">
        Carregando...
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !roles.includes(user.perfil)) {
    return (
      <div className="p-8 text-center">
        <h2 className="text-xl font-semibold text-red-600">Acesso negado</h2>
        <p className="mt-2 text-slate-600">
          Voce nao tem permissao para acessar esta tela.
        </p>
      </div>
    );
  }

  return <>{children}</>;
}
