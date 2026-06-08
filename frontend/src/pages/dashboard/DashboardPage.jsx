import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { getDashboardCategoryStock, getDashboardSummary } from '../../api/dashboard';
import ErrorState from '../../components/ErrorState';
import StatCard from '../../components/StatCard';
import { useAuth } from '../../context/AuthContext';

const BAR_GREENS = ['#14532d', '#166534', '#15803d', '#16a34a', '#22c55e', '#4ade80', '#86efac'];

const TODAY = new Date().toLocaleDateString('en-US', {
  weekday: 'long',
  year: 'numeric',
  month: 'long',
  day: 'numeric',
});

const QUICK_LINKS = [
  { label: 'Products', path: '/products', icon: 'M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z' },
  { label: 'New Order', path: '/orders', icon: 'M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z' },
  { label: 'Suppliers', path: '/suppliers', icon: 'M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12' },
  { label: 'Categories', path: '/categories', icon: 'M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581c.699.699 1.78.872 2.607.33a18.095 18.095 0 005.223-5.223c.542-.827.369-1.908-.33-2.607L11.16 3.66A2.25 2.25 0 009.568 3z' },
];

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="border border-gray-100 bg-white px-4 py-3 shadow-xl">
      <p className="mb-0.5 text-xs font-semibold uppercase tracking-wider text-gray-400">{label}</p>
      <p className="text-2xl font-bold text-gray-900">{payload[0].value}</p>
      <p className="text-xs text-gray-400">units in stock</p>
    </div>
  );
};

