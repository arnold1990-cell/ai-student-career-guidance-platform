import { useMemo, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AUTH_STORAGE_KEY, loadAuthState } from './lib/auth';
import { AdminPage } from './pages/AdminPage';
import { CompanyPage } from './pages/CompanyPage';
import { LoginPage } from './pages/LoginPage';
import { StudentPage } from './pages/StudentPage';
import type { AuthState } from './types';

export const App = () => {
  const [auth, setAuth] = useState<AuthState | null>(() => loadAuthState());

  const logout = () => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    setAuth(null);
  };

  const defaultRoute = useMemo(() => {
    if (!auth) {
      return '/login';
    }
    return `/${auth.role.toLowerCase()}`;
  }, [auth]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Navigate to={defaultRoute} replace />} />
        <Route path='/login' element={<LoginPage auth={auth} onLogin={setAuth} />} />
        <Route
          path='/student'
          element={
            <ProtectedRoute auth={auth} expectedRole='STUDENT'>
              <StudentPage auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/company'
          element={
            <ProtectedRoute auth={auth} expectedRole='COMPANY'>
              <CompanyPage auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/admin'
          element={
            <ProtectedRoute auth={auth} expectedRole='ADMIN'>
              <AdminPage auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route path='*' element={<Navigate to={defaultRoute} replace />} />
      </Routes>
    </BrowserRouter>
  );
};
