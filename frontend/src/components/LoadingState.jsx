import React from 'react';

const LoadingState = ({ label = 'Loading data...' }) => (
  <div className="flex min-h-[220px] items-center justify-center rounded-2xl border border-slate-200 bg-white">
    <div className="flex items-center gap-3 text-slate-600">
      <div className="h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-slate-900" />
      <span className="text-sm font-medium">{label}</span>
    </div>
  </div>
);

export default LoadingState;
