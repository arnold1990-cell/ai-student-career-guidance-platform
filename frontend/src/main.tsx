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

type JwtPayload = {
  role?: string;
};

type ApiEnvelope<T> = {
  data: T;
  message: string;
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

const parseJwtPayload = (token: string): JwtPayload | null => {
  const payloadSegment = token.split('.')[1];
  if (!payloadSegment) {
    return null;
  }

  try {
    const normalizedPayload = payloadSegment.replace(/-/g, '+').replace(/_/g, '/');
    const paddedPayload = normalizedPayload.padEnd(Math.ceil(normalizedPayload.length / 4) * 4, '=');
    const decoded = window.atob(paddedPayload);
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
};

const authHeaders = (auth: AuthState) => ({
  Authorization: `Bearer ${auth.accessToken}`,
});

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
  links,
}: {
  children: React.ReactNode;
  auth: AuthState;
  onLogout: () => void;
  links: Array<{ path: string; label: string }>;
}) => (
  <div>
    <header>
      <h1>EduRite</h1>
      <nav>
        {links.map((link) => (
          <Link key={link.path} to={link.path}>
            {link.label}
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

const Message = ({ value }: { value: string | null }) => (value ? <p className='helper-text'>{value}</p> : null);

const ErrorMessage = ({ value }: { value: string | null }) => (value ? <p className='error-text'>{value}</p> : null);

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
      const response = await axios.post<ApiEnvelope<TokenResponse>>(`${API_BASE_URL}/api/v1/auth/login`, {
        email,
        password,
      });

      const tokenPayload = response.data.data;
      if (!tokenPayload?.accessToken || !tokenPayload?.refreshToken) {
        setError('Unexpected login response from server.');
        return;
      }

      const decodedToken = parseJwtPayload(tokenPayload.accessToken);
      const resolvedRole = decodedToken?.role?.toUpperCase();
      if (!resolvedRole) {
        setError('Unexpected login response from server. Missing role information.');
        return;
      }

      const nextAuth: AuthState = {
        accessToken: tokenPayload.accessToken,
        refreshToken: tokenPayload.refreshToken,
        role: resolvedRole,
        email,
      };

      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(nextAuth));
      onLogin(nextAuth);
      navigate(`/${resolvedRole.toLowerCase()}`, { replace: true });
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
          <p className='helper-text'>
            Use seeded accounts such as <strong>student@edutech.local</strong>,{' '}
            <strong>company@edutech.local</strong>, or <strong>admin@edutech.local</strong> with password{' '}
            <strong>password123</strong>.
          </p>
          <ErrorMessage value={error} />
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

const StudentDashboard = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => {
  const [profileStatus, setProfileStatus] = useState<string | null>(null);
  const [docsStatus, setDocsStatus] = useState<string | null>(null);
  const [bursaries, setBursaries] = useState<Array<{ id: number; title: string }>>([]);
  const [notifications, setNotifications] = useState<Array<{ id: number; title: string }>>([]);

  const loadProfile = async () => {
    try {
      const response = await axios.get<ApiEnvelope<{ interests: string; locationPreference: string; bio?: string }>>(
        `${API_BASE_URL}/api/v1/students/profile`,
        { headers: authHeaders(auth) },
      );
      const profile = response.data.data;
      setProfileStatus(`Profile: ${profile.interests} | ${profile.locationPreference}`);
    } catch {
      setProfileStatus('No profile yet. Use Save profile to create one.');
    }
  };

  const saveProfile = async () => {
    try {
      const response = await axios.put<ApiEnvelope<unknown>>(
        `${API_BASE_URL}/api/v1/students/profile`,
        {
          interests: 'Software Engineering',
          locationPreference: 'Remote',
          bio: 'Motivated student looking for opportunities.',
        },
        { headers: authHeaders(auth) },
      );
      setProfileStatus(response.data.message);
    } catch {
      setProfileStatus('Failed to save profile.');
    }
  };

  const listDocuments = async () => {
    try {
      const response = await axios.get<ApiEnvelope<string[]>>(`${API_BASE_URL}/api/v1/students/profile/documents`, {
        headers: authHeaders(auth),
      });
      setDocsStatus(`Documents: ${response.data.data.length}`);
    } catch {
      setDocsStatus('Could not load documents.');
    }
  };

  const searchBursaries = async () => {
    try {
      const response = await axios.get<ApiEnvelope<Array<{ id: number; title: string }>>>(
        `${API_BASE_URL}/api/v1/bursaries/search`,
        { params: { q: '' }, headers: authHeaders(auth) },
      );
      setBursaries(response.data.data);
    } catch {
      setBursaries([]);
    }
  };

  const loadNotifications = async () => {
    try {
      const response = await axios.get<ApiEnvelope<Array<{ id: number; title: string }>>>(
        `${API_BASE_URL}/api/v1/notifications`,
        { headers: authHeaders(auth) },
      );
      setNotifications(response.data.data);
    } catch {
      setNotifications([]);
    }
  };

  const subscribeBasic = async () => {
    try {
      const response = await axios.post<ApiEnvelope<{ plan: string; status: string }>>(
        `${API_BASE_URL}/api/v1/subscriptions`,
        null,
        { params: { planCode: 'BASIC' }, headers: authHeaders(auth) },
      );
      setDocsStatus(`Subscription: ${response.data.data.plan} (${response.data.data.status})`);
    } catch {
      setDocsStatus('Subscription failed.');
    }
  };

  return (
    <Layout
      auth={auth}
      onLogout={onLogout}
      links={[
        { path: '/student', label: 'student' },
        { path: '/student/bursaries', label: 'bursaries' },
        { path: '/student/notifications', label: 'notifications' },
      ]}
    >
      <div className='grid'>
        <Card title='Profile'>
          <button onClick={loadProfile} type='button'>Load profile</button>
          <button onClick={saveProfile} type='button'>Save profile</button>
          <Message value={profileStatus} />
        </Card>
        <Card title='Documents + Subscription'>
          <button onClick={listDocuments} type='button'>Load documents</button>
          <button onClick={subscribeBasic} type='button'>Subscribe BASIC</button>
          <Message value={docsStatus} />
        </Card>
        <Card title='Bursary Search'>
          <button onClick={searchBursaries} type='button'>Search bursaries</button>
          <ul>{bursaries.slice(0, 5).map((item) => <li key={item.id}>{item.title}</li>)}</ul>
        </Card>
        <Card title='Notifications'>
          <button onClick={loadNotifications} type='button'>Load notifications</button>
          <ul>{notifications.slice(0, 5).map((item) => <li key={item.id}>{item.title}</li>)}</ul>
        </Card>
      </div>
    </Layout>
  );
};

const CompanyDashboard = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => {
  const [companyName, setCompanyName] = useState('Future Tech Ltd');
  const [onboardStatus, setOnboardStatus] = useState<string | null>(null);
  const [bursaryStatus, setBursaryStatus] = useState<string | null>(null);

  const onboardCompany = async () => {
    try {
      const response = await axios.post<ApiEnvelope<{ companyName: string; status: string }>>(
        `${API_BASE_URL}/api/v1/companies/onboard`,
        null,
        { params: { companyName }, headers: authHeaders(auth) },
      );
      setOnboardStatus(`${response.data.data.companyName}: ${response.data.data.status}`);
    } catch {
      setOnboardStatus('Onboarding failed.');
    }
  };

  const createBursary = async () => {
    try {
      const response = await axios.post<ApiEnvelope<{ title: string; status: string }>>(
        `${API_BASE_URL}/api/v1/bursaries`,
        null,
        {
          params: {
            title: 'STEM Leadership Bursary',
            description: 'Support for final-year students in STEM fields.',
          },
          headers: authHeaders(auth),
        },
      );
      setBursaryStatus(`${response.data.data.title}: ${response.data.data.status}`);
    } catch {
      setBursaryStatus('Bursary creation failed.');
    }
  };

  return (
    <Layout
      auth={auth}
      onLogout={onLogout}
      links={[
        { path: '/company', label: 'company' },
        { path: '/company/bursaries', label: 'bursary management' },
      ]}
    >
      <div className='grid'>
        <Card title='Profile & Verification'>
          <label>
            Company name
            <input value={companyName} onChange={(event) => setCompanyName(event.target.value)} />
          </label>
          <button onClick={onboardCompany} type='button'>Submit onboarding</button>
          <Message value={onboardStatus} />
        </Card>
        <Card title='Bursary Management'>
          <button onClick={createBursary} type='button'>Create bursary</button>
          <Message value={bursaryStatus} />
        </Card>
      </div>
    </Layout>
  );
};

const AdminDashboard = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => {
  const [analytics, setAnalytics] = useState<string | null>(null);
  const [queueItems, setQueueItems] = useState<Array<{ id: number; status: string }>>([]);

  const loadAnalytics = async () => {
    try {
      const response = await axios.get<ApiEnvelope<{ users: number; bursaries: number }>>(
        `${API_BASE_URL}/api/v1/analytics/dashboard`,
        { headers: authHeaders(auth) },
      );
      setAnalytics(`Users: ${response.data.data.users}, Bursaries: ${response.data.data.bursaries}`);
    } catch {
      setAnalytics('Could not load analytics dashboard.');
    }
  };

  const loadQueue = async () => {
    try {
      const response = await axios.get<ApiEnvelope<Array<{ id: number; status: string }>>>(
        `${API_BASE_URL}/api/v1/companies/verification-queue`,
        { headers: authHeaders(auth) },
      );
      setQueueItems(response.data.data);
    } catch {
      setQueueItems([]);
    }
  };

  return (
    <Layout
      auth={auth}
      onLogout={onLogout}
      links={[
        { path: '/admin', label: 'admin' },
        { path: '/admin/verification', label: 'verification queue' },
      ]}
    >
      <div className='grid'>
        <Card title='Dashboard'>
          <button onClick={loadAnalytics} type='button'>Load analytics</button>
          <Message value={analytics} />
        </Card>
        <Card title='Verification Queue'>
          <button onClick={loadQueue} type='button'>Load pending verifications</button>
          <ul>{queueItems.slice(0, 5).map((item) => <li key={item.id}>#{item.id} - {item.status}</li>)}</ul>
        </Card>
      </div>
    </Layout>
  );
};

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
          path='/student/*'
          element={
            <ProtectedRoute auth={auth} expectedRole='STUDENT'>
              <StudentDashboard auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/company/*'
          element={
            <ProtectedRoute auth={auth} expectedRole='COMPANY'>
              <CompanyDashboard auth={auth as AuthState} onLogout={logout} />
            </ProtectedRoute>
          }
        />
        <Route
          path='/admin/*'
          element={
            <ProtectedRoute auth={auth} expectedRole='ADMIN'>
              <AdminDashboard auth={auth as AuthState} onLogout={logout} />
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
