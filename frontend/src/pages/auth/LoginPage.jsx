import React, { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import Button from '../../components/Button';
import { FieldLabel, TextInput } from '../../components/Input';
import { useAuth } from '../../context/AuthContext';
import ErrorState from '../../components/ErrorState';

const LoginPage = () => {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: 'admin@demo.com', password: 'Admin123!' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) {
    return <Navigate to={location.state?.from?.pathname || '/'} replace />;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(form);
      navigate(location.state?.from?.pathname || '/', { replace: true });
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-950 px-4 py-10 sm:px-6 lg:px-8">
      <div className="mx-auto grid min-h-[calc(100vh-5rem)] max-w-6xl overflow-hidden bg-white shadow-soft lg:grid-cols-[1.15fr_0.85fr]">
        <div className="flex flex-col justify-between bg-[radial-gradient(circle_at_top_right,_rgba(14,165,233,0.24),_transparent_35%),linear-gradient(135deg,_#0f172a_0%,_#111827_55%,_#020617_100%)] px-8 py-10 text-white sm:px-12">
          <div>
            <p className="text-xs uppercase tracking-[0.32em] text-sky-200">Inventory platform</p>
            <h1 className="mt-5 max-w-xl text-4xl font-semibold leading-tight">
              Keep stock, suppliers, and orders in one clean control surface.
            </h1>
            <p className="mt-5 max-w-lg text-sm leading-6 text-gray-300">
              Sign in to manage products, track restocking, and act on low-stock alerts without
              leaving the dashboard.
            </p>
          </div>

          <div className="mt-10 grid gap-4 sm:grid-cols-3">
            {[
              ['Products', 'Fast CRUD'],
              ['Orders', 'Status workflow'],
              ['Alerts', 'Low stock focus'],
            ].map(([title, subtitle]) => (
              <div key={title} className="border border-white/10 bg-white/5 p-4 backdrop-blur">
                <p className="text-sm font-semibold">{title}</p>
                <p className="mt-1 text-xs text-gray-300">{subtitle}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="flex items-center justify-center px-6 py-10 sm:px-10">
          <div className="w-full max-w-md">
            <div className="mb-8">
              <p className="text-sm font-semibold uppercase tracking-[0.2em] text-gray-500">
                Welcome back
              </p>
              <h2 className="mt-2 text-3xl font-semibold text-gray-900">Login</h2>
              <p className="mt-2 text-sm text-gray-600">
                Use your account to access the inventory dashboard.
              </p>
            </div>

            {error ? <ErrorState message={error} /> : null}

            <form onSubmit={handleSubmit} className="mt-6 space-y-5">
              <div>
                <FieldLabel htmlFor="email">Email</FieldLabel>
                <TextInput
                  id="email"
                  type="email"
                  value={form.email}
                  onChange={(event) => setForm({ ...form, email: event.target.value })}
                  required
                />
              </div>
              <div>
                <FieldLabel htmlFor="password">Password</FieldLabel>
                <TextInput
                  id="password"
                  type="password"
                  value={form.password}
                  onChange={(event) => setForm({ ...form, password: event.target.value })}
                  required
                />
              </div>
              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Signing in...' : 'Sign in'}
              </Button>
            </form>

            <p className="mt-6 text-sm text-gray-600">
              Need an account?{' '}
              <Link to="/register" className="font-semibold text-gray-900 hover:underline">
                Register
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