const DashboardPage = () => {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [summary, setSummary] = useState(null);
  const [categoryStock, setCategoryStock] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [summaryData, categoryStockData] = await Promise.all([
        getDashboardSummary(),
        getDashboardCategoryStock(),
      ]);
      setSummary(summaryData);
      setCategoryStock(categoryStockData);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  if (error) return <ErrorState message={error} onRetry={loadData} />;

  const totalStock = categoryStock.reduce((s, c) => s + (c.stockQty ?? 0), 0);
  const lowStockCount = summary?.lowStockItems ?? 0;
  const totalProducts = summary?.totalProducts ?? 1;
  const healthPct = Math.max(0, Math.round(((totalProducts - lowStockCount) / Math.max(totalProducts, 1)) * 100));

  return (
    <div className="space-y-6">

      {/* ── Page header ── */}
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 sm:text-3xl">Dashboard</h1>
          <p className="mt-1 text-sm text-gray-500">
            {TODAY} &mdash; {isAdmin ? 'Full admin access' : 'Read-only access'}
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => navigate('/products')}
            className="inline-flex items-center gap-2 bg-green-700 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-green-800"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            Add Product
          </button>
          <button
            onClick={loadData}
            className="inline-flex items-center gap-2 border border-gray-200 bg-white px-4 py-2 text-sm font-semibold text-gray-700 shadow-sm transition hover:bg-gray-50"
          >
            <svg className="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
            </svg>
            Refresh
          </button>
        </div>
      </div>

      {/* ── Stat cards ── */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {loading ? (
          [0, 1, 2, 3].map((i) => (
            <div key={i} className={`h-[148px] animate-pulse ${i === 0 ? 'bg-green-200' : 'bg-gray-200'}`} />
          ))
        ) : (
          <>
            <StatCard
              label="Total Products"
              value={summary?.totalProducts ?? 0}
              featured
              trend="Live inventory count"
            />
            <StatCard
              label="Low Stock Items"
              value={summary?.lowStockItems ?? 0}
              warn
              helper="Requires attention"
            />
            <StatCard
              label="Pending Orders"
              value={summary?.pendingOrders ?? 0}
              trend="Awaiting fulfillment"
            />
            <StatCard
              label="Total Suppliers"
              value={summary?.totalSuppliers ?? 0}
              trend="Active partners"
            />
          </>
        )}
      </div>

      {/* ── Main content: chart + side panel ── */}
      <div className="grid gap-5 lg:grid-cols-[1fr_300px]">

        {/* Chart */}
        <div className="border border-gray-100 bg-white p-6 shadow-card">
          <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
            <div>
              <h3 className="text-base font-bold text-gray-900">Stock by Category</h3>
              <p className="mt-0.5 text-sm text-gray-500">Total units on hand per category</p>
            </div>
            {!loading && categoryStock.length > 0 && (
              <span className="inline-flex items-center gap-1.5 bg-green-50 px-3 py-1 text-xs font-semibold text-green-700">
                <span className="h-1.5 w-1.5 rounded-full bg-green-500" />
                {categoryStock.length} {categoryStock.length === 1 ? 'category' : 'categories'}
              </span>
            )}
          </div>

          {loading ? (
            <div className="h-72 animate-pulse bg-gray-100" />
          ) : categoryStock.length === 0 ? (
            <div className="flex h-72 flex-col items-center justify-center gap-3 text-gray-400">
              <svg className="h-10 w-10 opacity-30" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z" />
              </svg>
              <p className="text-sm font-medium">No category data yet</p>
            </div>
          ) : (
            <div className="h-72">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={categoryStock}
                  margin={{ top: 4, right: 8, left: -18, bottom: 0 }}
                  barCategoryGap="38%"
                >
                  <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
                  <XAxis
                    dataKey="categoryName"
                    stroke="#9ca3af"
                    tickLine={false}
                    axisLine={false}
                    tick={{ fontSize: 11, fontWeight: 500, fill: '#9ca3af' }}
                  />
                  <YAxis
                    stroke="#9ca3af"
                    tickLine={false}
                    axisLine={false}
                    tick={{ fontSize: 11, fill: '#9ca3af' }}
                  />
                  <Tooltip content={<CustomTooltip />} cursor={{ fill: '#f9fafb' }} />
                  <Bar dataKey="stockQty" radius={0} maxBarSize={52}>
                    {categoryStock.map((_, i) => (
                      <Cell key={i} fill={BAR_GREENS[i % BAR_GREENS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        {/* Right panel */}
        <div className="flex flex-col gap-5">

          {/* Stock health */}
          <div className="border border-gray-100 bg-white p-5 shadow-card">
            <div className="mb-4 flex items-center justify-between">
              <h4 className="text-sm font-bold text-gray-900">Stock Health</h4>
              <span className={`px-2.5 py-1 text-xs font-semibold ${
                healthPct >= 80 ? 'bg-green-50 text-green-700' : healthPct >= 50 ? 'bg-amber-50 text-amber-700' : 'bg-rose-50 text-rose-700'
              }`}>
                {healthPct}%
              </span>
            </div>

            {loading ? (
              <div className="h-2 animate-pulse bg-gray-100" />
            ) : (
              <>
                <div className="h-2 overflow-hidden bg-gray-100">
                  <div
                    className={`h-full transition-all duration-700 ${
                      healthPct >= 80 ? 'bg-green-500' : healthPct >= 50 ? 'bg-amber-400' : 'bg-rose-500'
                    }`}
                    style={{ width: `${healthPct}%` }}
                  />
                </div>
                <p className="mt-3 text-xs text-gray-500">
                  {`${totalProducts - lowStockCount} of ${totalProducts} products at healthy levels`}
                </p>
                {totalStock > 0 && (
                  <p className="mt-1 text-xs text-gray-400">
                    {totalStock.toLocaleString()} total units across {categoryStock.length} {categoryStock.length === 1 ? 'category' : 'categories'}
                  </p>
                )}
              </>
            )}

            {!loading && lowStockCount > 0 && (
              <div className="mt-4 flex items-start gap-2.5 bg-amber-50 p-3">
                <svg className="mt-px h-4 w-4 flex-shrink-0 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
                </svg>
                <div>
                  <p className="text-xs font-semibold text-amber-800">{lowStockCount} item{lowStockCount !== 1 ? 's' : ''} below threshold</p>
                  <p className="mt-0.5 text-xs text-amber-600">Review and restock soon</p>
                </div>
              </div>
            )}

            {!loading && lowStockCount === 0 && (
              <div className="mt-4 flex items-center gap-2 bg-green-50 p-3">
                <svg className="h-4 w-4 flex-shrink-0 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p className="text-xs font-semibold text-green-700">All stock levels are healthy</p>
              </div>
            )}

            <button
              onClick={() => navigate('/products')}
              className="mt-4 w-full bg-green-700 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-green-800"
            >
              View Products
            </button>
          </div>

          {/* Quick access */}
          <div className="border border-gray-100 bg-white p-5 shadow-card">
            <h4 className="mb-4 text-sm font-bold text-gray-900">Quick Access</h4>
            <div className="space-y-1">
              {QUICK_LINKS.map(({ label, path, icon }) => (
                <button
                  key={path}
                  onClick={() => navigate(path)}
                  className="group flex w-full items-center gap-3 px-3 py-2.5 text-sm font-medium text-gray-600 transition hover:bg-gray-50 hover:text-green-700"
                >
                  <span className="flex h-7 w-7 flex-shrink-0 items-center justify-center border border-gray-200 bg-white text-gray-500 shadow-sm transition group-hover:border-green-200 group-hover:bg-green-50 group-hover:text-green-600">
                    <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
                      <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
                    </svg>
                  </span>
                  {label}
                  <svg className="ml-auto h-3.5 w-3.5 opacity-0 transition group-hover:opacity-100" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                  </svg>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* ── Summary strip ── */}
      {!loading && (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
          {[
            { label: 'Products tracked', value: summary?.totalProducts ?? 0, icon: 'M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z' },
            { label: 'Categories', value: categoryStock.length, icon: 'M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581c.699.699 1.78.872 2.607.33a18.095 18.095 0 005.223-5.223c.542-.827.369-1.908-.33-2.607L11.16 3.66A2.25 2.25 0 009.568 3z' },
            { label: 'Suppliers', value: summary?.totalSuppliers ?? 0, icon: 'M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12' },
            { label: 'Total units', value: totalStock.toLocaleString(), icon: 'M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z' },
          ].map(({ label, value, icon }) => (
            <div key={label} className="flex items-center gap-3 border border-gray-100 bg-white px-4 py-4 shadow-card">
              <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center bg-green-50 text-green-700">
                <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
                  <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
                </svg>
              </div>
              <div className="min-w-0">
                <p className="text-lg font-bold text-gray-900 leading-tight">{value}</p>
                <p className="truncate text-xs text-gray-500">{label}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DashboardPage;
