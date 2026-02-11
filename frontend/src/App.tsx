import { useMemo, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { clearToken, getUserFromToken } from './lib/auth';
import { AdminPage } from './pages/AdminPage';
import { CompanyPage } from './pages/CompanyPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { StudentPage } from './pages/StudentPage';
import type { AuthState } from './types';

export const App = () => {
  const [auth, setAuth] = useState<AuthState | null>(() => getUserFromToken());

  const logout = () => {
    clearToken();
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
        <Route path='/register' element={<RegisterPage auth={auth} />} />

        <Route element={<ProtectedRoute auth={auth} />}>
          <Route path='/' element={auth ? <Layout auth={auth} onLogout={logout} /> : <Navigate to='/login' replace />}>
            <Route element={<ProtectedRoute auth={auth} expectedRole='STUDENT' />}>
              <Route path='student' element={<StudentPage />} />
            </Route>
            <Route element={<ProtectedRoute auth={auth} expectedRole='COMPANY' />}>
              <Route path='company' element={<CompanyPage />} />
            </Route>
            <Route element={<ProtectedRoute auth={auth} expectedRole='ADMIN' />}>
              <Route path='admin' element={<AdminPage />} />
            </Route>
          </Route>
        </Route>

        <Route path='*' element={<Navigate to={defaultRoute} replace />} />
      </Routes>
    </BrowserRouter>
  );
};
