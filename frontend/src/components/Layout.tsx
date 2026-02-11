import { Link, Outlet } from 'react-router-dom';
import type { AuthState } from '../types';

export const Layout = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => {
  const navItems = [
    { path: '/student', label: 'Student', role: 'STUDENT' },
    { path: '/company', label: 'Company', role: 'COMPANY' },
    { path: '/admin', label: 'Admin', role: 'ADMIN' },
  ].filter((item) => item.role === auth.role);

  return (
    <div>
      <header>
        <h1>EduRite</h1>
        <nav>
          {navItems.map((item) => (
            <Link key={item.path} to={item.path}>
              {item.label}
            </Link>
          ))}
          <button className='link-button' onClick={onLogout} type='button'>
            Logout ({auth.email})
          </button>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
