import React from 'react';

const LoadingState = ({ label = 'Loading data...' }) => (
  <div className="flex min-h-[220px] items-center justify-center border border-gray-200 bg-white">
    <div className="flex items-center gap-3 text-gray-600">
      <div className="h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-gray-900" />
      <span className="text-sm font-medium">{label}</span>
    </div>
  </div>
);

export default LoadingState;
