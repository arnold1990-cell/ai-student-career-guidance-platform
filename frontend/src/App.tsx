import { useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { getAuthState, type AuthState } from './auth/authStorage';
import { Dashboard } from './pages/Dashboard';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ProtectedRoute } from './routes/ProtectedRoute';

function App() {
  const [auth, setAuth] = useState<AuthState | null>(() => getAuthState());

  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Navigate to={auth ? '/dashboard' : '/login'} replace />} />
        <Route path='/login' element={<Login onLogin={setAuth} />} />
        <Route path='/register' element={<Register />} />

        <Route element={<ProtectedRoute auth={auth} />}>
          <Route
            path='/dashboard'
            element={auth ? <Dashboard auth={auth} onLogout={() => setAuth(null)} /> : <Navigate to='/login' replace />}
          />
        </Route>

        <Route path='*' element={<Navigate to='/' replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
