import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import type { AuthState } from '../types';

export const ProtectedRoute = ({
  auth,
  expectedRole,
  children,
}: {
  auth: AuthState | null;
  expectedRole: string;
  children: ReactNode;
}) => {
  const location = useLocation();
  if (!auth) {
    return <Navigate to='/login' replace state={{ from: location.pathname }} />;
  }
  if (auth.role !== expectedRole) {
    return <Navigate to={`/${auth.role.toLowerCase()}`} replace />;
  }
  return <>{children}</>;
};
