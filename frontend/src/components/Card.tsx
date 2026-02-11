import type { ReactNode } from 'react';

export const Card = ({ title, children }: { title: string; children: ReactNode }) => (
  <div className='card'>
    <h3>{title}</h3>
    {children}
  </div>
);
