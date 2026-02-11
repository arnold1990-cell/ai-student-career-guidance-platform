import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';

type StudentProfile = {
  firstName?: string;
  lastName?: string;
  qualification?: string;
  bio?: string;
};

type NotificationItem = {
  id: string | number;
  title?: string;
  body?: string;
};

export const StudentPage = () => {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setError(null);
      try {
        const [profileResponse, notificationResponse] = await Promise.all([
          api.get<{ data: StudentProfile }>(endpoints.studentProfile),
          api.get<{ data: NotificationItem[] }>(endpoints.notifications),
        ]);
        setProfile(profileResponse.data.data);
        setNotifications(notificationResponse.data.data ?? []);
      } catch (loadError) {
        if (axios.isAxiosError(loadError)) {
          setError(loadError.response?.data?.message ?? 'Could not load student dashboard data.');
        } else {
          setError('Could not load student dashboard data.');
        }
      }
    };

    void load();
  }, []);

  return (
    <div className='grid'>
      <Card title='Student dashboard'>
        {error && <p className='error-text'>{error}</p>}
        {!error && (
          <>
            <p>
              Welcome {profile?.firstName ?? 'Student'} {profile?.lastName ?? ''}
            </p>
            <p>{profile?.qualification ?? 'No qualification set yet.'}</p>
          </>
        )}
      </Card>

      <Card title='Notifications'>
        {notifications.length === 0 && <p>No notifications yet.</p>}
        <ul>
          {notifications.map((notification) => (
            <li key={notification.id}>{notification.title ?? notification.body ?? 'Notification'}</li>
          ))}
        </ul>
      </Card>
    </div>
  );
};
