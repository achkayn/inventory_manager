import React, { useEffect, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { getDashboardSummary } from '../../api/dashboard';
import { listProducts } from '../../api/products';
import ErrorState from '../../components/ErrorState';
import PageHeader from '../../components/PageHeader';
import StatCard from '../../components/StatCard';

const DashboardPage = () => {
  const [summary, setSummary] = useState(null);
  const [categoryStock, setCategoryStock] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [summaryData, productsData] = await Promise.all([
        getDashboardSummary(),
        listProducts(),
      ]);
      setSummary(summaryData);
      const grouped = productsData.reduce((acc, product) => {
        const key = product.categoryName || 'Uncategorized';
        const existing = acc[key] || { name: key, stockQty: 0 };
        existing.stockQty += Number(product.stockQty || 0);
        acc[key] = existing;
        return acc;
      }, {});
      setCategoryStock(Object.values(grouped));
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Unable to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (error) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  return (
    <div>
      <PageHeader
        title="Dashboard"
        description="A quick operational view of stock health, supplier count, and order pipeline."
      />

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {loading ? (
          <>
            {['', '', '', ''].map((item, index) => (
              <div
                key={index}
                className="h-28 animate-pulse rounded-3xl border border-slate-200 bg-white/80"
              />
            ))}
          </>
        ) : (
          <>
            <StatCard label="Total Products" value={summary?.totalProducts ?? 0} tone="slate" />
            <StatCard label="Low Stock Items" value={summary?.lowStockItems ?? 0} tone="amber" />
            <StatCard label="Pending Orders" value={summary?.pendingOrders ?? 0} tone="sky" />
            <StatCard label="Total Suppliers" value={summary?.totalSuppliers ?? 0} tone="emerald" />
          </>
        )}
      </div>

      <div className="mt-6 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="mb-5">
          <h3 className="text-lg font-semibold text-slate-900">Stock by category</h3>
          <p className="text-sm text-slate-500">Total units currently on hand per category.</p>
        </div>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={categoryStock} margin={{ top: 10, right: 10, left: -10, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="name" stroke="#64748b" tickLine={false} axisLine={false} />
              <YAxis stroke="#64748b" tickLine={false} axisLine={false} />
              <Tooltip
                contentStyle={{
                  borderRadius: '14px',
                  border: '1px solid #e2e8f0',
                  boxShadow: '0 10px 30px rgba(15,23,42,0.08)',
                }}
              />
              <Bar dataKey="stockQty" fill="#0f172a" radius={[10, 10, 0, 0]} barSize={36} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {loading ? <div className="mt-4 text-sm text-slate-500">Loading chart data...</div> : null}
    </div>
  );
};

export default DashboardPage;
