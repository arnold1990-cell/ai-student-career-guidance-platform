import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import type { AuthState } from '../types';

export const Layout = ({
  children,
  auth,
  onLogout,
}: {
  children: ReactNode;
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
