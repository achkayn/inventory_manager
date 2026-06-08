import React from 'react';

const ArrowUpRightIcon = () => (
  <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 19.5l15-15m0 0H8.25m11.25 0v11.25" />
  </svg>
);

const TrendUpIcon = () => (
  <svg className="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 18L9 11.25l4.306 4.307a11.95 11.95 0 015.814-5.519l2.74-1.22m0 0l-5.94-2.28m5.94 2.28l-2.28 5.941" />
  </svg>
);

const WarningIcon = () => (
  <svg className="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
  </svg>
);

const StatCard = ({ label, value, featured = false, trend, helper, warn = false }) => {
  if (featured) {
    return (
      <div className="relative overflow-hidden bg-green-900 p-6 shadow-green-glow">
        <div className="pointer-events-none absolute -right-6 -top-6 h-28 w-28 rounded-full bg-green-700/30" />
        <div className="pointer-events-none absolute -bottom-4 right-12 h-20 w-20 rounded-full bg-white/5" />
        <div className="relative">
          <div className="flex items-start justify-between gap-4">
            <p className="text-sm font-semibold text-green-300">{label}</p>
            <div className="flex h-7 w-7 flex-shrink-0 items-center justify-center bg-white text-green-900 shadow-sm">
              <ArrowUpRightIcon />
            </div>
          </div>
          <p className="mt-5 text-5xl font-bold tracking-tight text-white">{value}</p>
          {trend && (
            <div className="mt-4 inline-flex items-center gap-1.5 bg-green-700/50 px-2.5 py-1">
              <TrendUpIcon />
              <span className="text-xs font-medium text-green-200">{trend}</span>
            </div>
          )}
          {helper && !trend && (
            <p className="mt-3 text-xs font-medium text-green-400">{helper}</p>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="relative overflow-hidden border border-gray-100 bg-white p-6 shadow-card">
      <div className="flex items-start justify-between gap-4">
        <p className="text-sm font-medium text-gray-500">{label}</p>
        <div className="flex h-7 w-7 flex-shrink-0 items-center justify-center border border-gray-200 text-gray-400">
          <ArrowUpRightIcon />
        </div>
      </div>
      <p className="mt-5 text-5xl font-bold tracking-tight text-gray-900">{value}</p>
      {trend && !warn && (
        <div className="mt-4 inline-flex items-center gap-1.5 bg-green-50 px-2.5 py-1">
          <span className="h-1.5 w-1.5 rounded-full bg-green-500" />
          <span className="text-xs font-medium text-green-700">{trend}</span>
        </div>
      )}
      {warn && value > 0 && (
        <div className="mt-4 inline-flex items-center gap-1.5 bg-amber-50 px-2.5 py-1">
          <WarningIcon />
          <span className="text-xs font-medium text-amber-700">{helper || 'Requires attention'}</span>
        </div>
      )}
      {warn && value === 0 && (
        <div className="mt-4 inline-flex items-center gap-1.5 bg-green-50 px-2.5 py-1">
          <span className="h-1.5 w-1.5 rounded-full bg-green-500" />
          <span className="text-xs font-medium text-green-700">All levels healthy</span>
        </div>
      )}
      {helper && !trend && !warn && (
        <p className="mt-3 text-xs font-medium text-gray-400">{helper}</p>
      )}
    </div>
  );
};

export default StatCard;
