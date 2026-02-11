import { Navigate, Outlet, useLocation } from 'react-router-dom';
import type { AuthState } from '../types';

export const ProtectedRoute = ({
  auth,
  expectedRole,
}: {
  auth: AuthState | null;
  expectedRole?: string;
}) => {
  const location = useLocation();

  if (!auth) {
    return <Navigate to='/login' replace state={{ from: location.pathname }} />;
  }

  if (expectedRole && auth.role !== expectedRole) {
    return <Navigate to={`/${auth.role.toLowerCase()}`} replace />;
  }

  return <Outlet />;
};
