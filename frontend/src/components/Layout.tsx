import { Link, Outlet } from 'react-router-dom';
import type { AuthState } from '../types';

export const Layout = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => {
  const navItems = [
    { path: '/student', label: 'Student' },
    { path: '/company', label: 'Company' },
    { path: '/admin', label: 'Admin' },
  ];

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
