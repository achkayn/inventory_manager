import React, { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import Button from '../../components/Button';
import { FieldLabel, TextInput, SelectInput } from '../../components/Input';
import ErrorState from '../../components/ErrorState';
import { useAuth } from '../../context/AuthContext';

const RegisterPage = () => {
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    role: 'ROLE_USER',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await register(form);
      navigate('/login', { replace: true });
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 px-4 py-10 sm:px-6 lg:px-8">
      <div className="mx-auto grid min-h-[calc(100vh-5rem)] max-w-6xl overflow-hidden rounded-[2rem] bg-white shadow-soft lg:grid-cols-[0.95fr_1.05fr]">
        <div className="flex items-center justify-center bg-[radial-gradient(circle_at_bottom_left,_rgba(34,197,94,0.18),_transparent_40%),linear-gradient(135deg,_#020617_0%,_#111827_60%,_#0f172a_100%)] px-8 py-10 text-white sm:px-12">
          <div className="max-w-md">
            <p className="text-xs uppercase tracking-[0.32em] text-emerald-200">Create account</p>
            <h1 className="mt-5 text-4xl font-semibold leading-tight">
              Start managing inventory from a single, structured frontend.
            </h1>
            <p className="mt-5 text-sm leading-6 text-slate-300">
              Register once and move into the operational dashboard with products, suppliers, and
              order workflows ready to use.
            </p>
          </div>
        </div>

        <div className="flex items-center justify-center px-6 py-10 sm:px-10">
          <div className="w-full max-w-md">
            <div className="mb-8">
              <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                Join now
              </p>
              <h2 className="mt-2 text-3xl font-semibold text-slate-900">Register</h2>
              <p className="mt-2 text-sm text-slate-600">Create your inventory manager account.</p>
            </div>

            {error ? <ErrorState message={error} /> : null}

            <form onSubmit={handleSubmit} className="mt-6 space-y-5">
              <div>
                <FieldLabel htmlFor="name">Full name</FieldLabel>
                <TextInput
                  id="name"
                  value={form.name}
                  onChange={(event) => setForm({ ...form, name: event.target.value })}
                  required
                />
              </div>
              <div>
                <FieldLabel htmlFor="register-email">Email</FieldLabel>
                <TextInput
                  id="register-email"
                  type="email"
                  value={form.email}
                  onChange={(event) => setForm({ ...form, email: event.target.value })}
                  required
                />
              </div>
              <div>
                <FieldLabel htmlFor="register-password">Password</FieldLabel>
                <TextInput
                  id="register-password"
                  type="password"
                  value={form.password}
                  onChange={(event) => setForm({ ...form, password: event.target.value })}
                  required
                />
              </div>
              <div>
                <FieldLabel htmlFor="register-role">Role</FieldLabel>
                <SelectInput
                  id="register-role"
                  value={form.role}
                  onChange={(event) => setForm({ ...form, role: event.target.value })}
                >
                  <option value="ROLE_USER">Employee</option>
                  <option value="ROLE_ADMIN">Admin</option>
                </SelectInput>
              </div>
              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Creating account...' : 'Create account'}
              </Button>
            </form>

            <p className="mt-6 text-sm text-slate-600">
              Already have an account?{' '}
              <Link to="/login" className="font-semibold text-slate-900 hover:underline">
                Login
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
