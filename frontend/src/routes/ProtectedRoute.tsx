import { Navigate, Outlet } from 'react-router-dom';
import type { AuthState, Role } from '../auth/authStorage';

interface Props {
  auth: AuthState | null;
  allowedRole?: Role;
}

export const ProtectedRoute = ({ auth, allowedRole }: Props) => {
  if (!auth) {
    return <Navigate to='/login' replace />;
  }

  if (allowedRole && auth.user.role !== allowedRole) {
    return <Navigate to='/dashboard' replace />;
  }

  return <Outlet />;
};
