import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';

type StudentProfile = {
  firstName: string;
  lastName: string;
  phone: string;
  qualification: string;
  bio: string;
};

type NotificationItem = {
  id: string | number;
  title?: string;
  message: string;
  createdAt?: string;
};

type Bursary = {
  id: string | number;
  title: string;
  provider?: string;
  deadline?: string;
};

const defaultProfile: StudentProfile = {
  firstName: '',
  lastName: '',
  phone: '',
  qualification: '',
  bio: '',
};

export const StudentPage = () => {
  const [profile, setProfile] = useState<StudentProfile>(defaultProfile);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileSaving, setProfileSaving] = useState(false);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [profileSuccess, setProfileSuccess] = useState<string | null>(null);

  const [documentFile, setDocumentFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [uploadSuccess, setUploadSuccess] = useState<string | null>(null);

  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [notificationsLoading, setNotificationsLoading] = useState(false);
  const [notificationsError, setNotificationsError] = useState<string | null>(null);

  const [bursaryQuery, setBursaryQuery] = useState('');
  const [bursaryResults, setBursaryResults] = useState<Bursary[]>([]);
  const [bursaryLoading, setBursaryLoading] = useState(false);
  const [bursaryError, setBursaryError] = useState<string | null>(null);

  const normalizeError = (error: unknown, fallback: string): string => {
    if (axios.isAxiosError(error)) {
      return error.response?.data?.message ?? fallback;
    }

    return fallback;
  };

  const loadProfile = async () => {
    setProfileLoading(true);
    setProfileError(null);

    try {
      const response = await api.get<StudentProfile>(endpoints.studentProfile);
      setProfile({ ...defaultProfile, ...response.data });
    } catch (error) {
      setProfileError(normalizeError(error, 'Could not load your profile.'));
    } finally {
      setProfileLoading(false);
    }
  };

  const saveProfile = async (event: React.FormEvent) => {
    event.preventDefault();
    setProfileSaving(true);
    setProfileError(null);
    setProfileSuccess(null);

    try {
      await api.put(endpoints.studentProfile, profile);
      setProfileSuccess('Profile saved successfully.');
    } catch (error) {
      setProfileError(normalizeError(error, 'Could not save your profile.'));
    } finally {
      setProfileSaving(false);
    }
  };

  const uploadDocument = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!documentFile) {
      setUploadError('Please choose a file before uploading.');
      return;
    }

    const formData = new FormData();
    formData.append('file', documentFile);

    setUploading(true);
    setUploadProgress(0);
    setUploadError(null);
    setUploadSuccess(null);

    try {
      await api.post(endpoints.studentDocumentUpload, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          const total = progressEvent.total ?? 0;
          if (total > 0) {
            setUploadProgress(Math.round((progressEvent.loaded * 100) / total));
          }
        },
      });

      setUploadSuccess('Document uploaded successfully.');
      setDocumentFile(null);
    } catch (error) {
      setUploadError(normalizeError(error, 'Document upload failed.'));
    } finally {
      setUploading(false);
    }
  };

  const loadNotifications = async () => {
    setNotificationsLoading(true);
    setNotificationsError(null);

    try {
      const response = await api.get<NotificationItem[] | { data: NotificationItem[] }>(endpoints.notifications);
      const payload = Array.isArray(response.data) ? response.data : response.data.data;
      setNotifications(payload ?? []);
    } catch (error) {
      setNotificationsError(normalizeError(error, 'Could not load notifications.'));
    } finally {
      setNotificationsLoading(false);
    }
  };

  useEffect(() => {
    void loadProfile();
    void loadNotifications();
  }, []);

  useEffect(() => {
    const timeoutId = window.setTimeout(async () => {
      if (!bursaryQuery.trim()) {
        setBursaryResults([]);
        setBursaryError(null);
        return;
      }

      setBursaryLoading(true);
      setBursaryError(null);

      try {
        const response = await api.get<Bursary[] | { data: Bursary[] }>(endpoints.bursaries, {
          params: { query: bursaryQuery.trim() },
        });

        const payload = Array.isArray(response.data) ? response.data : response.data.data;
        setBursaryResults(payload ?? []);
      } catch (error) {
        setBursaryError(normalizeError(error, 'Bursary search failed.'));
      } finally {
        setBursaryLoading(false);
      }
    }, 400);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [bursaryQuery]);

  return (
    <div className='grid'>
      <Card title='Profile'>
        {profileLoading && <p>Loading profile...</p>}
        <form className='auth-form' onSubmit={saveProfile}>
          <label>
            First name
            <input
              value={profile.firstName}
              onChange={(event) => setProfile((prev) => ({ ...prev, firstName: event.target.value }))}
              type='text'
              disabled={profileSaving}
            />
          </label>
          <label>
            Last name
            <input
              value={profile.lastName}
              onChange={(event) => setProfile((prev) => ({ ...prev, lastName: event.target.value }))}
              type='text'
              disabled={profileSaving}
            />
          </label>
          <label>
            Phone
            <input
              value={profile.phone}
              onChange={(event) => setProfile((prev) => ({ ...prev, phone: event.target.value }))}
              type='text'
              disabled={profileSaving}
            />
          </label>
          <label>
            Qualification
            <input
              value={profile.qualification}
              onChange={(event) => setProfile((prev) => ({ ...prev, qualification: event.target.value }))}
              type='text'
              disabled={profileSaving}
            />
          </label>
          <label>
            Bio
            <textarea
              value={profile.bio}
              onChange={(event) => setProfile((prev) => ({ ...prev, bio: event.target.value }))}
              disabled={profileSaving}
            />
          </label>
          {profileError && <p className='error-text'>{profileError}</p>}
          {profileSuccess && <p>{profileSuccess}</p>}
          <button type='submit' disabled={profileSaving || profileLoading}>
            {profileSaving ? 'Saving profile...' : 'Save profile'}
          </button>
        </form>
      </Card>

      <Card title='Document upload'>
        <form className='auth-form' onSubmit={uploadDocument}>
          <label>
            Upload CV/Transcript
            <input
              type='file'
              onChange={(event) => setDocumentFile(event.target.files?.[0] ?? null)}
              disabled={uploading}
            />
          </label>
          {uploading && <p>Uploading... {uploadProgress}%</p>}
          {uploadError && <p className='error-text'>{uploadError}</p>}
          {uploadSuccess && <p>{uploadSuccess}</p>}
          <button type='submit' disabled={uploading}>
            {uploading ? 'Uploading...' : 'Upload document'}
          </button>
        </form>
      </Card>

      <Card title='Notifications'>
        <button type='button' onClick={() => void loadNotifications()} disabled={notificationsLoading}>
          {notificationsLoading ? 'Refreshing...' : 'Refresh notifications'}
        </button>
        {notificationsError && <p className='error-text'>{notificationsError}</p>}
        {!notificationsLoading && notifications.length === 0 && <p>No notifications found.</p>}
        <ul>
          {notifications.map((notification) => (
            <li key={notification.id}>
              <strong>{notification.title ?? 'Update'}:</strong> {notification.message}
            </li>
          ))}
        </ul>
      </Card>

      <Card title='Bursary search'>
        <label className='auth-form'>
          Search bursaries
          <input
            type='text'
            value={bursaryQuery}
            onChange={(event) => setBursaryQuery(event.target.value)}
            placeholder='Type to search bursaries'
          />
        </label>
        {bursaryLoading && <p>Searching...</p>}
        {bursaryError && <p className='error-text'>{bursaryError}</p>}
        {!bursaryLoading && bursaryQuery && bursaryResults.length === 0 && <p>No bursaries found.</p>}
        <ul>
          {bursaryResults.map((bursary) => (
            <li key={bursary.id}>
              <strong>{bursary.title}</strong>
              {bursary.provider ? ` - ${bursary.provider}` : ''}
              {bursary.deadline ? ` (Deadline: ${bursary.deadline})` : ''}
            </li>
          ))}
        </ul>
      </Card>
    </div>
  );
};
