import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';
import type { MeResponse } from '../types';

export const CompanyPage = () => {
  const [profile, setProfile] = useState<MeResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setError(null);
      try {
        const response = await api.get<{ data: MeResponse }>(endpoints.authMe);
        setProfile(response.data.data);
      } catch (loadError) {
        if (axios.isAxiosError(loadError)) {
          setError(loadError.response?.data?.message ?? 'Could not load company dashboard.');
        } else {
          setError('Could not load company dashboard.');
        }
      }
    };

    void load();
  }, []);

  return (
    <div className='grid'>
      <Card title='Company dashboard'>
        {error && <p className='error-text'>{error}</p>}
        {!error && <p>Welcome {profile?.fullName ?? 'Company user'}.</p>}
        <p>Manage opportunities, discover students, and track engagement.</p>
      </Card>
    </div>
  );
};
