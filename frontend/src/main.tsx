import React, { useMemo, useState } from 'react';
import ReactDOM from 'react-dom/client';
import axios from 'axios';
import {
  BrowserRouter,
  Link,
  Navigate,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from 'react-router-dom';
import './styles.css';

type TokenResponse = {
  accessToken: string;
  refreshToken: string;
};

type AuthState = {
  accessToken: string;
  refreshToken: string;
  role: string;
  email: string;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const AUTH_STORAGE_KEY = 'edurite-auth';

const loadAuthState = (): AuthState | null => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as AuthState;
    if (!parsed.accessToken || !parsed.refreshToken || !parsed.role) {
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
};

const Card = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <div className='card'>
    <h3>{title}</h3>
    {children}
  </div>
);

const Layout = ({
  children,
  auth,
  onLogout,
}: {
  children: React.ReactNode;
  auth: AuthState;
  onLogout: () => void;
}) => (
  <div>
    <header>
      <h1>EduRite</h1>
      <nav>
        {['/student', '/company', '/admin'].map((path) => (
          <Link key={path} to={path}>
            {path.slice(1)}
          </Link>
        ))}
        <button className='link-button' onClick={onLogout} type='button'>
          Logout ({auth.email})
        </button>
      </nav>
    </header>
    <main>{children}</main>
  </div>
);

const Login = ({
  auth,
  onLogin,
}: {
  auth: AuthState | null;
  onLogin: (state: AuthState) => void;
}) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('student@edutech.local');
  const [password, setPassword] = useState('password123');
  const [role, setRole] = useState<'STUDENT' | 'COMPANY' | 'ADMIN'>('STUDENT');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (auth) {
    return <Navigate to={`/${auth.role.toLowerCase()}`} replace />;
  }

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const response = await axios.post<{ data: TokenResponse }>(`${API_BASE_URL}/api/v1/auth/login`, {
        email,
        password,
      });

      const tokenPayload = response.data.data;
      if (!tokenPayload?.accessToken || !tokenPayload?.refreshToken) {
        setError('Unexpected login response from server.');
        return;
      }

      const nextAuth: AuthState = {
        accessToken: tokenPayload.accessToken,
        refreshToken: tokenPayload.refreshToken,
        role,
        email,
      };

      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(nextAuth));
      onLogin(nextAuth);
      navigate(`/${role.toLowerCase()}`, { replace: true });
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message ?? 'Login failed. Please check your credentials.');
      } else {
        setError('Login failed due to an unknown error.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className='auth-shell'>
      <Card title='Login'>
        <p>Sign in to open your dashboard pages.</p>
        <form className='auth-form' onSubmit={submit}>
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type='email' required />
          </label>
          <label>
            Password
            <input
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              type='password'
              required
            />
          </label>
          <label>
            Dashboard Role
            <select value={role} onChange={(event) => setRole(event.target.value as 'STUDENT' | 'COMPANY' | 'ADMIN')}>
              <option value='STUDENT'>Student</option>
              <option value='COMPANY'>Company</option>
              <option value='ADMIN'>Admin</option>
            </select>
          </label>
          {error && <p className='error-text'>{error}</p>}
          <button disabled={submitting} type='submit'>
            {submitting ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </Card>
    </main>
  );
};

const ProtectedRoute = ({
  auth,
  expectedRole,
  children,
}: {
  auth: AuthState | null;
  expectedRole: string;
  children: React.ReactNode;
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

const Student = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Application progress, saved opportunities, analytics.</Card>
      <Card title='Profile'>Personal details, qualifications, experience, CV/transcript uploads.</Card>
      <Card title='Careers & AI'>Search careers and get AI recommendations.</Card>
      <Card title='Bursaries'>Browse, bookmark, apply, and view saved list.</Card>
      <Card title='Subscription'>Choose BASIC/PREMIUM and checkout flow.</Card>
      <Card title='Notifications'>In-app alert center.</Card>
    </div>
  </Layout>
);

const Company = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Status, applicants, views, completion rate.</Card>
      <Card title='Profile & Verification'>Business details and verification status.</Card>
      <Card title='Bursary Management'>Create/edit/submit bursaries for approval.</Card>
      <Card title='Talent Search + Shortlist'>Filter students and shortlist candidates.</Card>
      <Card title='Messaging'>Message shortlisted students securely.</Card>
    </div>
  </Layout>
);

const Admin = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Analytics tiles and pending queues.</Card>
      <Card title='Users + Bulk Upload'>Manage users and import CSV.</Card>
      <Card title='Verification + Approval'>Approve/reject companies and bursaries.</Card>
      <Card title='Subscriptions + Payments'>Track status and transaction oversight.</Card>
      <Card title='Templates + Audit Logs'>Manage templates and review audit logs.</Card>
    </div>
  </Layout>
);

const App = () => {
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
        <Route path='/login' element={<Login auth={auth} onLogin={setAuth} />} />
        <Route
          path='/student'
          element={
            <ProtectedRoute auth={auth} expectedRole='STUDENT'>
              <Student auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/company'
          element={
            <ProtectedRoute auth={auth} expectedRole='COMPANY'>
              <Company auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/admin'
          element={
            <ProtectedRoute auth={auth} expectedRole='ADMIN'>
              <Admin auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route path='*' element={<Navigate to={defaultRoute} replace />} />
      </Routes>
    </BrowserRouter>
  );
};

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
