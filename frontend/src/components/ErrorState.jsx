import React from 'react';
import Button from './Button';

const ErrorState = ({ message, onRetry }) => (
  <div className="border border-rose-200 bg-rose-50 px-5 py-4 text-rose-800">
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p className="font-semibold">Something went wrong</p>
        <p className="text-sm text-rose-700">{message}</p>
      </div>
      {onRetry ? (
        <Button variant="danger" onClick={onRetry}>
          Retry
        </Button>
      ) : null}
    </div>
  </div>
);

export default ErrorState;
