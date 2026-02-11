import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';

type SubscriptionPlan = {
  id: string | number;
  name: string;
  price?: number;
  description?: string;
};

type ActiveSubscription = {
  planId: string | number;
  planName?: string;
  status?: string;
};

export const CompanyPage = () => {
  const [plans, setPlans] = useState<SubscriptionPlan[]>([]);
  const [activePlan, setActivePlan] = useState<ActiveSubscription | null>(null);
  const [loadingPlans, setLoadingPlans] = useState(false);
  const [loadingError, setLoadingError] = useState<string | null>(null);
  const [subscribeLoading, setSubscribeLoading] = useState<string | number | null>(null);
  const [subscribeError, setSubscribeError] = useState<string | null>(null);
  const [subscribeSuccess, setSubscribeSuccess] = useState<string | null>(null);

  const normalizeError = (error: unknown, fallback: string): string => {
    if (axios.isAxiosError(error)) {
      return error.response?.data?.message ?? fallback;
    }

    return fallback;
  };

  const loadPlans = async () => {
    setLoadingPlans(true);
    setLoadingError(null);

    try {
      const response = await api.get<SubscriptionPlan[] | { data: SubscriptionPlan[]; activePlan?: ActiveSubscription }>(
        endpoints.subscriptionPlans,
      );

      if (Array.isArray(response.data)) {
        setPlans(response.data);
      } else {
        setPlans(response.data.data ?? []);
        setActivePlan(response.data.activePlan ?? null);
      }
    } catch (error) {
      setLoadingError(normalizeError(error, 'Could not load subscription plans.'));
    } finally {
      setLoadingPlans(false);
    }
  };

  const subscribe = async (planId: string | number) => {
    setSubscribeLoading(planId);
    setSubscribeError(null);
    setSubscribeSuccess(null);

    try {
      const response = await api.post<{ activePlan?: ActiveSubscription }>(endpoints.subscribe, { planId });
      const returnedPlan = response.data.activePlan;
      if (returnedPlan) {
        setActivePlan(returnedPlan);
      } else {
        const selectedPlan = plans.find((plan) => plan.id === planId);
        if (selectedPlan) {
          setActivePlan({ planId: selectedPlan.id, planName: selectedPlan.name, status: 'ACTIVE' });
        }
      }

      setSubscribeSuccess('Subscription activated successfully.');
    } catch (error) {
      setSubscribeError(normalizeError(error, 'Subscription failed.'));
    } finally {
      setSubscribeLoading(null);
    }
  };

  useEffect(() => {
    void loadPlans();
  }, []);

  return (
    <div className='grid'>
      <Card title='Subscription plans'>
        <button type='button' onClick={() => void loadPlans()} disabled={loadingPlans}>
          {loadingPlans ? 'Refreshing...' : 'Refresh plans'}
        </button>

        {loadingError && <p className='error-text'>{loadingError}</p>}
        {subscribeError && <p className='error-text'>{subscribeError}</p>}
        {subscribeSuccess && <p>{subscribeSuccess}</p>}

        {activePlan && (
          <p>
            Active plan: <strong>{activePlan.planName ?? activePlan.planId}</strong>{' '}
            {activePlan.status ? `(${activePlan.status})` : ''}
          </p>
        )}

        {!loadingPlans && plans.length === 0 && <p>No plans available.</p>}

        <ul>
          {plans.map((plan) => {
            const isActive = activePlan?.planId === plan.id;
            const isSubmitting = subscribeLoading === plan.id;

            return (
              <li key={plan.id}>
                <div>
                  <strong>{plan.name}</strong>
                  {typeof plan.price === 'number' ? ` - $${plan.price}` : ''}
                </div>
                {plan.description && <p>{plan.description}</p>}
                <button type='button' disabled={isSubmitting || isActive} onClick={() => void subscribe(plan.id)}>
                  {isActive ? 'Active' : isSubmitting ? 'Subscribing...' : 'Subscribe'}
                </button>
              </li>
            );
          })}
        </ul>
      </Card>

      <Card title='Company dashboard'>
        <p>Manage bursaries, discover students, and track engagement metrics.</p>
      </Card>
    </div>
  );
};
